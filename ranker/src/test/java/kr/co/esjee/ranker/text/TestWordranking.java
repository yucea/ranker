package kr.co.esjee.ranker.text;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import kr.co.esjee.ranker.config.AppConfig;
import kr.co.esjee.ranker.elasticsearch.ElasticOption;
import kr.co.esjee.ranker.elasticsearch.ElasticSearcher;
import kr.co.esjee.ranker.elasticsearch.TestElasticsearch;
import kr.co.esjee.ranker.text.Wordrank.Word;
import kr.co.esjee.ranker.webapp.model.Article;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestWordranking extends TestElasticsearch {

	private static final String TITLE = "title";
	private static final String CONTENT = "content";
	private Client client = null;
	private String indexName = "article";
	private String typeName = DOC;
	private int minCount = 30;
	private int maxLength = 10;

	@Before
	public void init() throws UnknownHostException {
		client = super.client();
	}

	@After
	public void destroy() {
		client.close();
	}

	@Data
	@AllArgsConstructor
	class WordScore {
		private String key;
		private int currentCount;
		private int beforCount;
		private int count;
	}

	@Test
	public void ranking() throws Exception {
		List<Word> w1 = this.getWordrank(this.getArticle(-1, "20190609").getContent());
		Map<String, Word> map = w1.stream().collect(Collectors.toMap(
				x -> x.getKey(),
				x -> x));

		List<Word> w2 = this.getWordrank(this.getArticle(-1, "20190610").getContent());

		Map<Integer, List<WordScore>> tree = new TreeMap<>(Collections.reverseOrder());
		for (Word w : w2) {
			// log.info("{}", w);
			String key = w.getKey();
			int count = w.getCount() - (map.containsKey(key) ? map.get(key).getCount() : 0);

			List<WordScore> list = tree.containsKey(count) ? tree.get(count) : new ArrayList<WordScore>();
			list.add(new WordScore(key, w.getCount(), map.containsKey(key) ? map.get(key).getCount() : 0, count));

			tree.put(count, list);
		}

		tree.forEach((k, v) -> {
			for (WordScore w : v) {
				log.info("{} : {}, {}, {}, {}", k, w.getKey(), w.getCount(), w.getCurrentCount(), w.getBeforCount());
			}
		});
	}

	private Article getArticle(long id, String dateString) throws Exception {
		QueryBuilder query = QueryBuilders.termQuery("created", dateString);
		ElasticOption option = ElasticOption.newInstance()
				.queryBuilder(query)
				.page(0, 1000);

		SearchHits hits = ElasticSearcher.search(client, indexName, option);

		List<String> source = new ArrayList<>();
		for (SearchHit hit : hits) {
			source.add(hit.getSourceAsMap().get(TITLE).toString());
			source.add(hit.getSourceAsMap().get(CONTENT).toString());
		}

		Article article = new Article();
		article.setId(id);
		article.setTitle("_");
		article.setContent(StringUtils.join(source, " "));

		return article;
	}

	private List<Word> getWordrank(String text) throws IOException {
		Map<String, String> themes = new HashMap<>();
		themes.put("한국당", "자유한국당");
		themes.put("한국당은", "자유한국당");
		themes.put("한국당이", "자유한국당");
		themes.put("한국당의", "자유한국당");
		themes.put("한국당을", "자유한국당");
		themes.put("자유한국당은", "자유한국당");
		themes.put("자유한국당을", "자유한국당");
		themes.put("대변인은 ", "대변인");
		themes.put("대통령의", "대통령");
		themes.put("SK이", "SK이노베이션");
		themes.put("SK이노", "SK이노베이션");
		themes.put("SK이노베이션이", "SK이노베이션");
		themes.put("SK이노베이션은", "SK이노베이션");
		Wordrank wordrank = new Wordrank(minCount, maxLength, false);
		wordrank.setThemes(themes);
		wordrank.setStopwords(AppConfig.getStopwords());
		return wordrank.execute(text);
	}

}
