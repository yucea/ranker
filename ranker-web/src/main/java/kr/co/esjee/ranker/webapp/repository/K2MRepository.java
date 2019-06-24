package kr.co.esjee.ranker.webapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import kr.co.esjee.ranker.webapp.model.K2MNode;

public interface K2MRepository extends ElasticsearchRepository<K2MNode, Long> {

	Page<K2MNode> findByKeyLike(String searchKey, Pageable pageable);

}
