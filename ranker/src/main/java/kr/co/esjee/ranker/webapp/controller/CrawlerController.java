package kr.co.esjee.ranker.webapp.controller;

import java.io.IOException;
import java.util.List;

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
import kr.co.esjee.ranker.crawler.Crawler.CrawlerVO;
import kr.co.esjee.ranker.webapp.AppConstant;
import kr.co.esjee.ranker.webapp.service.CrawlerService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RestController
@RequestMapping("/rest/crawler")
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
	@ApiOperation(value = "Crawler")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "url", value = "주소", required = true, dataType = "string", paramType = "query", example = "1234fgg"),
		@ApiImplicitParam(name = "listAtrb", value = "목록 속성", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "listEachAtrb", value = "목록 각각의 속성", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "titleAtrb", value = "제목 속성", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "contentAtrb", value = "내용 속성", required = true, dataType = "string", paramType = "query")
		})
	@RequestMapping(value = "/execute", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String execute(@RequestParam String url, @RequestParam String listAtrb, @RequestParam String listEachAtrb,
			@RequestParam String titleAtrb, @RequestParam String contentAtrb) {
				
		JSONObject returnObj = new JSONObject();
		
		try {
			List<CrawlerVO> resultList = crawlerService.execute(url, listAtrb, listEachAtrb, titleAtrb, contentAtrb);
			
			returnObj.put(SUCCESS, true);
			returnObj.put(TOTAL_COUNT, resultList.size());
			returnObj.put(RESULT, resultList);
						
		} catch (IOException e) {						
			log.error("error = {}", e.getLocalizedMessage());

			returnObj.put(SUCCESS, false);
			returnObj.put(ERROR, e.getLocalizedMessage());
		} 
        
        return returnObj.toString();		
	}
}
