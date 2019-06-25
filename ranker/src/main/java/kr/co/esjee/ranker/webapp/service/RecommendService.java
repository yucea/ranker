package kr.co.esjee.ranker.webapp.service;

import java.util.List;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import kr.co.esjee.ranker.webapp.model.recommend.K2MNode;
import kr.co.esjee.ranker.webapp.model.recommend.M2KNode;
import kr.co.esjee.ranker.webapp.model.recommend.M2MNode;
import kr.co.esjee.ranker.webapp.repository.K2MNodeRepository;
import kr.co.esjee.ranker.webapp.repository.M2KNodeRepository;
import kr.co.esjee.ranker.webapp.repository.M2MNodeRepository;

@Service
public class RecommendService {

	@Autowired
	private M2KNodeRepository m2k;

	@Autowired
	private M2MNodeRepository m2m;

	@Autowired
	private K2MNodeRepository k2m;

	@Autowired
	private ElasticsearchTemplate template;

	public Client getClient() {
		return template.getClient();
	}

	public List<M2KNode> findAllM2KNode() {
		return Lists.newArrayList(m2k.findAll().iterator());
	}

	public void m2kSaveAll(List<M2KNode> nodes) {
		m2k.saveAll(nodes);
	}

	public void m2mSaveAll(List<M2MNode> nodes) {
		m2m.saveAll(nodes);
	}

	public void k2mSaveAll(List<K2MNode> nodes) {
		k2m.saveAll(nodes);
	}

	public long m2mCount() {
		return m2m.count();
	}

	public void k2mDeleteAll() {
		k2m.deleteAll();
		k2m.refresh();
	}

	public void m2kDeleteAll() {
		m2k.deleteAll();
		m2k.refresh();
	}

	public void m2mDeleteAll() {
		m2m.deleteAll();
		m2m.refresh();
	}

}
