package kr.co.esjee.ranker.crawler;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import kr.co.esjee.ranker.webapp.model.Article;
import kr.co.esjee.ranker.webapp.service.ArticleService;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TestCrawler {
	
	@Autowired
	private ArticleService articleService;

	@Test
	public void testCrawler(){
		
		String url = "https://media.daum.net/ranking/popular";
		String[] urlParams = {"regDate=20190530"};
		String listAtrb = "ul.list_news2";
		String listEachAtrb = "li";
		String titleAtrb = "h3.tit_view";
		String contentAtrb = "div.news_view";
		
		try {
			Crawler crawler = new Crawler();
			
			List<Article> articleList = crawler.execute(url, urlParams, listAtrb, listEachAtrb, titleAtrb, contentAtrb);
			
			for (Article article : articleList) {
				article.setWriter("Test");
				article.setCreated("2019-05-30");
				articleService.save(article);
			}
		} catch (IOException e) {
			log.error("Error = {}", e.getLocalizedMessage());
		}
	}
}
