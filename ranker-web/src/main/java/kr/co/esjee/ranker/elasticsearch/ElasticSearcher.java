package kr.co.esjee.ranker.elasticsearch;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.delete.DeleteAction;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexAction;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
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

import kr.co.esjee.ranker.webapp.AppConstant;
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