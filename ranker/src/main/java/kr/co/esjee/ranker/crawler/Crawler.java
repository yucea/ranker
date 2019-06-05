package kr.co.esjee.ranker.crawler;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Lists;

import kr.co.esjee.ranker.webapp.model.Article;
import kr.co.esjee.ranker.webapp.model.Schedule;

public class Crawler {

	/**
	 * Crawler Scheduler Execute
	 * 
	 * @param schedule
	 * @return
	 * @throws IOException
	 */
	public List<Article> execute(Schedule schedule) throws IOException {
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
	 * @param listAtrb
	 * @param listEachAtrb
	 * @param titleAtrb
	 * @param contentAtrb
	 * @return
	 * @throws IOException
	 */
	public List<Article> execute(String url, String[] urlParams, String idColumn, String dateColumn,
			String categoryColumn, String listAtrb, String listEachAtrb, String titleAtrb, String contentAtrb)
			throws IOException {

		List<Article> articleList = new ArrayList<Article>();
		
		List<String> returnUrlList = getUrl(url, urlParams);
		
		for(String returnUrl : returnUrlList) {
			
			// List URL Connect
			Document listDoc = Jsoup.connect(returnUrl).get();

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

	/**
	 * Get Query Map
	 * 
	 * @param query
	 * @return
	 */
	public static Map<String, String> getQueryMap(String query) {

		if (query == null)
			return null;

		int pos = query.indexOf("?");

		if (pos >= 0) {
			query = query.substring(pos + 1);
		}

		String[] params = query.split("&");

		Map<String, String> map = new HashMap<String, String>();

		for (String param : params) {
			String name = param.split("=")[0];
			String value = param.split("=")[1];
			map.put(name, value);
		}

		return map;
	}
}