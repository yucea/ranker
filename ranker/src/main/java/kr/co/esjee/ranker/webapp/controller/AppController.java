package kr.co.esjee.ranker.webapp.controller;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import kr.co.esjee.ranker.webapp.AppConstant;

public abstract class AppController implements AppConstant {

	@Autowired
	private ElasticsearchTemplate template;

	public Client getClient() {
		return template.getClient();
	}

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
