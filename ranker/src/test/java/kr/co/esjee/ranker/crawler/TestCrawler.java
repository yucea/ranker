package kr.co.esjee.ranker.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.Lists;

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
		String[] urlParams = {"sectionId=[100,101]", "date=20190605"};		
		String idColumn = "aid";
		String dateColumn = "date";
		String categoryColumn = "rankingSectionId";
		String listAtrb = "div.ranking";
		String listEachAtrb = "div.ranking_text";
		String titleAtrb = "h3#articleTitle";
		String contentAtrb = "div#articleBodyContents";
		
		/*
		String url = "https://sports.news.naver.com/ranking/index.nhn";
		String[] urlParams = {"date=20190605"};		
		String idColumn = "aid";
		String dateColumn = "date";
		String categoryColumn = "rankingSectionId";
		String listAtrb = "div.news_list .ranking";
		String listEachAtrb = "div.text";
		String titleAtrb = "h4#title";
		String contentAtrb = "div#newsEndContents";
		*/
		
		try {
			Crawler crawler = new Crawler();
			
			List<Article> articleList = crawler.execute(url, urlParams, idColumn, dateColumn, categoryColumn, 
					listAtrb, listEachAtrb, titleAtrb, contentAtrb);
			
			for (Article article : articleList) {
				articleService.save(article);
				log.info("{}", article.toString());
			}
			
		} catch (IOException e) {
			log.error("Error = {}", e.getLocalizedMessage());
		}
	}
	
	@Test
	public void testGetUrl() {
		String url = "https://news.naver.com/main/ranking/popularDay.nhn?rankingType=popular_day";
		String[] urlParams = {"sectionId=[100,101]", "date=20190604"};
		
		List<String> returnUrlList = getUrl(url, urlParams);
		
		for(String returnUrl : returnUrlList) {
			System.out.println(returnUrl);
		}
	}
	
	public List<String> getUrl(String url, String[] urlParams) {	
		
		List<String> returnList = new ArrayList<String>();
		
		if(urlParams.length > 0) {			
			List<String> paramList = Lists.newArrayList(urlParams);
			List<String> arrayList = new ArrayList<String>();
			
			for(String param : paramList) {
				if(param.contains("[") && param.contains("]")) {
					arrayList.add(param.replace("[", "").replace("]", ""));
				} else {
					url += url.contains("?") ? "&" + param : "?" + param;
				}
			}
			
			List<List<String>> combinationList = new ArrayList<List<String>>();
			
			if(!arrayList.isEmpty()) {				
				for(int idx = 0; idx < arrayList.size(); idx++) {					
					String key = arrayList.get(idx).split("=")[0];
					String[] values = arrayList.get(idx).split("=")[1].split(",");
					
					List<String> list = new ArrayList<String>();
					
					for(String value : values) {
						list.add(key + "=" + value);
					}
					
					combinationList.add(idx, list);
				}
				
				int[] indices = new int[combinationList.size()];
				
				int currentIndex = indices.length - 1;
				
				outerProcess: while (true) {
					String combinationUrl = "";
					
					for (int i = 0; i < combinationList.size(); i++) {
						combinationUrl += combinationList.get(i).get(indices[i]) + "&";
					}
					
					if(url.contains("?")) {
						returnList.add(url + "&" + combinationUrl.substring(0, combinationUrl.length() - 1));
					} else {
						returnList.add(url + "?" + combinationUrl.substring(0, combinationUrl.length() - 1));
					}
				
					while (true) {						
						indices[currentIndex]++;
				       
						if (indices[currentIndex] >= combinationList.get(currentIndex).size()) {		    	   
							for (int j = currentIndex; j < indices.length; j++) {
								indices[j] = 0;
							}
							
							currentIndex--;		           
						} else {
							while (currentIndex < indices.length - 1) {
								currentIndex++;
							}	
							
							break;
						}
						
						if (currentIndex == -1) {
							break outerProcess;
						}
					}
				}			
			} else {
				returnList.add(url);
			}
		} else {
			returnList.add(url);
		}
		
		return returnList;
	}
}