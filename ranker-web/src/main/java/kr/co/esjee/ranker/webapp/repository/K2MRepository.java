package kr.co.esjee.ranker.webapp.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import kr.co.esjee.ranker.webapp.model.M2MNode;

public interface K2MRepository extends ElasticsearchRepository<M2MNode, Long> {

}
