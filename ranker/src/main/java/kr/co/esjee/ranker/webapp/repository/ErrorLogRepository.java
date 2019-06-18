package kr.co.esjee.ranker.webapp.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import kr.co.esjee.ranker.webapp.model.ErrorLog;

public interface ErrorLogRepository extends ElasticsearchRepository<ErrorLog, Long> {

}
