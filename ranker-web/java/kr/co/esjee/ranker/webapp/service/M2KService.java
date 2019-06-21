package kr.co.esjee.ranker.webapp.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.esjee.ranker.webapp.model.M2KNode;
import kr.co.esjee.ranker.webapp.repository.M2KRepository;

@Service
public class M2KService {

	@Autowired
	private M2KRepository repository;

	public Page<M2KNode> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}

	public M2KNode findById(long id) {
		Optional<M2KNode> optional = repository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		} else {
			throw new IllegalArgumentException("Item not found :" + id);
		}
	}

}
