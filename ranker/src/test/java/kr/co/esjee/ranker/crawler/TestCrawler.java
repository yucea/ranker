package kr.co.esjee.ranker.crawler;

import java.io.IOException;
import java.text.ParseException;
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
		
		String url = "https://news.naver.com/main/ranking/popularDay.nhn?rankingType=popular_day";
		String[] urlParams = {"sectionId=[100,101,102]", "date=[20190608,20190609,20190610]"};		
		String idColumn = "aid";
		String dateColumn = "date";
		String categoryColumn = "rankingSectionId";
		String listAtrb = "div.ranking";
		String listEachAtrb = "div.ranking_text";
		String titleAtrb = "h3#articleTitle";
		String contentAtrb = "div#articleBodyContents";
		
		try {
			Crawler crawler = new Crawler();
			
			List<Article> articleList = crawler.execute(url, urlParams, idColumn, dateColumn, categoryColumn, 
					listAtrb, listEachAtrb, titleAtrb, contentAtrb);
			
			for (Article article : articleList) {
				articleService.save(article);
			}
			
			log.info("Save count = {}", articleList.size());			
			
		} catch (IOException e) {
			log.error("Error = {}", e.getLocalizedMessage());
		} catch (ParseException e) {
			log.error("Error = {}", e.getLocalizedMessage());
		}
	}
	
	@Test
	public void testGetUrl() {
		String url = "https://news.naver.com/main/ranking/popularDay.nhn";
		String[] urlParams = {"date=20190504~20190606", "temp=temp"};
		
		try {
			Crawler crawler = new Crawler();
			
			List<String> returnUrlList = crawler.getUrl(url, urlParams, "date");
			
			for(String returnUrl : returnUrlList) {
				System.out.println(returnUrl);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
}