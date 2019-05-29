package kr.co.esjee.ranker.webapp.service;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class CrawlerService {
	
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
	public JSONArray execute(String url, String listAtrb, String listEachAtrb, String titleAtrb, String contentAtrb) throws IOException {
		
		Document listDoc = null;		
        
        
        JSONArray rtnJsonArray = new JSONArray();         
        
    	listDoc = Jsoup.connect(url).get();
    	
    	// 목록 Element 
    	Elements listElement = listDoc.select(listAtrb);
         
        // 목록 Element 에서 상세 정보 추출
        for(Element dtlEl : listElement.select(listEachAtrb)) {
        	
        	Document dtlDoc = null;
        	
        	// 상세 정보 링크
            String dtlUrl = dtlEl.select("a").attr("abs:href").toString();
            
            dtlDoc = Jsoup.connect(dtlUrl).get();
            
            // 상세 정보 Body
            Elements dtlElement = dtlDoc.select("body");
            
            // Title
            String title = dtlElement.select(titleAtrb).text();
            
            // Contents
            String content = dtlElement.select(contentAtrb).text();
            
            JSONObject obj = new JSONObject();
            
            obj.put("content", content);
            obj.put("title", title);
            
            rtnJsonArray.put(obj);                
        }
        
        return rtnJsonArray;		
	}
}
