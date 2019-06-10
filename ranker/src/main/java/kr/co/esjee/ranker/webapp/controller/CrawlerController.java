package kr.co.esjee.ranker.webapp.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

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
import kr.co.esjee.ranker.webapp.model.Article;
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
		@ApiImplicitParam(name = "url", value = "주소", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "urlParams", value = "파라미터", required = false, dataType = "string", paramType = "query", allowMultiple = true),
		@ApiImplicitParam(name = "idColumn", value = "아이디 컬럼", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "dateColumn", value = "날짜 컬럼", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "categoryColumn", value = "카테고리 컬럼", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "listAtrb", value = "목록 속성", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "listEachAtrb", value = "목록 각각의 속성", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "titleAtrb", value = "제목 속성", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "contentAtrb", value = "내용 속성", required = true, dataType = "string", paramType = "query")
		})
	@RequestMapping(value = "/execute", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String execute(HttpServletRequest request,
			@RequestParam String url, 
			@RequestParam(defaultValue = "") String[] urlParams,
			@RequestParam String idColumn, 
			@RequestParam String dateColumn,
			@RequestParam String categoryColumn,
			@RequestParam String listAtrb,
			@RequestParam String listEachAtrb,
			@RequestParam String titleAtrb, 
			@RequestParam String contentAtrb) {
				
		JSONObject returnObj = new JSONObject();
		
		try {
			List<Article> articleList = crawlerService.execute(url, urlParams, idColumn, dateColumn, categoryColumn, 
					listAtrb, listEachAtrb, titleAtrb, contentAtrb);
			
			returnObj.put(SUCCESS, true);
			returnObj.put(TOTAL_COUNT, articleList.size());
			returnObj.put(RESULT, articleList);
						
		} catch (IOException e) {						
			log.error("Error = {}", e.getLocalizedMessage());

			returnObj.put(SUCCESS, false);
			returnObj.put(ERROR, e.getLocalizedMessage());
		} catch (ParseException e) {
			log.error("Error = {}", e.getLocalizedMessage());

			returnObj.put(SUCCESS, false);
			returnObj.put(ERROR, e.getLocalizedMessage());
		} 
        
        return returnObj.toString();		
	}
}
