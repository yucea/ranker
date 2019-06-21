package kr.co.esjee.ranker.webapp.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PathController {

	@RequestMapping("/")
	public String path() {
		return "redirect:view/index.html";
	}

	@RequestMapping("/redirect}")
	public String redirect(@RequestParam String url) {
		return "redirect:" + url;
	}

	@RequestMapping(value = "/page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String goPage(@RequestParam Map<String, String> params, Model model) {
		model.addAttribute("params", params);

		return params.get("url");
	}

}
