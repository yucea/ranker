package kr.co.esjee.ranker.webapp.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.esjee.ranker.webapp.model.K2MNode;
import kr.co.esjee.ranker.webapp.model.M2KNode;
import kr.co.esjee.ranker.webapp.model.M2MNode;
import kr.co.esjee.ranker.webapp.repository.K2MRepository;
import kr.co.esjee.ranker.webapp.repository.M2KRepository;
import kr.co.esjee.ranker.webapp.repository.M2MRepository;

@Service
public class RecommendService {

	@Autowired
	private M2KRepository m2k;

	@Autowired
	private M2MRepository m2m;

	@Autowired
	private K2MRepository k2m;

	public Page<M2KNode> findAllOfM2K(Pageable pageable) {
		return m2k.findAll(pageable);
	}

	public Page<K2MNode> findAllOfK2M(Pageable pageable) {
		return k2m.findAll(pageable);
	}

	public M2KNode findByIdOfM2K(long id) {
		Optional<M2KNode> optional = m2k.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		} else {
			throw new IllegalArgumentException("Item not found :" + id);
		}
	}

	public M2MNode findByIdOfM2M(long id) {
		Optional<M2MNode> optional = m2m.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		} else {
			throw new IllegalArgumentException("Item not found :" + id);
		}
	}

	public K2MNode findByIdOfK2M(long id) {
		Optional<K2MNode> optional = k2m.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		} else {
			throw new IllegalArgumentException("Item not found :" + id);
		}
	}

	public Page<M2KNode> findByTitleLikeOfM2K(String searchKey, Pageable pageable) {
		return m2k.findByTitleLike(searchKey, pageable);
	}

	public Page<K2MNode> findByKeyLikeOfK2M(String searchKey, Pageable pageable) {
		return k2m.findByKeyLike(searchKey, pageable);
	}

}
