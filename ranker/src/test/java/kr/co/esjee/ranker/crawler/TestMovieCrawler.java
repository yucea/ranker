package kr.co.esjee.ranker.crawler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
	
	private static final int DELAY_TIME = 5000;
	
	@Autowired
	private MovieService movieService;
	
	@Autowired
	private PersonService personService;
	
	@Autowired
	private MovieCrawler movieCrawler;

	@Test
	public void testCrawler(){
		
		MovieVO movieVO = new MovieVO(); 
		
		MovieInfo movieInfo = movieCrawler.execute(movieVO);
		
		if(movieInfo.getMovieInfo() != null && movieInfo.getPersonInfo() != null) {
			// Movie Info
			movieService.merge(movieInfo.getMovieInfo());
			
			// Person Info
			for (Person person : movieInfo.getPersonInfo()) {
				personService.merge(person);
			}
		}
	}
	
	@Test
	public void testListCrawler(){ 
		
		MovieVO movieVO = new MovieVO();
		
		log.info("=========== Movie Crawer Start ===========");
		
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
						movieService.merge(movieInfo.getMovieInfo());
						
						// Person Info
						for (Person person : movieInfo.getPersonInfo()) {
							personService.merge(person);
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
					log.error("Sleep Error = {}", e.getLocalizedMessage());
				}
			}
		}
		
		log.info("=========== Movie Crawering Finish ===========");		
	}	
	
	@Test
	public void testTermCrawler() {
		
		String baseUrl = "https://movie.naver.com/movie/sdb/browsing/bmovie_open.nhn";
		String attribute = "table.directory_item_other tbody tr td a";
		int startYear = 2000;
		int endYear = 2010;
		
		MovieVO movieVO = new MovieVO();
		
		List<Map<String, Object>> baseUrlList = movieCrawler.getUrlList(baseUrl, attribute, startYear, endYear);
		
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
								movieService.merge(movieInfo.getMovieInfo());
								
								// Person Info
								for (Person person : movieInfo.getPersonInfo()) {
									personService.merge(person);
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
							log.error("Sleep Error = {}", e.getLocalizedMessage());
						}
					}
				}
				
				yearCount++;
				
				log.info("=========== {} Year Movie Crawering Success ===========", bUrl.get("year"));
				
				try {
					Thread.sleep(DELAY_TIME);
				} catch (InterruptedException e) {
					log.error("Sleep Error = {}", e.getLocalizedMessage());
				}
			}
		}
	}
	
	 @Test
	 public void tesxts () {		 
		 Date date = Calendar.getInstance().getTime();
		 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
		 String result = format.format(date);
		 System.out.println(result);
	 }
	 
	 @Test
	 public void tetet () {
		 
		 MovieCrawler movieCrawler2 = new MovieCrawler();
		 
		 List<String> urlList = new ArrayList<String>();
		 
		 boolean stop = false;
		 String prevUrl = "";
		 
		 int pageCount = 1;
		 
		 while(true) {
			
			Document doc = movieCrawler2.jsoupConnect("https://movie.naver.com/movie/sdb/browsing/bmovie.nhn?open=198" + "&" + "page" + "=" + pageCount);
			
			if(doc != null) {
				Elements elements = doc.select("ul.directory_list li a");
				
				int urlCount = 1;
				
				for(Element element : elements) {
					if(element.absUrl("href").contains("/movie/bi/mi/basic.nhn?code=")) {
						
						if(urlCount == 1) {
							if(prevUrl.equals(element.attr("abs:href"))) {
								stop = true;
								break;
							}else {
								prevUrl = element.attr("abs:href");
							}
						}
						
						urlList.add(element.attr("abs:href"));
						urlCount ++;
					}
				}
				
				if(stop) {
					break;
				}
				
				pageCount++;
			}
		 }
		 
		 for(String a : urlList) {
			 System.out.println(a);
		 }
	 }
}