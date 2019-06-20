package kr.co.esjee.ranker.webapp.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import kr.co.esjee.ranker.webapp.model.recommend.M2MNode;

public interface M2MNodeRepository extends ElasticsearchRepository<M2MNode, Long> {

}
