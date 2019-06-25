package kr.co.esjee.ranker.webapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import kr.co.esjee.ranker.webapp.model.Movie;

public interface MovieRepository extends ElasticsearchRepository<Movie, Long> {
	
	Page<Movie> findByTitleLike(String searchKey, Pageable pageable);
	
	Movie findByTid(String tid);
	
	Movie findByMovieId(String movieId);
	
	Page<Movie> findByGenre(String genre, Pageable pageable);
	
}