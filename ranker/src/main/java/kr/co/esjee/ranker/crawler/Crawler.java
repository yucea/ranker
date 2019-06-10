package kr.co.esjee.ranker.crawler;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Lists;

import kr.co.esjee.ranker.webapp.model.Article;
import kr.co.esjee.ranker.webapp.model.Schedule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Crawler {
	
	private static final String COMMA = ",";
	
	private static final String SEPARATOR = "=";
	
	private static final String LEFT_ARRAY_SEPARATOR = "[";	
	
	private static final String RIGHT_ARRAY_SEPARATOR = "]";	
	
	private static final String QUESTION_SEPARATOR = "?";	
	    
	private static final String PARAM_SEPARATOR = "&";

	/**
	 * Crawler Scheduler Execute
	 * 
	 * @param schedule
	 * @return
	 * @throws IOException
	 * @throws ParseException 
	 */
	public List<Article> execute(Schedule schedule) throws IOException, ParseException {
		return execute(
				schedule.getUrl(), 
				schedule.getUrlParams(), 
				schedule.getIdColumn(), 
				schedule.getDateColumn(),
				schedule.getCategoryColumn(), 
				schedule.getListAtrb(), 
				schedule.getListEachAtrb(),
				schedule.getTitleAtrb(), 
				schedule.getContentAtrb()
				);
	}

	/**
	 * Crawler Execute
	 * 
	 * @param url
	 * @param urlParams
	 * @param idColumn
	 * @param dateColumn
	 * @param categoryColumn
	 * @param listAtrb
	 * @param listEachAtrb
	 * @param titleAtrb
	 * @param contentAtrb
	 * @return
	 * @throws IOException
	 * @throws ParseException 
	 */
	public List<Article> execute(String url, String[] urlParams, String idColumn, String dateColumn,
			String categoryColumn, String listAtrb, String listEachAtrb, String titleAtrb, String contentAtrb)
			throws IOException, ParseException {

		List<Article> articleList = new ArrayList<Article>();
		
		List<String> returnUrlList = getUrl(url, urlParams, dateColumn);
		
		log.info("###### Crawler site count = {} ######", returnUrlList.size());
		
		int siteCnt = 1;
		
		for(String returnUrl : returnUrlList) {
			
			// List URL Connect
			Document listDoc = Jsoup.connect(returnUrl)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:65.0) Gecko/20100101 Firefox/65.0")
					.get();

			Elements listElement = listDoc.select(listAtrb);

			for (Element eachAtrb : listElement.select(listEachAtrb)) {

				// Detail URL Connect
				Document dtlDoc = Jsoup.connect(eachAtrb.select("a").attr("abs:href").toString()).get();

				// Body Element
				Elements dtlElement = dtlDoc.select("body");

				Article article = new Article();
				article.setTitle(dtlElement.select(titleAtrb).text());
				article.setContent(dtlElement.select(contentAtrb).text());

				// URL Parameter Parsing
				URL aURL = new URL(eachAtrb.select("a").attr("abs:href").toString());
				
				Map<String, String> map = getQueryMap(aURL.getQuery());

				if (map != null) {
					if(map.get(categoryColumn) != null) {
						article.setCategory(map.get(categoryColumn).toString());
					}
					
					if(map.get(idColumn) != null) {
						article.setId(Long.parseLong(map.get(idColumn).toString()));
					}
					
					if(map.get(dateColumn) != null) {
						article.setCreated(map.get(dateColumn).toString());
					}
				}

				articleList.add(article);
			}
			
			try {
				log.info("###### Crawler site loading count = {} ######", siteCnt);
				Thread.sleep(5000);
				siteCnt++;
			} catch (InterruptedException e) {
				log.error("sleep error = {}", e.getLocalizedMessage());
			}
		}		

		return articleList;

	}
	
	/**
	 * Get URL List
	 * 
	 * @param url
	 * @param urlParams
	 * @return
	 */
	private List<String> getUrl(String url, String[] urlParams) {
		
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
					String[] values = StringUtils.split(StringUtils.split(arrayList.get(idx), SEPARATOR)[1], ",");
					
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

	/**
	 * Get Query Map
	 * 
	 * @param query
	 * @return
	 */
	private static Map<String, String> getQueryMap(String query) {

		if (query == null) return null;

		int pos = query.indexOf(QUESTION_SEPARATOR);

		if (pos >= 0) {
			query = query.substring(pos + 1);
		}

		String[] params = StringUtils.split(query, PARAM_SEPARATOR);

		Map<String, String> map = new HashMap<String, String>();

		for (String param : params) {				
			String name = StringUtils.split(param, SEPARATOR)[0];
			String value = StringUtils.split(param, SEPARATOR)[1];
			map.put(name, value);
		}

		return map;
	}
}