package kr.co.esjee.ranker.webapp.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.esjee.ranker.webapp.model.Person;
import kr.co.esjee.ranker.webapp.repository.PersonRepository;

@Service
public class PersonService {

	@Autowired
	private PersonRepository repository;

	public Page<Person> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
	
	public Page<Person> findByNameLike(String searchKey, Pageable pageable) {
		return repository.findByNameLike(searchKey, pageable);
	}
	
	public Page<Person> findByName(String searchKey, Pageable pageable) {
		return repository.findByName(searchKey, pageable);
	}

	public Person findById(long id) {
		Optional<Person> optional = repository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		} else {
			throw new IllegalArgumentException("Item not found :" + id);
		}
	}
	
}