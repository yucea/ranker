package kr.co.esjee.ranker.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Lists;

import kr.co.esjee.ranker.webapp.model.Article;

public class Crawler {
	
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
	public List<Article> execute(String url, String[] urlParams, String listAtrb,String listEachAtrb, String titleAtrb, String contentAtrb) throws IOException {
		
		List<Article> articleList = new ArrayList<Article>();
		
		List<String> paramList = Lists.newArrayList(urlParams);
		
		if(!paramList.isEmpty()) {
			for(String param : paramList) {
				url += "?" + param;
			}
		}
		
        Document listDoc = Jsoup.connect(url).get();
    	
    	Elements listElement = listDoc.select(listAtrb);
         
        for(Element dtlEl : listElement.select(listEachAtrb)) {
        
            String dtlUrl = dtlEl.select("a").attr("abs:href").toString();
            
            Document dtlDoc = Jsoup.connect(dtlUrl).get();
            
            Elements dtlElement = dtlDoc.select("body");
            
            String title = dtlElement.select(titleAtrb).text();            
            String content = dtlElement.select(contentAtrb).text();
            
            Article article = new Article();
            
            article.setTitle(title);
            article.setContent(content);
            
            articleList.add(article);                
        }
        
        return articleList;		
	}
}