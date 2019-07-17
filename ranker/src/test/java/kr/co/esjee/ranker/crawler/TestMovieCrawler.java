package kr.co.esjee.ranker.crawler;

import com.google.gson.Gson;
import kr.co.esjee.ranker.elasticsearch.ElasticOption;
import kr.co.esjee.ranker.elasticsearch.ElasticQuery;
import kr.co.esjee.ranker.elasticsearch.ElasticSearcher;
import kr.co.esjee.ranker.util.CalendarUtil;
import kr.co.esjee.ranker.webapp.AppConstant;
import kr.co.esjee.ranker.webapp.model.Movie;
import kr.co.esjee.ranker.webapp.model.MovieInfo;
import kr.co.esjee.ranker.webapp.model.MovieVO;
import kr.co.esjee.ranker.webapp.model.Person;
import kr.co.esjee.ranker.webapp.service.MovieCrawlerService;
import kr.co.esjee.ranker.webapp.service.MovieService;
import kr.co.esjee.ranker.webapp.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import java.util.*;
	
//@RunWith(SpringRunner.class)
//@SpringBootTest
@Slf4j
public class TestMovieCrawler implements AppConstant {
	
	private static final int DELAY_TIME = 5000;
	
	@Autowired
	private MovieService movieService;
	
	@Autowired
	private PersonService personService;
	
	@Autowired
	private MovieCrawler movieCrawler;
	
	@Autowired
	private ElasticsearchTemplate template;
	
	@Autowired
	private MovieCrawlerService movieCrawlerService;

