package kr.co.esjee.ranker.webapp.controller;

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

import kr.co.esjee.ranker.webapp.model.Person;
import kr.co.esjee.ranker.webapp.service.PersonService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/rest/person")
@Slf4j
public class PersonController extends AppController {
	
	@Autowired
	private PersonService personService;

	@RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String list(
			@RequestParam int draw,
			@RequestParam int start,
			@RequestParam int length,
			@RequestParam("search[value]") String searchKey,
			@RequestParam Map<String, String> formData) {
		
		JSONObject result = new JSONObject();
		
		try {
			
			Page<Person> page = personService.findAll(super.getPageable(start, length, Sort.by(Direction.DESC, PID)));

			result.put(DATA, page.getContent());
			result.put(DRAW, draw);
			result.put(RECORDS_TOTAL, page.getTotalElements());
			result.put(RECORDS_FILTERED, page.getTotalElements());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getLocalizedMessage());
			result.put(SUCCESS, false);
			result.put(MESSAGE, e.getLocalizedMessage());
		}		

		return result.toString();
	}
	
	@RequestMapping(value = "/info/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String info(@PathVariable long id, Model model) {
		JSONObject result = new JSONObject();

		try {
			Person person = personService.findById(id);

			result.put(SUCCESS, true);
			result.put(DATA, new JSONObject(person));
		} catch (Exception e) {
			e.printStackTrace();
			result.put(SUCCESS, false);
			result.put(MESSAGE, e.getLocalizedMessage());
		}

		return result.toString();
	}

}