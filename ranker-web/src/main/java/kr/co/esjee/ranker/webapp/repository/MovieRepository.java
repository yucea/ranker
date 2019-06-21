package kr.co.esjee.ranker.webapp.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import kr.co.esjee.ranker.webapp.model.Movie;

public interface MovieRepository extends ElasticsearchRepository<Movie, Long> {
	
	Movie findByTid(String tid);
	
}