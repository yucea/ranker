package kr.co.esjee.ranker.webapp.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.esjee.ranker.webapp.model.Movie;
import kr.co.esjee.ranker.webapp.repository.MovieRepository;

@Service
public class MovieService {

	@Autowired
	private MovieRepository repository;

	public Page<Movie> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}

	public Movie findById(long id) {
		Optional<Movie> optional = repository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		} else {
			throw new IllegalArgumentException("Item not found :" + id);
		}
	}
	
}