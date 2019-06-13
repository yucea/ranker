package kr.co.esjee.ranker.crawler;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import kr.co.esjee.ranker.webapp.model.MovieInfo;
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
		
		MovieCrawler movieCrawler = new MovieCrawler();		
		MovieVO movieVO = new MovieVO(); 
		
		MovieInfo movieInfo = movieCrawler.execute(movieVO);
		
		if(movieInfo.getMovieInfo() != null && movieInfo.getPersonInfo() != null) {
			// Movie Info
			if(movieService.findByTid(movieInfo.getMovieInfo().getTid()) == null) {
				movieService.save(movieInfo.getMovieInfo());
			}
			
			// Person Info
			for (Person person : movieInfo.getPersonInfo()) {				
				if(personService.findByPid(person.getPid()) == null) {
					personService.save(person);
				}
			}
		}else {
			log.error("=========== Movie Crawering Error ===========");
		}
	}
	
	@Test
	public void testListCrawler(){ 
		
		MovieVO movieVO = new MovieVO();
		
		log.info("=========== Movie Crawer Start ===========");
		
		MovieCrawler movieCrawler = new MovieCrawler();
		
		List<String> urlList = movieCrawler.getUrlList(movieVO);
		
		if(!urlList.isEmpty()) {
			
			log.info("=========== Movie Crawering TotalCount = {} ===========", urlList.size());
			
			int count = 1;
			
			for(String basicUrl : urlList) {
				
				movieVO.setBasicUrl(basicUrl);
				
				log.info("=========== Movie Crawering Start = {}/{} ===========", count, urlList.size());
				
				MovieInfo movieInfo = movieCrawler.execute(movieVO);
				
				if(movieInfo.getMovieInfo() != null && movieInfo.getPersonInfo() != null) {
					
					try {
						// Movie Info
						if(movieService.findByTid(movieInfo.getMovieInfo().getTid()) == null) {
							movieService.save(movieInfo.getMovieInfo());
						}
						
						// Person Info
						for (Person person : movieInfo.getPersonInfo()) {				
							if(personService.findByPid(person.getPid()) == null) {
								personService.save(person);
							}
						}
						
						log.info("=========== Movie Crawering Success = {}/{} ===========", count, urlList.size());						
					} catch (Exception e) {
						log.info("=========== Movie Crawering Error = {}/{} ===========", count, urlList.size());
					}
				} else {
					log.info("=========== Movie Crawering Error = {}/{} ===========", count, urlList.size());
				}
				
				count++;
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					log.error("Sleep Erroe = {}", e.getLocalizedMessage());
				}
			}
		}
		
		log.info("=========== Movie Crawering Finish ===========");		
	}	
}