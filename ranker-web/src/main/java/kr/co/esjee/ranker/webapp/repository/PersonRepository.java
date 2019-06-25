package kr.co.esjee.ranker.webapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import kr.co.esjee.ranker.webapp.model.Person;

public interface PersonRepository extends ElasticsearchRepository<Person, Long> {
	
	Page<Person> findByNameLike(String searchKey, Pageable pageable);
	
}