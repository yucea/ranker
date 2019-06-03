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
		
		
		String url = "https://news.naver.com/main/ranking/popularDay.nhn?rankingType=popular_day&sectionId=101&date=20190603";
		String[] urlParams = {};		
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
				
				/*
				if(articleService.existsById(article.getId())) {
					System.out.println("Update");
				} else {
					System.out.println("Insert");
				}
				*/
				
				articleService.save(article);
				log.info("{}", article.toString());
			}
			
		} catch (IOException e) {
			log.error("Error = {}", e.getLocalizedMessage());
		}
	}
}