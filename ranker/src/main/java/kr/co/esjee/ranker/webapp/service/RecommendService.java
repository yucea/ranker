package kr.co.esjee.ranker.webapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import kr.co.esjee.ranker.webapp.model.recommend.M2KNode;
import kr.co.esjee.ranker.webapp.model.recommend.M2MNode;
import kr.co.esjee.ranker.webapp.repository.M2KNodeRepository;
import kr.co.esjee.ranker.webapp.repository.M2MNodeRepository;

@Service
public class RecommendService {

	@Autowired
	private M2KNodeRepository m2k;

	@Autowired
	private M2MNodeRepository m2m;

	public void saveM2KNode(List<M2KNode> nodes) {
		m2k.saveAll(nodes);
	}

	public List<M2KNode> findAllM2KNodes() {
		return Lists.newArrayList(m2k.findAll().iterator());
	}

	public void m2mSaveAll(List<M2MNode> nodes) {
		m2m.saveAll(nodes);
	}

	public long m2mCount() {
		return m2m.count();
	}
}
