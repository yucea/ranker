package kr.co.esjee.ranker.webapp.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import kr.co.esjee.ranker.webapp.model.recommend.M2KNode;

public interface M2KNodeRepository extends ElasticsearchRepository<M2KNode, Long> {

}
