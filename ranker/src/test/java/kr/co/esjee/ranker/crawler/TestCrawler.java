package kr.co.esjee.ranker.crawler;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import kr.co.esjee.ranker.crawler.Crawler.CrawlerVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestCrawler {

	@Test
	public void testCrawler() {
		
		String url = "https://media.daum.net/ranking/popular";
		String listAtrb = "ul.list_news2";
		String listEachAtrb = "li";
		String titleAtrb = "h3.tit_view";
		String contentAtrb = "div.news_view";
		
		try {
			Crawler crawler = new Crawler();
			
			List<CrawlerVO> resultList = crawler.execute(url, listAtrb, listEachAtrb, titleAtrb, contentAtrb);
			
			for (CrawlerVO crawlerVO : resultList) {
				log.info("{}", crawlerVO.toString());
			}
			
		} catch (IOException e) {
			log.error("Error = {}", e.getLocalizedMessage());
		}
	}
}