	@Test
	public void testCrawler() {
		
		MovieVO movieVO = new MovieVO();
	
		movieVO.setMovieUrl("https://movie.naver.com/movie/bi/mi/basic.nhn?code=69776");
		
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
	public void test2Crawler(){
		
		List<Map<String, String>> urlList = new ArrayList<Map<String, String>>();
		List<Movie> movieList = new ArrayList<>();
		
		try {
			
			Iterable<Movie> items = movieService.findAll();
			
			CollectionUtils.addAll(movieList, items.iterator());
			
			if(!movieList.isEmpty()) {
				for(Movie movieInfo : movieList) {
					if(movieInfo.getOpenDay().length() > 8) {
						Map<String, String> urlInfo = new HashMap<String, String>();
						urlInfo.put("movieId", movieInfo.getMovieId());
						urlInfo.put("url", "https://movie.naver.com/movie/bi/mi/basic.nhn?code=" + movieInfo.getMovieId());
						urlList.add(urlInfo);
					}
				}
			}
			
			if(!urlList.isEmpty()) {
				
				log.info("Movie TotalCount = {}", urlList.size());
				
				int count = 1;
				
				for(Map<String, String> _url : urlList) {
					
					MovieVO movieVO = new MovieVO();
					
					movieVO.setMovieUrl(_url.get("url"));
					
					log.info("{} Movie Crawling Start = {}/{}", _url.get("movieId"), count, urlList.size());
					
					MovieInfo movieInfo = movieCrawler.execute(movieVO);
					
					if(movieInfo.getMovieInfo() != null && movieInfo.getPersonInfo() != null) {
						
						try {
							movieService.merge(movieInfo.getMovieInfo());
							
							for (Person person : movieInfo.getPersonInfo()) {
								personService.merge(person);
							}
							
							log.info("{} Movie Crawling Success = {}/{}", _url.get("movieId"), count, urlList.size());						
						} catch (Exception e) {
							log.error("{} Movie Crawling Failed = {}/{}", _url.get("movieId"), count, urlList.size());
						}
					} else {
						log.error("{} Movie Crawling Failed = {}/{}", _url.get("movieId"), count, urlList.size());
					}
					
					count++;					
					
					try {
						Thread.sleep(DELAY_TIME);
					} catch (InterruptedException e) {
						log.error("Sleep Error = {}", e.getLocalizedMessage());
					}
				}
			} else {
				log.error("Movie List URL is Empty");
			}
			
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
		}
	}
	
	@Test
	public void testMultiCrawler(){ 
		
		MovieVO movieVO = new MovieVO();
		
		List<Map<String, Object>> movieDirectList = 
				movieCrawler.getMovieDirectList(movieVO.getMovieDirUrl(), movieVO.getMovieDirAtrb(), movieVO.getStartYear(), movieVO.getEndYear());
		
		// Movie Directory Crawler
		if(!movieDirectList.isEmpty()) {
			
			log.info("[ Find Year Count = {} ]", movieDirectList.size());
			
			int yearCount = 1;
			
			for(Map<String, Object> mvDirMap : movieDirectList) {
					
				log.info("[ {} Year ] Crawling Start = {}/{}", mvDirMap.get("progress"), yearCount, movieDirectList.size());
				
				// URL Setting
				movieVO.setMovieListUrl(mvDirMap.get("url").toString());
				
				List<String> urlList = movieCrawler.getMovieUrlList(movieVO);
				
				if(!urlList.isEmpty()) {
					
					log.info("[ {} Year ] Movie TotalCount = {}", mvDirMap.get("progress"), urlList.size());
					
					int count = 1;
					
					for(String movieUrl : urlList) {
						
						movieVO.setMovieUrl(movieUrl);
						
						log.info("[ {} Year ] Movie Crawling Start = {}/{}", mvDirMap.get("progress"), count, urlList.size());
						
						MovieInfo movieInfo = movieCrawler.execute(movieVO);
						
						if(movieInfo.getMovieInfo() != null && movieInfo.getPersonInfo() != null) {
							
							try {
								// Movie Info
								movieService.merge(movieInfo.getMovieInfo());
								
								// Person Info
								for (Person person : movieInfo.getPersonInfo()) {
									personService.merge(person);
								}
								
								log.info("[ {} Year ] Movie Crawling Success = {}/{}", mvDirMap.get("progress"), count, urlList.size());
							} catch (Exception e) {
								log.error("[ {} Year ] Movie Crawling Failed = {}/{}", mvDirMap.get("progress"), count, urlList.size());
							}
						} else {
							log.error("[ {} Year ] Movie Crawling Failed = {}/{}", mvDirMap.get("progress"), count, urlList.size());
						}
						
						count++;
						
						try {
							Thread.sleep(DELAY_TIME);
						} catch (InterruptedException e) {
							log.error("Sleep Error = {}", e.getLocalizedMessage());
						}
					}
				} else {
					log.error("Movie List URL is Empty");
				}
				
				log.info("[ {} Year ] Crawling Success = {}/{}", mvDirMap.get("progress"), yearCount, movieDirectList.size());
				
				yearCount++;
				
				try {
					Thread.sleep(DELAY_TIME);
				} catch (InterruptedException e) {
					log.error("Sleep Error = {}", e.getLocalizedMessage());
				}
			}
		} 
		
		// Movie List Crawler
		else if (movieDirectList.isEmpty() && !movieVO.getMovieListUrl().isEmpty()) {
			
			List<String> urlList = movieCrawler.getMovieUrlList(movieVO);
			
			if(!urlList.isEmpty()) {
				
				log.info("Movie TotalCount = {}", urlList.size());
				
				int count = 1;
				
				for(String movieUrl : urlList) {
					
					movieVO.setMovieUrl(movieUrl);
					
					log.info("Movie Crawling Start = {}/{}", count, urlList.size());
					
					MovieInfo movieInfo = movieCrawler.execute(movieVO);
					
					if(movieInfo.getMovieInfo() != null && movieInfo.getPersonInfo() != null) {
						
						try {
							// Movie Info
							movieService.merge(movieInfo.getMovieInfo());
							
							// Person Info
							for (Person person : movieInfo.getPersonInfo()) {
								personService.merge(person);
							}
							
							log.info("Movie Crawling Success = {}/{}", count, urlList.size());
						} catch (Exception e) {
							log.error("Movie Crawling Failed = {}/{}", count, urlList.size());
						}
					} else {
						log.error("Movie Crawling Failed = {}/{}", count, urlList.size());
					}
					
					count++;
					
					try {
						Thread.sleep(DELAY_TIME);
					} catch (InterruptedException e) {
						log.error("Sleep Error = {}", e.getLocalizedMessage());
					}
				}
			} else {
				log.error("Movie List URL is Empty");
			}
		} 
		
		// Movie Crawler
		else if (movieDirectList.isEmpty() && movieVO.getMovieListUrl().isEmpty() && !movieVO.getMovieUrl().isEmpty()) {
			
			log.info("Movie Crawling Start");
			
			MovieInfo movieInfo = movieCrawler.execute(movieVO);
			
			if(movieInfo.getMovieInfo() != null && movieInfo.getPersonInfo() != null) {
				
				try {
					// Movie Info
					movieService.merge(movieInfo.getMovieInfo());
					
					// Person Info
					for (Person person : movieInfo.getPersonInfo()) {
						personService.merge(person);
					}
					
					log.info("Movie Crawling Success");
				} catch (Exception e) {
					log.error("Movie Crawling Failed");
				}
			} else {
				log.error("Movie Crawling Failed");
			}
			
		} else {
			log.error("Crawler URL is Empty");
		}
	}
	
	@Test
	public void testSchedule() {
		log.info("Schedule time: {}", CalendarUtil.getCurrentDateTime());
		
		try {
			Calendar calendar = CalendarUtil.getToday();

			BoolQueryBuilder query = QueryBuilders.boolQuery();
			query.must(ElasticQuery.termsQuery(FINISH, false));
			query.must(ElasticQuery.termsQuery(USABLE, true));
			query.must(ElasticQuery.termsQuery(WEEK, 0, calendar.get(Calendar.DAY_OF_WEEK)));
			query.must(ElasticQuery.termsQuery(MONTH, 0, calendar.get(Calendar.MONTH) + 1));
			query.must(ElasticQuery.termsQuery(DAY, 0, calendar.get(Calendar.DATE)));
			query.must(ElasticQuery.termsQuery(HOUR, 24, calendar.get(Calendar.HOUR)));
			query.must(ElasticQuery.termsQuery(MINUTE, 60, calendar.get(Calendar.MINUTE)));

			ElasticOption option = ElasticOption.newInstance()
					.queryBuilder(query)
					.page(1, 10000);

			SearchHits hits = ElasticSearcher.search(template.getClient(), SCHEDULE, option);

			log.info("{}", hits.totalHits);

			hits.forEach(h -> {
				MovieVO movieVO = new Gson().fromJson(new Gson().toJson(h.getSourceAsMap()), MovieVO.class);
				this.callCrawler(movieVO);
			});
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
		}
	}
	
	private void callCrawler(MovieVO movieVO) {
		log.info("call crawler : {}", movieVO);
		movieCrawlerService.execute(movieVO);
	}

	@Test
	public void test2() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 5; i++) {
			if(sb.length() > 0) sb.append(",");
			sb.append(i);
		}
		String a = sb.append(sb).toString();
		System.out.println(a);
	}
	
}