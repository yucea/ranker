package kr.co.esjee.ranker.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import lombok.AllArgsConstructor;
import lombok.Data;

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
	public List<CrawlerVO> execute(String url, String listAtrb, String listEachAtrb, String titleAtrb, String contentAtrb) throws IOException {
		
		List<CrawlerVO> resultList = new ArrayList<CrawlerVO>();
		
        Document listDoc = Jsoup.connect(url).get();
    	
    	Elements listElement = listDoc.select(listAtrb);
         
        for(Element dtlEl : listElement.select(listEachAtrb)) {
        
            String dtlUrl = dtlEl.select("a").attr("abs:href").toString();
            
            Document dtlDoc = Jsoup.connect(dtlUrl).get();
            
            Elements dtlElement = dtlDoc.select("body");
            
            String title = dtlElement.select(titleAtrb).text();            
            String content = dtlElement.select(contentAtrb).text();
            
            CrawlerVO crawlerVO = new CrawlerVO(title, content);
            
            resultList.add(crawlerVO);                
        }
        
        return resultList;		
	}
	
	@Data
	@AllArgsConstructor
	public class CrawlerVO {
		private String title;
		private String content;
	}
}