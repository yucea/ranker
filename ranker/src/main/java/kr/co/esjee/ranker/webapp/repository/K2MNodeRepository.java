package kr.co.esjee.ranker.webapp.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import kr.co.esjee.ranker.webapp.model.recommend.K2MNode;

public interface K2MNodeRepository extends ElasticsearchRepository<K2MNode, Long> {

}
