package kr.co.esjee.ranker.recommend;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.TreeMap;

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
	private String typeName = DOC;

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

		SearchHits hits2 = ElasticSearcher.search(client, MV_M2K, option);

		hits2.forEach(h -> {
			items.remove(h.getId());
		});

		items.forEach((k, v) -> {
			log.info("id : {}, title : {}", k, v);
		});

	}

}
