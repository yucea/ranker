package kr.co.esjee.ranker.webapp.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import kr.co.esjee.ranker.webapp.AppConstant;

public abstract class AppController implements AppConstant {

	public int getPage(int pageNo, int size) {
		return pageNo * size - size;
	}

	public Pageable getPageable(int pageNo, int size, Sort sort) {
		return PageRequest.of(this.getPage(pageNo, size), size, sort);
	}

	public Pageable getPageable(int pageNo, int size) {
		return this.getPageable(pageNo, size, Sort.by(Direction.DESC, ID));
	}

}
