package kr.co.esjee.ranker.webapp.controller;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import kr.co.esjee.ranker.webapp.AppConstant;
import kr.co.esjee.ranker.webapp.service.CrawlerService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RestController
@RequestMapping("/rest")
@Slf4j
public class CrawlerController implements AppConstant {
	
	@Autowired
	private CrawlerService crawlerService;
	
	/**
	 * Web Crawler
	 * 
	 * @param crawlerUrl
	 * @param listEl
	 * @param listDtlEl
	 * @param titleEl
	 * @param contentsEl
	 * @return json
	 */
	@ApiOperation(value = "WebCrawler")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "crawlerUrl", value = "주소", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "listEl", value = "목록 속성", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "listDtlEl", value = "목록 상세 속성", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "titleEl", value = "제목 속성", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "contentEl", value = "내용 속성", required = true, dataType = "string", paramType = "query")
		})
	@RequestMapping(value = "/webCrawler", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String webCrawler(@RequestParam String crawlerUrl, @RequestParam String listEl, @RequestParam String listDtlEl,
			@RequestParam String titleEl, @RequestParam String contentEl) {
				
		JSONObject returnObj = new JSONObject();
		
		try {			
			JSONArray returnArray = new JSONArray();
			returnArray = crawlerService.webCrawlerService(crawlerUrl, listEl, listDtlEl, titleEl, contentEl);
			
			returnObj.put(SUCCESS, true);
			returnObj.put(TOTAL_COUNT, returnArray.length());
			returnObj.put(RESULT, returnArray);
						
		} catch (IOException e) {			
			
			log.error("error = {}", e.getLocalizedMessage());

			returnObj.put(SUCCESS, false);
			returnObj.put(ERROR, e.getLocalizedMessage());
		} 
        
        return returnObj.toString();		
	}
}
