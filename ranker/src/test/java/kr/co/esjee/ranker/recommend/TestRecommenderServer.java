package kr.co.esjee.ranker.recommend;

import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import kr.co.esjee.ranker.elasticsearch.TestElasticsearch;
import kr.co.esjee.ranker.webapp.service.RecommendService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TestRecommenderServer extends TestElasticsearch {

	@Autowired
	private RecommendService service;

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
	public void m2kRunner() throws Exception {
		Recommender.m2kRunner(service, client, indexName, typeName);
	}

}
