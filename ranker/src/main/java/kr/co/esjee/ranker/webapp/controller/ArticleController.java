package kr.co.esjee.ranker.webapp.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import kr.co.esjee.ranker.webapp.model.Article;
import kr.co.esjee.ranker.webapp.service.ArticleService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/rest/article")
@Api(value = "Article")
@Slf4j
public class ArticleController extends AppController {

	@Autowired
	private ArticleService service;

	@ApiOperation(value = "list")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNo", value = "페이지 번호", required = true, dataType = "int", paramType = "path", defaultValue = "1"),
			@ApiImplicitParam(name = "size", value = "리스트 사이즈", required = true, dataType = "int", paramType = "path", defaultValue = "10")
	})
	@RequestMapping(value = "/list/{pageNo}/{size}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String list(@PathVariable(required = true) int pageNo, @PathVariable(required = true) int size) throws JSONException {
		JSONObject result = new JSONObject();

		try {
			long _s = System.currentTimeMillis();

			Page<Article> page = service.findAll(super.getPageable(pageNo, size));

			log.info("list - pageNo : {}, size : {}, count : {}, totalCount : {}, actual time : {}", pageNo, size, page.getSize(), page.getTotalElements(), System.currentTimeMillis() - _s);

			result.put(SUCCESS, true);
			result.put(TOTAL_COUNT, page.getTotalElements());
			result.put(TOTAL_PAGES, page.getTotalPages());
			result.put(DATA, page.getContent());
		} catch (Exception e) {
			e.printStackTrace();
			result.put(SUCCESS, false);
			result.put(MESSAGE, e.getLocalizedMessage());
		}

		return result.toString();
	}

}
