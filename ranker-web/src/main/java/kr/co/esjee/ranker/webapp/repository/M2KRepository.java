package kr.co.esjee.ranker.webapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import kr.co.esjee.ranker.webapp.model.M2KNode;

public interface M2KRepository extends ElasticsearchRepository<M2KNode, Long> {

	Page<M2KNode> findByTitleLike(String searchKey, Pageable pageable);

}
