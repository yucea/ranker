package kr.co.esjee.ranker.webapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import kr.co.esjee.ranker.webapp.model.Schedule;

public interface ScheduleRepository extends ElasticsearchRepository<Schedule, Long> {
	
	Page<Schedule> findByScheduleNameLike(String searchKey, Pageable pageable);
	
}