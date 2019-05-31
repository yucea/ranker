package kr.co.esjee.ranker.elasticsearch;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import kr.co.esjee.ranker.text.Wordrank;
import kr.co.esjee.ranker.text.Wordrank.Word;
import kr.co.esjee.ranker.webapp.model.Article;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestTermvectors extends TestElasticsearch {

	private Client client = null;
	private String indexName = "article";
	private String typeName = DOC;
	private static final String TITLE = "title";
	private static final String CONTENT = "content";

	@Before
	public void init() throws UnknownHostException {
		client = super.client();
	}

	@After
	public void destroy() {
		client.close();
	}

	@Test
	public void termvectors() throws Exception {
		ElasticOption option = ElasticOption.newInstance()
				.queryBuilder(QueryBuilders.matchAllQuery())
				.page(0, 1);

		SearchHits hits = ElasticSearcher.search(client, indexName, option);
		for (SearchHit hit : hits) {
			Map<String, Double> map = ElasticSearcher.termvectors(client, indexName, typeName, hit.getId(), "title", "content");
			log.info("{}", map);
		}
	}

	@Test
	public void multiTermvectors() throws Exception {
		ElasticOption option = ElasticOption.newInstance()
				.queryBuilder(QueryBuilders.matchAllQuery())
				.page(0, 1000); 

		SearchHits hits = ElasticSearcher.search(client, indexName, option);

		List<String> source = new ArrayList<>();
		for (SearchHit hit : hits) {
			source.add(hit.getSourceAsMap().get(TITLE).toString());
			source.add(hit.getSourceAsMap().get(CONTENT).toString());
		}

		String id = "-1";
		Article article = new Article();
		article.setId(Long.parseLong(id));
		article.setTitle("_");
		article.setContent(StringUtils.join(source, " "));

		ElasticSearcher.create(client, indexName, typeName, id, article.toString());

		Map<String, Double> data = ElasticSearcher.termvectors(client, indexName, typeName, id, CONTENT);
		data.forEach((k, v) -> {
			log.info("{} : {}", k, v);
		});

		ElasticSearcher.delete(client, indexName, typeName, id);
	}

	@Test
	public void delete() throws Exception {
		String id = "-1";
		ElasticSearcher.delete(client, indexName, typeName, id);
	}

	@Test
	public void wordrank() throws Exception {
		List<String> docs = new ArrayList<>();

		ElasticOption option = ElasticOption.newInstance()
				.queryBuilder(QueryBuilders.matchAllQuery())
				.page(0, 1000);

		SearchHits hits = ElasticSearcher.search(client, indexName, option);
		for (SearchHit hit : hits) {
			Map<String, Object> map = hit.getSourceAsMap();
			docs.add(map.get(TITLE).toString());
			docs.add(map.get(CONTENT).toString());
		}

		Wordrank wordrank = new Wordrank(10, 10);
		List<Word> list = wordrank.execute(docs);
		for (Word word : list) {
			log.info("{}", word.toString());
		}
	}
}
