package kr.co.esjee.ranker.recommend;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import kr.co.esjee.ranker.elasticsearch.ElasticOption;
import kr.co.esjee.ranker.elasticsearch.ElasticSearcher;
import kr.co.esjee.ranker.elasticsearch.TestElasticsearch;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestValidator extends TestElasticsearch {

	private Client client = null;
	private String indexName = "mv_basic_info";

	@Before
	public void init() throws UnknownHostException {
		client = super.client();
	}

	@After
	public void destroy() {
		client.close();
	}

	@Test
	public void testMovie() throws Exception {
		ElasticOption option = ElasticOption.newInstance().queryBuilder(QueryBuilders.matchAllQuery()).page(1, ES_MAXSIZE);

		SearchHits hits = ElasticSearcher.search(client, indexName, option);

		log.info("total : {}", hits.totalHits);

		Map<String, String> items = new TreeMap<>();
		hits.forEach(h -> {
			items.put(h.getId(), h.getSourceAsMap().get(TITLE).toString());
		});

		log.info("size : {}", items.size());

		SearchHits hits2 = ElasticSearcher.search(client, RECOMMEND_M2K, option);

		hits2.forEach(h -> {
			items.remove(h.getId());
		});

		items.forEach((k, v) -> {
			log.info("id : {}, title : {}", k, v);
		});

	}

	@Test
	public void testDuplicate() throws Exception {
		ElasticOption option = ElasticOption.newInstance().queryBuilder(QueryBuilders.matchAllQuery()).page(1, ES_MAXSIZE);
		SearchHits hits = ElasticSearcher.search(client, indexName, option);

		Map<String, List<String>> data = new HashMap<>();
		hits.forEach(h -> {
			Map<String, Object> source = h.getSourceAsMap();
			String tid = source.get(TID).toString();

			List<String> list = data.containsKey(tid) ? data.get(tid) : new ArrayList<>();
			list.add(source.get(ID).toString());
			data.put(tid, list);
		});

		Map<String, List<String>> newmap = data.entrySet().stream()
				.filter(x -> data.get(x.getKey()).size() > 1)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		newmap.forEach((k, v) -> {
			log.info("{} : {}", k, v);
		});
	}

}
