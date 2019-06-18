package kr.co.esjee.ranker.crawler;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kr.co.esjee.ranker.webapp.model.MovieInfo;
import kr.co.esjee.ranker.webapp.model.MovieVO;
import kr.co.esjee.ranker.webapp.model.Person;
import kr.co.esjee.ranker.webapp.service.MovieService;
import kr.co.esjee.ranker.webapp.service.PersonService;
import lombok.extern.slf4j.Slf4j;
	
//@RunWith(SpringRunner.class)
//@SpringBootTest
@Slf4j
public class TestMovieCrawler {
	
	private static final int DELAY_TIME = 5000;
	
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
		
		List<String> urlList = movieCrawler.getMovieUrlList(movieVO);
		
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
					Thread.sleep(DELAY_TIME);
				} catch (InterruptedException e) {
					log.error("Sleep Erroe = {}", e.getLocalizedMessage());
				}
			}
		}
		
		log.info("=========== Movie Crawering Finish ===========");		
	}	
	
	@Test
	public void testTermCrawler() {
		
		String baseUrl = "https://movie.naver.com/movie/sdb/browsing/bmovie_open.nhn";
		int startYear = 2000;
		int endYear = 2010;
		
		MovieVO movieVO = new MovieVO();
		MovieCrawler movieCrawler = new MovieCrawler();
		
		List<Map<String, Object>> baseUrlList = movieCrawler.getUrlList(baseUrl, startYear, endYear);
		
		if(!baseUrlList.isEmpty()) {
			
			log.info("=========== Year TotalCount = {} ===========", baseUrlList.size());
			
			int yearCount = 1;
			
			for(Map<String, Object> bUrl : baseUrlList) {
					
				log.info("=========== {} Year Crawering = {}/{} ===========", bUrl.get("year"), yearCount, baseUrlList.size());
				
				// URL Setting
				movieVO.setListUrl(bUrl.get("url").toString());
				
				List<String> urlList = movieCrawler.getMovieUrlList(movieVO);
				
				if(!urlList.isEmpty()) {
					
					log.info("=========== {] Year Movie TotalCount = {} ===========", bUrl.get("year"), urlList.size());
					
					int count = 1;
					
					for(String basicUrl : urlList) {
						
						movieVO.setBasicUrl(basicUrl);
						
						log.info("=========== {] Year Movie Crawering Start = {}/{} ===========", bUrl.get("year"), count, urlList.size());
						
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
								
								log.info("=========== {} Year Movie Crawering Success = {}/{} ===========", bUrl.get("year"), count, urlList.size());						
							} catch (Exception e) {
								log.error("=========== {} Year Movie Crawering Error = {}/{} ===========", bUrl.get("year"), count, urlList.size());
							}
						} else {
							log.error("=========== {} Year Movie Crawering Error = {}/{} ===========", bUrl.get("year"), count, urlList.size());
						}
						
						count++;
						
						try {
							Thread.sleep(DELAY_TIME);
						} catch (InterruptedException e) {
							log.error("Sleep Erroe = {}", e.getLocalizedMessage());
						}
					}
				}
				
				yearCount++;
				
				log.info("=========== {} Year Movie Crawering Success ===========", bUrl.get("year"));
				
				try {
					Thread.sleep(DELAY_TIME);
				} catch (InterruptedException e) {
					log.error("Sleep Erroe = {}", e.getLocalizedMessage());
				}
			}
		}
	}
}