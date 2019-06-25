package kr.co.esjee.ranker.webapp.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import kr.co.esjee.ranker.webapp.model.Person;

public interface PersonRepository extends ElasticsearchRepository<Person, Long> {
	
}