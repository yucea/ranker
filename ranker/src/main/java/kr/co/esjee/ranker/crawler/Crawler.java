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

		// User Parameter Add
		List<String> paramList = Lists.newArrayList(urlParams);
		if (!paramList.isEmpty()) {
			for (String param : paramList) {
				url += "?" + param;
			}
		}

		// List URL Connect
		Document listDoc = Jsoup.connect(url).get();

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
				article.setCategory(map.get(categoryColumn).toString());
				article.setId(Long.parseLong(map.get(idColumn).toString()));
				article.setCreated(map.get(dateColumn).toString());
			}

			articleList.add(article);
		}

		return articleList;

	}

	/**
	 * URL Parameter Parsing
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