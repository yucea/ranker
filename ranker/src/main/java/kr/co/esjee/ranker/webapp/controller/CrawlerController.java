package kr.co.esjee.ranker.webapp.controller;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.netty.util.internal.StringUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import kr.co.esjee.ranker.webapp.service.CrawlerService;

@Controller
@RestController
@RequestMapping("/rest")
public class CrawlerController {	
	
	@Autowired
	private CrawlerService crawlerService;
	
	@ApiOperation(value = "WebCrawler")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "crawlerUrl", value = "crawlerUrl", required = false, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "listEl", value = "listEl", required = false, dataType = "string", paramType = "query") 
		})
	@RequestMapping(value = "/webCrawler", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String webCrawler(@RequestParam String crawlerUrl, @RequestParam String listEl){
		
		JSONArray returnArray = new JSONArray();
		
		try {
			
			if(StringUtil.isNullOrEmpty(crawlerUrl)) {
				crawlerUrl = "https://news.naver.com/main/ranking/popularDay.nhn?rankingType=popular_day&sectionId=101&date=20190524"; 
			}
			
			returnArray = crawlerService.webCrawlerService(crawlerUrl);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
        
        return returnArray.toString();
		
	}
}
