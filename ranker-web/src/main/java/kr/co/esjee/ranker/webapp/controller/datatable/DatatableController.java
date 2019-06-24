package kr.co.esjee.ranker.webapp.controller.datatable;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.esjee.ranker.util.CalendarUtil;
import kr.co.esjee.ranker.webapp.AppConstant;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/rest/datatable")
@Slf4j
public class DatatableController implements AppConstant {

	@RequestMapping(value = "/sample", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String sample(
			@RequestParam int draw,
			@RequestParam int start,
			@RequestParam int length,
			@RequestParam("search[value]") String searchKey,
			@RequestParam Map<String, String> formData) {
		log.info("params : {}, {}, {}, {}, {}", draw, start, length, searchKey, formData);

		int totalCount = 32;

		JSONObject result = new JSONObject();

		JSONArray data = new JSONArray();

		for (int i = 1; i <= length; i++) {
			JSONObject item = new JSONObject();
			item.put("id", i + start);
			item.put("name", "name" + (i + start));
			item.put("phone", "phone" + (i + start));
			item.put("company", "company" + (i + start));
			item.put("zip", "zip" + (i + start));
			item.put("city", "city" + (i + start));
			item.put("date", CalendarUtil.getCurrentDate(CalendarUtil.DISPLAY_FORMAT));

			data.put(item);
			if (i + start >= totalCount)
				break;
		}

		result.put(DATA, data);
		result.put(DRAW, draw);
		result.put(RECORDS_TOTAL, totalCount);
		result.put(RECORDS_FILTERED, totalCount);

		return result.toString();
	}

}