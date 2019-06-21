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

	public Pageable getPageable(int start, int size, Sort sort) {
		return PageRequest.of(start / size, size, sort);
	}

	public Pageable getPageable(int start, int size) {
		return this.getPageable(start, size, Sort.by(Direction.DESC, ID));
	}

}
