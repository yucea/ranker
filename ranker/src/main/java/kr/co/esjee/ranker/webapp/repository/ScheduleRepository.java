package kr.co.esjee.ranker.webapp.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import kr.co.esjee.ranker.webapp.model.Schedule;

public interface ScheduleRepository extends ElasticsearchRepository<Schedule, Long> {

}
