package kr.co.esjee.ranker.elasticsearch;

import java.net.UnknownHostException;
import java.util.List;

import org.elasticsearch.client.Client;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import kr.co.esjee.ranker.elasticsearch.ElasticAnalyzer.Result;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestElasticAnalyzer extends TestElasticsearch {

	private Client client = null;
	private String indexName = "sample";

	@Before
	public void init() throws UnknownHostException {
		client = super.client();
	}

	@After
	public void destroy() {
		client.close();
	}

	@Test
	public void testAnalyze() throws Exception {
		String text = "광화문행 인천공항버스 디지털 미디어시티 소울소사이어티 Kubernetes kuberNETes 쿠버네티스 마이크로 서비스와 cloud 서버에 저장. 우리 오늘저녁에 구디에서 만나자. ㅅㅌㅊ과 ㅎㅌㅊ 상타침과 하타침, 밸로시 랩터와 티라노 사우르스는 육식 공룡이다. 미세먼지가 없는 하늘이 오랜만인다.";
		String analyzer = "korean";

		List<Result> results = ElasticAnalyzer.simple(client, indexName, analyzer, text);
		for (Result result : results) {
			log.info("{}, {}", result.getTerm(), result.getCount());
		}

	}

}
