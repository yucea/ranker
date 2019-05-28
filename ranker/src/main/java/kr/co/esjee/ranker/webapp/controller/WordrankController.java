package kr.co.esjee.ranker.webapp.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import kr.co.esjee.ranker.text.Wordrank.Word;
import kr.co.esjee.ranker.webapp.AppConstant;
import kr.co.esjee.ranker.webapp.service.WordrankService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/rest/wordrank")
@Api(value = "Wordrank")
@Slf4j
public class WordrankController implements AppConstant {

	@Autowired
	private WordrankService wordrankService;

	@ApiOperation(value = "Wordrank")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "docs", value = "Documents", required = true, dataType = "string", paramType = "query", allowMultiple = true),
			@ApiImplicitParam(name = "minCount", value = "최소 중복횟수", required = true, dataType = "int", paramType = "query", defaultValue = "5"),
			@ApiImplicitParam(name = "maxLength", value = "최대 단어길이", required = true, dataType = "int", paramType = "query", defaultValue = "10"),
			@ApiImplicitParam(name = "exclude", value = "제외단어", required = false, dataType = "string", paramType = "query", defaultValue = ""),
			@ApiImplicitParam(name = "match", value = "제외단어 일치여부<br/>일치 : true<br/>포함 : false", required = false, dataType = "boolean", paramType = "query", defaultValue = "true")
	})
	@RequestMapping(value = "/execute", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String list(HttpServletRequest request,
			@RequestParam String[] docs,
			@RequestParam(defaultValue = "5") int minCount,
			@RequestParam(defaultValue = "10") int maxLength,
			@RequestParam(defaultValue = "") String exclude,
			@RequestParam(defaultValue = "true") boolean match) throws JSONException {
		JSONObject result = new JSONObject();

		try {
			long start = System.currentTimeMillis();

			List<Word> data = wordrankService.execute(docs, minCount, maxLength, exclude, match);

			log.info("IP : {}, doc size : {}, actual time : {}(ms)", request.getRemoteAddr(), StringUtils.length(StringUtils.join(docs)), System.currentTimeMillis() - start);

			result.put(SUCCESS, true);
			result.put(TOTAL_COUNT, data.size());
			result.put(RESULT, data);
		} catch (Exception e) {
			log.warn(e.getLocalizedMessage());

			result.put(SUCCESS, false);
			result.put(ERROR, e.getLocalizedMessage());
		}

		return result.toString();
	}

}