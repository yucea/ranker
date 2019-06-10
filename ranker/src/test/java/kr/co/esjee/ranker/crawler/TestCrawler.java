package kr.co.esjee.ranker.crawler;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
	
	private static final String COMMA = ",";
	private static final String SEPARATOR = "=";	
	private static final String LEFT_ARRAY_SEPARATOR = "[";	
	private static final String RIGHT_ARRAY_SEPARATOR = "]";	
	private static final String QUESTION_SEPARATOR = "?";	
	private static final String PARAM_SEPARATOR = "&";
	
	@Autowired
	private ArticleService articleService;

	@Test
	public void testCrawler(){		
		
		String url = "https://news.naver.com/main/ranking/popularDay.nhn?rankingType=popular_day";
		String[] urlParams = {"sectionId=[100,101,102]", "date=20190101~20190607"};		
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
				log.info("{}", article.toString());
			}
			
			log.info("save count = {}", articleList.size());
			
			
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
			List<String> returnUrlList = getUrl(url, urlParams, "date");
			
			for(String returnUrl : returnUrlList) {
				System.out.println(returnUrl);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public List<String> getUrl(String url, String[] urlParams) {
		
		List<String> returnList = new ArrayList<String>();
		
		if(urlParams.length > 0) {
			List<String> paramList = Lists.newArrayList(urlParams);
			List<String> arrayList = new ArrayList<String>();
			
			for(String param : paramList) {
				if(param.contains(LEFT_ARRAY_SEPARATOR) && param.contains(RIGHT_ARRAY_SEPARATOR)) {
					arrayList.add(StringUtils.replace(StringUtils.replace(param, LEFT_ARRAY_SEPARATOR, ""), RIGHT_ARRAY_SEPARATOR, ""));
				} else {
					url += StringUtils.contains(url, QUESTION_SEPARATOR) ? PARAM_SEPARATOR + param : QUESTION_SEPARATOR + param;
				}
			}
			
			List<List<String>> combinationList = new ArrayList<List<String>>();
			
			if(!arrayList.isEmpty()) {
				for(int idx = 0; idx < arrayList.size(); idx++) {					
					String key = StringUtils.split(arrayList.get(idx), SEPARATOR)[0];
					String[] values = StringUtils.split(StringUtils.split(arrayList.get(idx), SEPARATOR)[1], COMMA);
					
					List<String> list = new ArrayList<String>();
					
					for(String value : values) {
						list.add(key + SEPARATOR + value);
					}
					
					combinationList.add(idx, list);
				}
				
				int[] indices = new int[combinationList.size()];
				
				int currentIndex = indices.length - 1;
				
				outerProcess: while (true) {
					
					String combinationUrl = "";
					
					for (int i = 0; i < combinationList.size(); i++) {
						combinationUrl += combinationList.get(i).get(indices[i]) + PARAM_SEPARATOR;
					}
						
					returnList.add(StringUtils.contains(url, QUESTION_SEPARATOR) ? 
							url + PARAM_SEPARATOR + StringUtils.substring(combinationUrl, 0, combinationUrl.length() - 1) : 
								url + QUESTION_SEPARATOR + StringUtils.substring(combinationUrl, 0, combinationUrl.length() - 1));
				
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
	
	public List<String> getUrl(String url, String[] urlParams, String dateColumn) throws ParseException {
		
		List<String> returnList = new ArrayList<String>();
		
		if(urlParams.length > 0) {
			List<String> paramList = Lists.newArrayList(urlParams);
			List<String> arrayList = new ArrayList<String>();
			
			for(String param : paramList) {
				if(param.contains(LEFT_ARRAY_SEPARATOR) && param.contains(RIGHT_ARRAY_SEPARATOR)) {
					arrayList.add(StringUtils.replace(StringUtils.replace(param, LEFT_ARRAY_SEPARATOR, ""), RIGHT_ARRAY_SEPARATOR, ""));
				} else {
					String key = StringUtils.split(param, SEPARATOR)[0];
					String[] values = StringUtils.split(StringUtils.split(param, SEPARATOR)[1], "~");
					
					if(StringUtils.equals(key, dateColumn) && values.length > 1){
						
						String inputStartDate = values[0];
						String inputEndDate = values[1];
						
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
						
						Date startDate = sdf.parse(inputStartDate);
						Date endDate = sdf.parse(inputEndDate);
						
						ArrayList<String> dates = new ArrayList<String>();
						Date currentDate = startDate;
						
						while (currentDate.compareTo(endDate) <= 0) {
							dates.add(sdf.format(currentDate));
							Calendar c = Calendar.getInstance();
							c.setTime(currentDate);
							c.add(Calendar.DAY_OF_MONTH, 1);
							currentDate = c.getTime();
						}
						
						String dateRange = "";
						
						for (String date : dates) {
							dateRange += date + COMMA;
						}
						
						arrayList.add(key + SEPARATOR + StringUtils.substring(dateRange, 0, dateRange.length() - 1));
						
						param = "";
					}
					
					url += StringUtils.contains(url, QUESTION_SEPARATOR) ? PARAM_SEPARATOR + param : QUESTION_SEPARATOR + param;
				}
			}
			
			List<List<String>> combinationList = new ArrayList<List<String>>();
			
			if(!arrayList.isEmpty()) {
				for(int idx = 0; idx < arrayList.size(); idx++) {					
					String key = StringUtils.split(arrayList.get(idx), SEPARATOR)[0];
					String[] values = StringUtils.split(StringUtils.split(arrayList.get(idx), SEPARATOR)[1], COMMA);
					
					List<String> list = new ArrayList<String>();
					
					for(String value : values) {
						list.add(key + SEPARATOR + value);
					}
					
					combinationList.add(idx, list);
				}
				
				int[] indices = new int[combinationList.size()];
				
				int currentIndex = indices.length - 1;
				
				outerProcess: while (true) {
					
					String combinationUrl = "";
					
					for (int i = 0; i < combinationList.size(); i++) {
						combinationUrl += combinationList.get(i).get(indices[i]) + PARAM_SEPARATOR;
					}
						
					returnList.add(StringUtils.contains(url, QUESTION_SEPARATOR) ? 
							url + PARAM_SEPARATOR + StringUtils.substring(combinationUrl, 0, combinationUrl.length() - 1) : 
								url + QUESTION_SEPARATOR + StringUtils.substring(combinationUrl, 0, combinationUrl.length() - 1));
				
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