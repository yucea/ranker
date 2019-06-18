package kr.co.esjee.ranker.webapp.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.esjee.ranker.webapp.model.ErrorLog;
import kr.co.esjee.ranker.webapp.repository.ErrorLogRepository;

@Service
public class ErrorLogService extends AppService {

	@Autowired
	private ErrorLogRepository repository;

	public Page<ErrorLog> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}

	public ErrorLog findById(long id) {
		Optional<ErrorLog> optional = repository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		} else {
			throw new IllegalArgumentException("Item not found : " + id);
		}
	}

	public ErrorLog save(ErrorLog error) {
		if (error.getId() == 0) {
			error.setId(super.getNextId(INDICES.mv_error_log));
		}

		return repository.save(error);
	}

	public void remove(long id) {
		repository.deleteById(id);
	}
	
	public boolean existsById(long id) {
		return repository.existsById(id); 
	}
}