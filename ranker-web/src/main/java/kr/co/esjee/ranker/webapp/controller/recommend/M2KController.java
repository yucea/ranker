package kr.co.esjee.ranker.webapp.controller.recommend;

import java.util.Map;

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
import kr.co.esjee.ranker.webapp.model.M2KNode;
import kr.co.esjee.ranker.webapp.service.M2KService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/rest/recommend/m2k")
@Slf4j
public class M2KController extends AppController {

	@Autowired
	private M2KService service;

	@RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String list(
			@RequestParam int draw,
			@RequestParam int start,
			@RequestParam int length,
			@RequestParam("search[value]") String searchKey,
			@RequestParam Map<String, String> formData) {
		JSONObject result = new JSONObject();

		try {
			long _s = System.currentTimeMillis();

			Page<M2KNode> page = service.findAll(super.getPageable(start, length, Sort.by(Direction.DESC, TID)));

			log.info("list - start : {}, length : {}, count : {}, totalCount : {}, actual time : {}", start, length, page.getSize(), page.getTotalElements(), System.currentTimeMillis() - _s);

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

	@RequestMapping(value = "/info/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String info(@PathVariable long id, Model model) {
		M2KNode node = service.findById(id);
		
		model.addAttribute("data", node);

		return "redirect:/remote/recommend/m2k_modal.html";
	}
	
}
