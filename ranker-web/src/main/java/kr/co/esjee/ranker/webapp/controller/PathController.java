package kr.co.esjee.ranker.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PathController {

//	@RequestMapping("/")
//	public String path() {
//		return "redirect:/index";
//	}
//
//	@RequestMapping("/index")
//	public String index() {
//		return "/index";
//	}

	@RequestMapping("/movie/list")
	public String movieList() {
		System.out.println("movie!!!!!!!!!!!!!!!!");
		return "movie/list";
	}

	@RequestMapping("/person/list")
	public String personList() {
		return "person/list";
	}

}
