package kr.co.esjee.ranker.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.BoostAttribute;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.action.delete.DeleteAction;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexAction;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.termvectors.MultiTermVectorsAction;
import org.elasticsearch.action.termvectors.MultiTermVectorsItemResponse;
import org.elasticsearch.action.termvectors.MultiTermVectorsRequestBuilder;
import org.elasticsearch.action.termvectors.MultiTermVectorsResponse;
import org.elasticsearch.action.termvectors.TermVectorsAction;
import org.elasticsearch.action.termvectors.TermVectorsRequest;
import org.elasticsearch.action.termvectors.TermVectorsRequest.FilterSettings;
import org.elasticsearch.action.termvectors.TermVectorsRequestBuilder;
import org.elasticsearch.action.termvectors.TermVectorsResponse;
import org.elasticsearch.action.update.UpdateAction;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;

import kr.co.esjee.ranker.util.RecommendUtil;
import kr.co.esjee.ranker.webapp.AppConstant;
import kr.co.esjee.ranker.webapp.model.recommend.Node;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ElasticSearcher implements AppConstant {

	public static SearchHits search(Client client, String indexName, ElasticOption option) throws Exception {
		long start = System.currentTimeMillis();

		SearchRequestBuilder builder = new SearchRequestBuilder(client, SearchAction.INSTANCE);
		builder.setIndices(indexName);

		option.fulfill(builder);

		log.info("query : {}", builder.toString());
		SearchResponse response = builder.execute().get();

		log.info("result count : {}, actual time : {}(ms)", response.getHits().totalHits, (System.currentTimeMillis() - start));

		return response.getHits();
	}

	public static long count(Client client, String indexName) {
		SearchRequestBuilder builder = new SearchRequestBuilder(client, SearchAction.INSTANCE);
		builder.setIndices(indexName);
		builder.setQuery(QueryBuilders.matchAllQuery());

		SearchResponse response = builder.get();
		return response.getHits().totalHits;
	}

	public static SearchHits fullSearch(Client client, String indexName, String searchKey, String[] fieldNames, ElasticOption option) throws Exception {
		if (option.getQueryBuilder() == null) {
			BoolQueryBuilder query = QueryBuilders.boolQuery();
			query.should(ElasticQuery.searchQuery(searchKey, fieldNames));
			query.should(ElasticQuery.matchQuery(searchKey, fieldNames));
			query.should(ElasticQuery.wildcardQuery(searchKey, fieldNames));
			option.setQueryBuilder(query);
		}

		return search(client, indexName, option);
	}

	public static SearchHit findById(Client client, String indexName, long id) throws Exception {
		SearchHits hits = search(client, indexName, ElasticOption.newInstance().queryBuilder(ElasticQuery.termQuery(ID, id)).sortSkip());
		if (hits.totalHits > 0) {
			return hits.getHits()[0];
		} else {
			throw new IllegalArgumentException(String.format("Item not found : %s", id));
		}
	}

	public static Map<String, Double> termvectors(Client client, String indexName, String typeName, String id, String... fields) throws IOException {
		TermVectorsRequestBuilder builder = new TermVectorsRequestBuilder(client, TermVectorsAction.INSTANCE, indexName, typeName, id);
		builder.setSelectedFields(fields);
		builder.setOffsets(false);
		builder.setPositions(false);
		builder.setTermStatistics(true);
		builder.setFieldStatistics(true);
		builder.setFilterSettings(new FilterSettings());
		builder.setPayloads(false);

		TermVectorsResponse response = builder.get();

		Map<Integer, List<String>> dataMap = new TreeMap<Integer, List<String>>(Collections.reverseOrder());

		for (String field : fields) {
			Terms terms = response.getFields().terms(field);
			TermsEnum termsEnum = terms.iterator();

			BytesRef term = null;
			while ((term = termsEnum.next()) != null) { // terms
				// int docFreq = termsEnum.docFreq(); // doc_freq
				// long totalTermFreq = termsEnum.totalTermFreq(); // ttf

				PostingsEnum postings = termsEnum.postings(null, PostingsEnum.ALL);
				int termFreq = postings.freq(); // term_freq

				// BoostAttribute boostAttribute = termsEnum.attributes().addAttribute(BoostAttribute.class);
				// float score = boostAttribute.getBoost(); // score
				String word = term.utf8ToString();

				// log.info("{}, {}, {}, {}, {}, {}", field, response.getId(), word, termFreq, terms.getDocCount(), terms.getSumTotalTermFreq());

				if (word.length() < 2)
					continue;

				List<String> list = dataMap.containsKey(termFreq) ? dataMap.get(termFreq) : new ArrayList<String>();
				list.add(term.utf8ToString());

				dataMap.put(termFreq, list);
			}
		}

		Map<String, Double> data = new LinkedHashMap<String, Double>();
		dataMap.forEach((k, v) -> {
			v.forEach(s -> {
				if (!data.containsKey(s))
					data.put(s, (double) k);
			});
		});

		Map<String, Double> result = RecommendUtil.normalize(data);

		// result.forEach((k, v) -> log.info("{} : {}", k, v));

		return result;
	}

	public static Map<String, List<TermResult>> multiTermvectors(Client client, String indexName, String typeName, Map<String, Node> sources) throws Exception {
		MultiTermVectorsRequestBuilder builder = new MultiTermVectorsRequestBuilder(client, MultiTermVectorsAction.INSTANCE);
		for (String id : sources.keySet()) {
			builder.add(termVectorsRequest(indexName, typeName, id, sources.get(id).getFields()));
		}

		MultiTermVectorsResponse responses = builder.get();

		Map<String, List<TermResult>> result = new HashMap<>();

		for (MultiTermVectorsItemResponse response : responses) {
			if (response.isFailed()) {
				response.getFailure().getCause().printStackTrace();
				continue;
			}

			TermVectorsResponse res = response.getResponse();
			if (!res.isExists())
				continue;

			String id = res.getId();
			Iterator<String> iter = res.getFields().iterator();
			while (iter.hasNext()) {
				String field = iter.next();
				Terms terms = res.getFields().terms(field);
				TermsEnum termsEnum = terms.iterator();

				BytesRef term = null;
				while ((term = termsEnum.next()) != null) { // terms
					int docFreq = termsEnum.docFreq(); // doc_freq
					if (docFreq == -1)
						continue;

					BoostAttribute boostAttribute = termsEnum.attributes().addAttribute(BoostAttribute.class);
					double score = boostAttribute.getBoost(); // score
					String word = term.utf8ToString();

					if (word.length() > 1) { // 단일문자 제외
						List<TermResult> list = result.containsKey(id) ? result.get(id) : new ArrayList<>();
						list.add(new TermResult(id, field, word.toUpperCase(), score * (RecommendUtil.isFieldValueString(sources.get(id), word) ? 0.001 : 1), score));

						result.put(id, list);
					}
				}
			}
		}

		return result;
	}

	private static TermVectorsRequest termVectorsRequest(String index, String type, String id, String... fields) {
		return new TermVectorsRequest(index, type, id)
				.selectedFields(fields)
				.offsets(false)
				.positions(false)
				.payloads(false)
				.termStatistics(true)
				.fieldStatistics(true)
				.filterSettings(new FilterSettings());
	}

	public static SuggestResult suggest(Client client, String indexName, String searchKey, String fieldName, ElasticOption option) throws Exception {
		HighlightBuilder highlightBuilder = option.getHighlightBuilder();
		String preTag = "";
		String postTag = "";
		if (highlightBuilder != null) {
			preTag = StringUtils.join(highlightBuilder.preTags());
			postTag = StringUtils.join(highlightBuilder.postTags());

			option.setHighlightBuilder(null);
		}

		BoolQueryBuilder query = QueryBuilders.boolQuery();
		query.should(ElasticQuery.matchQuery(searchKey, fieldName).boost(3));
		query.should(ElasticQuery.wildcardPostQuery(searchKey, fieldName).boost(2));
		query.should(ElasticQuery.wildcardQuery(searchKey, fieldName).boost(1));

		option.setQueryBuilder(query);

		if (option.getSortBuilders().isEmpty()) {
			option.sort(SortBuilders.scoreSort());
			option.sort(SortBuilders.fieldSort(fieldName).order(SortOrder.ASC));
		}

		SearchHits hits = search(client, indexName, option);

		// List<SuggestResult> result = new ArrayList<>();

		SuggestResult result = new SuggestResult();
		result.setTotalCount(hits.totalHits);

		for (SearchHit hit : hits) {
			String keyword = hit.getSourceAsMap().get(fieldName).toString();
			String highlight = highlightBuilder == null ? null : StringUtils.replace(keyword, searchKey, String.format("%s%s%s", preTag, searchKey, postTag));

			result.add(keyword, hit.getScore(), highlight);
		}

		return result;
	}

	public static SearchHits suggest(Client client, String indexName, String typeName, String suggestName, String field, String text) throws Exception {
		QueryBuilder query = QueryBuilders.matchQuery(field, text);

		SuggestBuilder suggestBuilder = new SuggestBuilder()
				.addSuggestion(suggestName, SuggestBuilders.completionSuggestion(field).text(text));

		SearchRequestBuilder builder = client.prepareSearch(indexName)
				.setTypes(typeName)
				.setQuery(query)
				.suggest(suggestBuilder);

		log.info("query : {}", builder.toString());

		SearchResponse response = builder.execute().get();

		return response.getHits();
	}

	public static IndexResponse create(Client client, String indexName, String typeName, String id, String jsonString) throws Exception {
		IndexRequestBuilder builder = new IndexRequestBuilder(client, IndexAction.INSTANCE, indexName);
		builder.setType(typeName);
		builder.setId(id);
		builder.setSource(jsonString, XContentType.JSON);

		IndexResponse response = builder.execute().get();

		log.info("{}", response.getResult());

		return response;
	}

	public static void update(Client client, String indexName, String typeName, String id, String jsonString) throws Exception {
		UpdateRequest request = new UpdateRequest(indexName, typeName, id);
		request.doc(jsonString, XContentType.JSON);

		UpdateRequestBuilder builder = new UpdateRequestBuilder(client, UpdateAction.INSTANCE, indexName, typeName, id);
		builder.setDoc(request);

		UpdateResponse response = builder.execute().get();

		log.info("{}", response.getResult());
	}

	public static void delete(Client client, String indexName, String typeName, String id) throws Exception {
		DeleteRequestBuilder builder = new DeleteRequestBuilder(client, DeleteAction.INSTANCE, indexName);
		builder.setType(typeName);
		builder.setId(id);

		DeleteResponse response = builder.execute().get();

		log.info("{}", response.getResult());
	}

}
