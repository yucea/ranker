package kr.co.esjee.ranker.webapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import kr.co.esjee.ranker.webapp.model.M2MNode;

public interface M2MRepository extends ElasticsearchRepository<M2MNode, Long> {

	Page<M2MNode> findByTitleLike(String searchKey, Pageable pageable);

}
