package kr.co.esjee.ranker.webapp.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.esjee.ranker.webapp.model.Person;
import kr.co.esjee.ranker.webapp.repository.PersonRepository;

@Service
public class PersonService extends AppService {

	@Autowired
	private PersonRepository repository;

	public Page<Person> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}

	public Person findById(long id) {
		Optional<Person> optional = repository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		} else {
			throw new IllegalArgumentException("Item not found : " + id);
		}
	}

	public Person save(Person person) {
		if (person.getId() == 0) {
			person.setId(super.getNextId(INDICES.mv_person_info));
		}

		return repository.save(person);
	}

	public void remove(long id) {
		repository.deleteById(id);
	}
	
	public boolean existsById(long id) {
		return repository.existsById(id);
	}
	
	public Person findByPid(String pid) {
		return repository.findByPid(pid);
	}
	
}