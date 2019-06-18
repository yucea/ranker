package kr.co.esjee.ranker.webapp.controller;

import javax.servlet.http.HttpServletRequest;

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
import kr.co.esjee.ranker.webapp.service.MovieCrawlerService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RestController
@RequestMapping("/rest/mvCrawler")
@Slf4j
public class MovieCrawlerController implements AppConstant {
	
	@Autowired
	private MovieCrawlerService movieCrawlerService;
	
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
	@ApiOperation(value = "mvCrawler")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "url", value = "주소", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "startYear", value = "시작연도", required = true, dataType = "int", paramType = "query"),
		@ApiImplicitParam(name = "endYear", value = "종료연도", required = true, dataType = "int", paramType = "query")
		})
	@RequestMapping(value = "/execute", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String mvExecute(HttpServletRequest request,
			@RequestParam String url,
			@RequestParam int startYear, 
			@RequestParam int endYear) {
				
		JSONObject returnObj = new JSONObject();
		
		try {
			
			movieCrawlerService.execute(url, startYear, endYear);
			returnObj.put(SUCCESS, true);
						
		} catch (Exception e) {
			log.error("Error = {}", e.getLocalizedMessage());

			returnObj.put(SUCCESS, false);
			returnObj.put(ERROR, e.getLocalizedMessage());
		}
		
        return returnObj.toString();		
	}
	
}