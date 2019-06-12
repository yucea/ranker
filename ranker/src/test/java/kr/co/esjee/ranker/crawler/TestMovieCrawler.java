package kr.co.esjee.ranker.crawler;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import kr.co.esjee.ranker.webapp.model.Movie;
import kr.co.esjee.ranker.webapp.model.MovieVO;
import kr.co.esjee.ranker.webapp.model.Person;
import kr.co.esjee.ranker.webapp.service.MovieService;
import kr.co.esjee.ranker.webapp.service.PersonService;
import lombok.extern.slf4j.Slf4j;
	
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TestMovieCrawler {
	
	@Autowired
	private MovieService movieService;
	
	@Autowired
	private PersonService personService;

	@Test
	public void testCrawler(){ 
		
		try {
			MovieCrawler movieCrawler = new MovieCrawler();
			
			MovieVO movieVO = new MovieVO();
			
			log.info("Movie Crawering Start");
			
			// 영화 정보
			Movie movie = movieCrawler.executeMovieInfo(movieVO);
			
			if(movieService.findByTid(movie.getTid()) == null) {
				movieService.save(movie);
			}
			
			// 인물 정보
			List<Person> personList = movieCrawler.executePersonInfo(movieVO);
			
			for (Person person : personList) {
				
				if(personService.findByPid(person.getPid()) == null) {
					personService.save(person);
				}
			}
			
			log.info("Movie Crawering Finish");
		} catch (Exception e) {
			log.error("Error = {}", e.getLocalizedMessage());
		}
	}
}