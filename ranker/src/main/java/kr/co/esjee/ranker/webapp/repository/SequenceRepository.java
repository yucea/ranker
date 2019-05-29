package kr.co.esjee.ranker.webapp.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import kr.co.esjee.ranker.webapp.model.Sequence;

public interface SequenceRepository extends ElasticsearchRepository<Sequence, Long> {

	Sequence findByName(String indexName);

	void deleteByName(String name);

}
