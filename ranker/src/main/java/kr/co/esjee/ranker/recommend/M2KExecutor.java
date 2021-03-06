package kr.co.esjee.ranker.recommend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;

import kr.co.esjee.ranker.elasticsearch.ElasticOption;
import kr.co.esjee.ranker.elasticsearch.ElasticSearcher;
import kr.co.esjee.ranker.elasticsearch.TermResult;
import kr.co.esjee.ranker.util.RecommendUtil;
import kr.co.esjee.ranker.webapp.AppConstant;
import kr.co.esjee.ranker.webapp.model.recommend.Keyword;
import kr.co.esjee.ranker.webapp.model.recommend.M2KNode;
import kr.co.esjee.ranker.webapp.model.recommend.Node;
import kr.co.esjee.ranker.webapp.service.RecommendService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class M2KExecutor implements AppConstant {

	public synchronized static int execute(RecommendService service, String indexName, String typeName, List<String> stopwords) throws Exception {
		service.m2kDeleteAll();
		
		List<M2KNode> nodes = execute(service.getClient(), indexName, typeName, stopwords);

		service.m2kSaveAll(nodes);

		return nodes.size();
	}

	public synchronized static List<M2KNode> execute(Client client, String indexName, String typeName, List<String> stopwords) throws Exception {
		// search
		SearchHits hits = search(client, indexName);
		// id, [감독, 배우, 배역, 제목]에 포함된 키워드는 다운그레이드 시킨다.
		Map<String, Node> sources = getSources(hits);

		Map<String, List<TermResult>> termvectors = ElasticSearcher.multiTermvectors(client, indexName, typeName, sources);

		List<M2KNode> nodes = new ArrayList<>();
		// sorting
		termvectors.forEach((id, terms) -> {
			Collections.sort(terms, new Comparator<TermResult>() {
				@Override
				public int compare(TermResult o1, TermResult o2) {
					Double s1 = o1.getScore();
					Double s2 = o2.getScore();
					return s2.compareTo(s1);
				}
			});

			Map<String, Keyword> map = new LinkedHashMap<>();

			double maxScore = getMaxScore(terms, stopwords);

			terms.forEach(t -> {
				String word = t.getWord().toUpperCase();
				if (stopwords.contains(word))
					return;

				double score = Math.round(t.getScore() / maxScore * NORMALIZING_SCORE * 100d) / 100d;
				// if (score < 0.1)
				// return;

				Keyword keyword = map.containsKey(word) ? map.get(word) : new Keyword(word, score);
				keyword.putSource(t.getField(), score);

				map.put(t.getWord(), keyword);
			});

			mergeKeyword(map);

			M2KNode node = (M2KNode) sources.get(id);
			node.setKeywords(map.values().toArray(new Keyword[0]));

			nodes.add(node);
		});

		return nodes;
	}

	private static double getMaxScore(List<TermResult> list, List<String> stopwords) {
		for (TermResult term : list) {
			if (!stopwords.contains(term.getWord().toUpperCase()))
				return term.getScore();
		}

		return list.get(0).getScore();
	}

	/**
	 * score가 동일하고 text를 포함할 경우 처리 [광화 : 5.23, 광화문 : 5.23 => 광화문 : 5.23]
	 */
	private static void mergeKeyword(Map<String, Keyword> map) {
		List<String> list = map.keySet().stream()
				.sorted(Comparator.reverseOrder())
				.collect(Collectors.toList());

		StringBuilder sb = new StringBuilder();
		for (String key : list) {
			Keyword keyword = map.get(key);
			String text = String.format("%.2f:%s", keyword.getScore(), keyword.getWord());
			if (sb.toString().contains(text)) {
				map.remove(key);
			} else {
				sb.append(text);
			}
		}
	}

	private static SearchHits search(Client client, String indexName) throws Exception {
		ElasticOption option = ElasticOption.newInstance()
				.queryBuilder(QueryBuilders.matchAllQuery())
				.page(1, ES_MAXSIZE);

		return ElasticSearcher.search(client, indexName, option);
	}

	private static Map<String, Node> getSources(SearchHits hits) {
		Map<String, Node> map = new HashMap<>();

		hits.forEach(item -> {
			Map<String, Object> source = item.getSourceAsMap();

			try {
				String id = source.get(ID).toString();

				List<String> fields = new ArrayList<>();
				if (StringUtils.isNotEmpty(source.get(SYNOPSIS).toString()))
					fields.add(SYNOPSIS);
				if (StringUtils.isNotEmpty(source.get(MAKING_NOTE).toString()))
					fields.add(MAKING_NOTE);

				map.put(id, new M2KNode(
						Long.parseLong(id),
						source.get(TID).toString(),
						source.get(TITLE).toString(),
						source.get(DIRECTOR).toString(),
						source.get(ACTOR).toString(),
						source.get(ROLE).toString(),
						Double.parseDouble(StringUtils.defaultIfEmpty(source.get(SCORE).toString(), "0")),
						source.get(GENRE).toString(),
						RecommendUtil.getRating(id, source.get(GRADE).toString()),
						source.get(OPENDAY).toString(),
						fields.toArray(new String[0]),
						null, null));
			} catch (Exception e) {
				log.error("{}", source);
				e.printStackTrace();
			}
		});

		return map;
	}

}
