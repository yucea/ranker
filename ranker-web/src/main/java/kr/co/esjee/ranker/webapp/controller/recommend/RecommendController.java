package kr.co.esjee.ranker.webapp.controller.recommend;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.esjee.ranker.webapp.controller.AppController;
import kr.co.esjee.ranker.webapp.model.K2MNode;
import kr.co.esjee.ranker.webapp.model.M2KNode;
import kr.co.esjee.ranker.webapp.model.M2MNode;
import kr.co.esjee.ranker.webapp.service.RecommendService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/rest/recommend")
@Slf4j
public class RecommendController extends AppController {

	@Autowired
	private RecommendService service;

	@RequestMapping(value = "/movies", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String movies(
			@RequestParam int draw,
			@RequestParam int start,
			@RequestParam int length,
			@RequestParam("search[value]") String searchKey,
			@RequestParam Map<String, String> formData) {
		JSONObject result = new JSONObject();

		try {
			long _s = System.currentTimeMillis();

			Page<M2KNode> page = null;
			if (StringUtils.isEmpty(searchKey)) {
				page = service.findAllOfM2K(super.getPageable(start, length, Sort.by(Direction.DESC, TID)));
			} else {
				page = service.findByTitleLikeOfM2K(searchKey, super.getPageable(start, length, Sort.by(Direction.DESC, TID)));
			}

			log.info("list - start : {}, length : {}, totalCount : {}, totalPage : {}, actual time : {}", start, length, page.getTotalElements(), page.getTotalPages(), System.currentTimeMillis() - _s);

			result.put(DATA, page.getContent());
			result.put(DRAW, draw);
			result.put(RECORDS_TOTAL, page.getTotalElements());
			result.put(RECORDS_FILTERED, page.getTotalElements());
		} catch (Exception e) {
			e.printStackTrace();
			result.put(SUCCESS, false);
			result.put(MESSAGE, e.getLocalizedMessage());
		}

		return result.toString();
	}

	@RequestMapping(value = "/m2k/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String info(@PathVariable long id, Model model) {
		JSONObject result = new JSONObject();

		try {
			M2KNode node = service.findByIdOfM2K(id);

			result.put(SUCCESS, true);
			result.put(DATA, new JSONObject(node));
		} catch (Exception e) {
			e.printStackTrace();
			result.put(SUCCESS, false);
			result.put(MESSAGE, e.getLocalizedMessage());
		}

		return result.toString();
	}

	@RequestMapping(value = "/m2m/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String m2m(@PathVariable long id, Model model) {
		JSONObject result = new JSONObject();

		try {
			M2KNode info = service.findByIdOfM2K(id);
			M2MNode node = service.findByIdOfM2M(id);

			Map<String, Object> map = new HashMap<>();
			map.put(INFO, info);
			map.put(LIST, node);

			result.put(SUCCESS, true);
			result.put(DATA, map);
		} catch (Exception e) {
			e.printStackTrace();
			result.put(SUCCESS, false);
			result.put(MESSAGE, e.getLocalizedMessage());
		}

		return result.toString();
	}

	@RequestMapping(value = "/k2m/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String k2mList(
			@RequestParam int draw,
			@RequestParam int start,
			@RequestParam int length,
			@RequestParam("search[value]") String searchKey,
			@RequestParam Map<String, String> formData) {
		JSONObject result = new JSONObject();

		try {
			long _s = System.currentTimeMillis();

			Page<K2MNode> page = null;
			if (StringUtils.isEmpty(searchKey)) {
				page = service.findAllOfK2M(super.getPageable(start, length));
			} else {
				page = service.findByKeyLikeOfK2M(searchKey, super.getPageable(start, length));
			}

			log.info("list - start : {}, length : {}, totalCount : {}, totalPage : {}, actual time : {}", start, length, page.getTotalElements(), page.getTotalPages(), System.currentTimeMillis() - _s);

			result.put(DATA, page.getContent());
			result.put(DRAW, draw);
			result.put(RECORDS_TOTAL, page.getTotalElements());
			result.put(RECORDS_FILTERED, page.getTotalElements());
		} catch (Exception e) {
			e.printStackTrace();
			result.put(SUCCESS, false);
			result.put(MESSAGE, e.getLocalizedMessage());
		}

		return result.toString();
	}

	@RequestMapping(value = "/k2m/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String k2m(@PathVariable long id, Model model) {
		JSONObject result = new JSONObject();

		try {
			K2MNode node = service.findByIdOfK2M(id);

			result.put(SUCCESS, true);
			result.put(DATA, new JSONObject(node));
		} catch (Exception e) {
			e.printStackTrace();
			result.put(SUCCESS, false);
			result.put(MESSAGE, e.getLocalizedMessage());
		}

		return result.toString();
	}

}
