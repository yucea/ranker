package kr.co.esjee.ranker.webapp.controller;

import java.util.Map;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.esjee.ranker.elasticsearch.ElasticOption;
import kr.co.esjee.ranker.elasticsearch.ElasticSearcher;

@Controller
public class PathController extends AppController {

	@RequestMapping("/")
	public String path() {
		return "redirect:view/index.html";
	}

	@RequestMapping("/redirect}")
	public String redirect(@RequestParam String url) {
		return "redirect:" + url;
	}

	@RequestMapping(value = "/page", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String page(@RequestParam Map<String, String> params, Model model) {
		model.addAttribute("params", params);

		return params.get("url");
	}

	@RequestMapping(value = "/keyword/{key}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public String keyword(@PathVariable String key) {
		JSONObject result = new JSONObject();

		try {
			ElasticOption option = ElasticOption.newInstance()
					.queryBuilder(QueryBuilders.termQuery(KEY, key));
			SearchHits search = ElasticSearcher.search(super.getClient(), RECOMMEND_K2M, option);

			if (search.getTotalHits() > 0) {
				result.put(SUCCESS, true);
				result.put(ID, search.getHits()[0].getId());
			} else {
				result.put(SUCCESS, false);
				result.put(MESSAGE, "Item not found : " + key);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put(SUCCESS, false);
			result.put(MESSAGE, e.getLocalizedMessage());
		}

		return result.toString();
	}
	
	@RequestMapping(value = "/movie/{title}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public String movie(@PathVariable String title) {
		JSONObject result = new JSONObject();

		try {
			ElasticOption option = ElasticOption.newInstance()
					.queryBuilder(QueryBuilders.matchQuery(TITLE, title));
			SearchHits search = ElasticSearcher.search(super.getClient(), RECOMMEND_M2K, option);

			if (search.getTotalHits() > 0) {
				result.put(SUCCESS, true);
				result.put(ID, search.getHits()[0].getId());
			} else {
				result.put(SUCCESS, false);
				result.put(MESSAGE, "Item not found : " + title);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put(SUCCESS, false);
			result.put(MESSAGE, e.getLocalizedMessage());
		}

		return result.toString();
	}

}
