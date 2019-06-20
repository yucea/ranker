package kr.co.esjee.ranker.webapp.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.esjee.ranker.webapp.model.Movie;
import kr.co.esjee.ranker.webapp.repository.MovieRepository;

@Service
public class MovieService extends AppService {

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
			throw new IllegalArgumentException("Item not found : " + id);
		}
	}

	public Movie save(Movie movie) {
		if (movie.getId() == 0) {
			movie.setId(super.getNextId(INDICES.mv_basic_info));
		}

		return repository.save(movie);
	}

	public void remove(long id) {
		repository.deleteById(id);
	}
	
	public boolean existsById(long id) {
		return repository.existsById(id); 
	}
	
	public Movie findByTid(String tid) {
		return repository.findByTid(tid);
	}
	
	public void deleteByTid(String tid) {
		repository.deleteByTid(tid);
	}
	
	public Movie merge(Movie movie) {		
		Movie movieInfo = repository.findByTid(movie.getTid());		
		if(movieInfo != null) {
			movie.setId(movieInfo.getId());
		}	
		
		return this.save(movie);
	}
}