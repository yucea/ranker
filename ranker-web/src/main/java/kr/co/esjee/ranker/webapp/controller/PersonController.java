package kr.co.esjee.ranker.webapp.controller;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.esjee.ranker.webapp.AppConstant;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/rest/person")
@Slf4j
public class PersonController implements AppConstant {

	@RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String list(
			@RequestParam int draw,
			@RequestParam int start,
			@RequestParam int length,
			@RequestParam("search[value]") String searchKey,
			@RequestParam Map<String, String> formData) {
		log.info("params : {}, {}, {}, {}, {}", draw, start, length, searchKey, formData);

		JSONObject result = new JSONObject();

		JSONArray data = new JSONArray();

		for (int i = 1; i <= length; i++) {
			JSONObject item = new JSONObject();
			item.put(ID, i + start);
			item.put("name", "name" + i);
			item.put("phone", "phone" + i);
			item.put("company", "company" + i);
			item.put("zip", "zip" + i);
			item.put("city", "city" + i);
			item.put("date", "date" + i);

			data.put(item);
			if (i + start >= 25)
				break;
		}

		result.put(DATA, data);
		result.put("draw", draw);
		result.put("recordsTotal", 26);
		result.put("recordsFiltered", 26);

		return result.toString();
	}

}
