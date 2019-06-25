package kr.co.esjee.ranker.webapp.service;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import kr.co.esjee.ranker.elasticsearch.ElasticSearcher;
import kr.co.esjee.ranker.webapp.AppConstant;
import kr.co.esjee.ranker.webapp.model.Sequence;
import kr.co.esjee.ranker.webapp.repository.SequenceRepository;

public abstract class AppService implements AppConstant {

	@Autowired
	private ElasticsearchTemplate template;

	@Autowired
	private SequenceRepository sequenceRepository;

	public Client getClient() {
		return template.getClient();
	}

	public void remove(INDICES index) {
		sequenceRepository.deleteByName(index.name());
	}

	public long getNextId(INDICES index) {
		String indexName = index.name();
		Sequence sequence = sequenceRepository.findByName(indexName);
		if (sequence == null) {
			long count = ElasticSearcher.count(this.getClient(), indexName);
			sequence = new Sequence();
			sequence.setId(sequenceRepository.count());
			sequence.setName(indexName);
			sequence.setValue(count == 0 ? ES_START_ID : count + 1);
		} else {
			sequence.increase();
		}

		sequenceRepository.save(sequence);

		return sequence.getValue();
	}

	public Sequence update(INDICES index, long value) {
		Sequence sequence = sequenceRepository.findByName(index.name());
		sequence.setValue(value);

		return sequenceRepository.save(sequence);
	}

}