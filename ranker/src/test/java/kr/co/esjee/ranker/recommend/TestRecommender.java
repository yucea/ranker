package kr.co.esjee.ranker.recommend;

import java.net.UnknownHostException;
import java.util.List;

import org.elasticsearch.client.Client;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import kr.co.esjee.ranker.config.AppConfig;
import kr.co.esjee.ranker.elasticsearch.TestElasticsearch;
import kr.co.esjee.ranker.webapp.model.recommend.M2KNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestRecommender extends TestElasticsearch {

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
	public void m2kExecute() throws Exception {
		List<M2KNode> nodes = M2KExecutor.execute(client, indexName, typeName, AppConfig.getStopwords());

		for (int i = 0; i < nodes.size(); i++) {
			M2KNode n = nodes.get(i);
			log.info("{} - {}, {}, {}", i, n.getId(), n.getTitle(), n.getKeywords());
		}
	}

}
