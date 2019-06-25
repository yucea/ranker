package kr.co.esjee.ranker.elasticsearch;

import java.net.UnknownHostException;
import java.util.List;

import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse.AnalyzeToken;
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
	public void testSimple() throws Exception {
		String text = "광화문행 인천공항버스 디지털 미디어시티 소울소사이어티 Kubernetes kuberNETes 쿠버네티스 마이크로 서비스와 cloud 서버에 저장. 우리 오늘저녁에 구디에서 만나자. ㅅㅌㅊ과 ㅎㅌㅊ 상타침과 하타침, 밸로시 랩터와 티라노 사우르스는 육식 공룡이다. 미세먼지가 없는 하늘이 오랜만인다.";
		String analyzer = "korean";

		List<Result> results = ElasticAnalyzer.simple(client, indexName, analyzer, text);
		for (Result result : results) {
			log.info("{}, {}", result.getTerm(), result.getCount());
		}
	}

	@Test
	public void testAnalyze() throws Exception {
		String text = "실패투성이 소년 찰리 브라운과 최고의 친구 스누피! “네가 있어 참 다행이야” 수줍음 많고 매사에 용기 없는 소년 ‘찰리 브라운’은 빨간 머리 소녀가 전학 오던 날, 그녀에게 첫 눈에 반하고 만다. 하지만 절친 스누피의 노력과는 달리 찰리는 자신의 마음을 고백할 기회를 번번이 놓치고 만다. “이번엔 꼭 달라질 거야! 나를 바꿔서 최고가 되겠어!” 한편 연말 댄스파티 소식이 전해지자 참견 잘하는 루시, 담요를 꼭 갖고 다니는 라이너스, 우등생 마시, 찰리의 동생 샐리 등 여러 친구들과 빨간 머리 소녀까지 각자 춤 솜씨를 뽐낼 기회를 갖게 된다. 찰리 브라운도 어렵게 용기를 내어 자신을 바꾸기로 하고 유명인사가 되는 기회까지 얻게 되는데… 과연 찰리 브라운은 용기를 얻고 그녀와 첫 사랑을 이룰 수 있을까? 2015년 12월 24일, 큰 꿈은 이루어진다!";
		String analyzer = "korean";

		List<AnalyzeToken> tokens = ElasticAnalyzer.analyze(client, indexName, analyzer, text);
		for (AnalyzeToken token : tokens) {
			log.info("{}, {}, {}", token.getTerm(), token.getType(), token.getPosition());
		}
	}

	@Test
	public void testExplain() throws Exception {
		String text = "실패투성이 소년 찰리 브라운과 최고의 친구 스누피! “네가 있어 참 다행이야” 수줍음 많고 매사에 용기 없는 소년 ‘찰리 브라운’은 빨간 머리 소녀가 전학 오던 날, 그녀에게 첫 눈에 반하고 만다. 하지만 절친 스누피의 노력과는 달리 찰리는 자신의 마음을 고백할 기회를 번번이 놓치고 만다. “이번엔 꼭 달라질 거야! 나를 바꿔서 최고가 되겠어!” 한편 연말 댄스파티 소식이 전해지자 참견 잘하는 루시, 담요를 꼭 갖고 다니는 라이너스, 우등생 마시, 찰리의 동생 샐리 등 여러 친구들과 빨간 머리 소녀까지 각자 춤 솜씨를 뽐낼 기회를 갖게 된다. 찰리 브라운도 어렵게 용기를 내어 자신을 바꾸기로 하고 유명인사가 되는 기회까지 얻게 되는데… 과연 찰리 브라운은 용기를 얻고 그녀와 첫 사랑을 이룰 수 있을까? 2015년 12월 24일, 큰 꿈은 이루어진다!";
		String analyzer = "korean";

		AnalyzeToken[] results = ElasticAnalyzer.explain(client, indexName, analyzer, text);
		for (AnalyzeToken token : results) {
			log.info("{}, {}, {}, {}", token.getTerm(), token.getType(), token.getPosition(), token.getAttributes());
		}
	}

}
