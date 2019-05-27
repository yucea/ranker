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
	
	public JSONArray webCrawlerService(String crawlerUrl) throws Exception {
		
		Document doc = null;
        Document dtlDoc2 = null;
         
        try {
            doc = Jsoup.connect(crawlerUrl).get();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Elements element = doc.select(".ranking_list");
        
        JSONArray array = new JSONArray();
         
        for(Element el : element.select("div.ranking_thumb a")) {     
        	
            String dtlUrl = "https://news.naver.com" + el.attr("href").toString();
            
            try {
            	dtlDoc2 = Jsoup.connect(dtlUrl).get();
            } catch (IOException ex) {
            	ex.printStackTrace();
            }
            
            Elements dtlElement = dtlDoc2.select("div.content");
            
            // Title
            String title2 = dtlElement.select("div.article_info h3").text();
            
            // Contents
            String contents = dtlElement.select("div#articleBodyContents").text();
            
            JSONObject obj = new JSONObject();
            
            obj.put("Title", title2);
            obj.put("Contents", contents);
            
            array.put(obj);
        }
        
        return array;
		
	}
}
