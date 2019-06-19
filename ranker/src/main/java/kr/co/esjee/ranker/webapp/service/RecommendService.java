package kr.co.esjee.ranker.webapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.esjee.ranker.webapp.model.recommend.M2KNode;
import kr.co.esjee.ranker.webapp.repository.M2KNodeRepository;

@Service
public class RecommendService {

	@Autowired
	private M2KNodeRepository m2kNodeRepository;

	public void saveM2KNode(List<M2KNode> nodes) {
		m2kNodeRepository.saveAll(nodes);
	}
}
