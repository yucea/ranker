package kr.co.esjee.ranker.text;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse.AnalyzeToken;
import org.elasticsearch.client.Client;
import org.junit.After;
import org.junit.Test;

import com.google.common.collect.Lists;

import kr.co.esjee.ranker.elasticsearch.ElasticAnalyzer;
import kr.co.esjee.ranker.elasticsearch.TestElasticsearch;
import kr.co.esjee.ranker.text.Wordrank.Word;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestWordrank extends TestElasticsearch {

	private Client client = null;
	private String indexName = "sample";

	public Client getClient() throws UnknownHostException {
		if (client == null)
			client = super.client();

		return client;
	}

	@After
	public void destroy() {
		if (client != null)
			client.close();
	}

	@Test
	public void simple() throws Exception {
		String text = "소확행이란 '소소하지만 확실한 행복'의 축약어로, 일상에서 느낄 수 있는 작지만 확실하게 실현 가능한 행복이나 그러한 행복을 추구하는 삶의 경향을 뜻하는 신조어 및 유행어이다. 소확행은 한자로는 小確幸으로 표기한다";

		int minCount = 2;
		int maxLength = 10;
		Wordrank wordrank = new Wordrank(minCount, maxLength, true);
		// wordRank.setExclude("클라");
		List<Word> list = wordrank.execute(text);
		for (Word word : list) {
			log.info("{}", word.toString());
		}
	}

	@Test
	public void corpus() throws Exception {
		String text = "소확행이란 '소소하지만 확실한 행복'의 축약어로, 일상에서 느낄 수 있는 작지만 확실하게 실현 가능한 행복이나 그러한 행복을 추구하는 삶의 경향을 뜻하는 신조어 및 유행어이다. 소확행은 한자로는 小確幸으로 표기한다";

		int minCount = 1;
		int maxLength = 10;
		List<String> corpus = Lists.newArrayList("확실한 행복");

		Wordrank wordrank = new Wordrank(minCount, maxLength, true);
		wordrank.setCorpus(corpus);
		List<Word> list = wordrank.execute(text);
		for (Word word : list) {
			log.info("{}", word.toString());
		}
	}

	@Test
	public void theme() throws Exception {
		String text = "소확행이란 '소소하지만 확실한 행복'의 축약어로, 일상에서 느낄 수 있는 작지만 확실하게 실현 가능한 행복이나 그러한 행복을 추구하는 삶의 경향을 뜻하는 신조어 및 유행어이다. 소확행은 한자로는 小確幸으로 표기한다";

		int minCount = 1;
		int maxLength = 10;
		List<String> corpus = Lists.newArrayList("추구하는 삶");
		Map<String, String> themes = new HashMap<>();
		themes.put("실현 가능한", "실현가능");
		themes.put("소소하지만 확실한 행복", "소확행");

		Wordrank wordrank = new Wordrank(minCount, maxLength, true);
		wordrank.setThemes(themes);
		wordrank.setCorpus(corpus);

		List<Word> list = wordrank.execute(text);
		for (Word word : list) {
			log.info("{}", word.toString());
		}
	}

	@Test
	public void analyze() throws Exception {
		client = super.client();

		String text = "소확행이란 '소소하지만 확실한 행복'의 축약어로, 일상에서 느낄 수 있는 작지만 확실하게 실현 가능한 행복이나 그러한 행복을 추구하는 삶의 경향을 뜻하는 신조어 및 유행어이다. 소확행은 한자로는 小確幸으로 표기한다";
		// String text = "클라우드에서 클레오파트라가 클라리넷을 불며 클라이막스에 다다르고 있다 클레오파트라는 이집트의 왕비이다 클라리넷은 금관악기이다 클라스가 다르다 클라이엄프 클라이밍";
		String analyzer = "korean";

		List<String> corpus = ElasticAnalyzer.keyword(client, indexName, analyzer, text);

		corpus.forEach(k -> log.info("---> {}", k));

		int minCount = 2;
		int maxLength = 10;
		Wordrank wordrank = new Wordrank(minCount, maxLength, true);
		wordrank.setCorpus(corpus);

		List<Word> list = wordrank.execute(text);
		for (Word word : list) {
			log.info("===> {}", word.toString());
		}

		client.close();

	}

	@Test
	public void explain() throws Exception {
		String text = "클라우드에서 클레오파트라가 클라리넷을 불며 클라이막스에 다다르고 있다 클레오파트라는 이집트의 왕비이다 클라리넷은 금관악기이다 금관악기는 호른, 색소폰, 클라리넷, 트럼펫 등이 있다. 클라스가 다르다 클라이엄프 클라이밍";
		// String text = "소확행이란 '소소하지만 확실한 행복'의 축약어로, 일상에서 느낄 수 있는 작지만 확실하게 실현 가능한 행복이나 그러한 행복을 추구하는 삶의 경향을 뜻하는 신조어 및 유행어이다. 소확행은 한자로는 小確幸으로 표기한다";
		String analyzer = "korean";

		AnalyzeToken[] tokens = ElasticAnalyzer.explain(getClient(), indexName, analyzer, text);
		for (AnalyzeToken token : tokens) {
			log.info("{}, {}, {}", token.getTerm(), token.getType(), token.getAttributes());
		}
	}

}
