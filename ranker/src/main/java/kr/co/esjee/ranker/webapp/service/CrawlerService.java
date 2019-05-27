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
	 * Web Crawler Service
	 * 
	 * @param crawlerUrl
	 * @param listEl
	 * @param listDtlEl
	 * @param titleEl
	 * @param contentsEl
	 * @return JSONArray
	 * @throws IOException 
	 * @throws Exception
	 */
	public JSONArray webCrawlerService(String crawlerUrl, String listEl, String listDtlEl, String titleEl, String contentEl) throws IOException {
		
		Document listDoc = null;		
        Document dtlDoc = null;
        
        JSONArray rtnJsonArray = new JSONArray();         
        
    	listDoc = Jsoup.connect(crawlerUrl).get();
    	
    	// 목록 Element 
    	Elements listElement = listDoc.select(listEl);
         
        // 목록 Element 에서 상세 정보 추출
        for(Element dtlEl : listElement.select(listDtlEl)) {     
        	
        	// 상세 정보 링크
            String dtlUrl = dtlEl.select("a").attr("abs:href").toString();
            
            dtlDoc = Jsoup.connect(dtlUrl).get();
            
            // 상세 정보 Body
            Elements dtlElement = dtlDoc.select("body");
            
            // Title
            String title = dtlElement.select(titleEl).text();
            
            // Contents
            String content = dtlElement.select(contentEl).text();
            
            JSONObject obj = new JSONObject();
            
            obj.put("content", content);
            obj.put("title", title);
            
            rtnJsonArray.put(obj);                
        }
        
        return rtnJsonArray;		
	}
}
