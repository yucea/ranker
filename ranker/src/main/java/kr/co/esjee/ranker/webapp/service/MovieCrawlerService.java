package kr.co.esjee.ranker.webapp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import kr.co.esjee.ranker.crawler.MovieCrawler;
import kr.co.esjee.ranker.schedule.Scheduler;
import kr.co.esjee.ranker.util.CalendarUtil;
import kr.co.esjee.ranker.webapp.model.ErrorLog;
import kr.co.esjee.ranker.webapp.model.MovieInfo;
import kr.co.esjee.ranker.webapp.model.MovieVO;
import kr.co.esjee.ranker.webapp.model.Person;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MovieCrawlerService {
	
	private static final int DELAY_TIME = 3000;
	
	@Autowired
	private MovieCrawler movieCrawler;
	
	@Autowired
	private MovieService movieService;
	
	@Autowired
	private PersonService personService;
	
	@Autowired
	private ErrorLogService errorLogService;

	/**
	 * execute
	 * 
	 * @param baseUrl
	 * @param attribute
	 * @param startYear
	 * @param endYear
	 */
	public void execute(String baseUrl, String attribute, Integer startYear, Integer endYear) {
		
		MovieVO movieVO = new MovieVO();
		
		List<Map<String, Object>> movieDirectList = movieCrawler.getMovieDirectList(baseUrl, attribute, startYear, endYear);
		
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
					log.info("URL is Empty");
				}
				
				log.info("[ {} Year ] Crawling Success = {}/{}", mvDirMap.get("progress"), yearCount, movieDirectList.size());
				
				yearCount++;
				
				try {
					Thread.sleep(DELAY_TIME);
				} catch (InterruptedException e) {
					log.error("Sleep Error = {}", e.getLocalizedMessage());
				}
			}
		} else {
			log.info("URL is Empty");
		}
	}
	
	/**
	 * execute with movieVO
	 * 
	 * @param baseUrl
	 * @param attribute
	 * @param startYear
	 * @param endYear
	 * @param movieVO
	 */
	@Async("scheduleExecutor")
	public void execute(MovieVO movieVO) {
		
		List<Map<String, Object>> movieDirectList = 
				movieCrawler.getMovieDirectList(movieVO.getMovieDirUrl(), movieVO.getMovieDirAtrb(), movieVO.getStartYear(), movieVO.getEndYear());
		
		// Movie Directory Crawler
		if(!movieDirectList.isEmpty()) {
			
			log.info("[ Find Year Count = {} ]", movieDirectList.size());
			
			int yearCount = 1;
			
			for(Map<String, Object> mvDirMap : movieDirectList) {
					
				log.info("[ {} Year ] Crawling Start = {}/{}", mvDirMap.get("progress"), yearCount, movieDirectList.size());
				
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
								movieService.merge(movieInfo.getMovieInfo());
								
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
							movieService.merge(movieInfo.getMovieInfo());
							
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
					movieService.merge(movieInfo.getMovieInfo());
					
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
		
		Scheduler.scheduleIds.remove(movieVO.getId());
	}
	
	public void errorExecute(String url, String startDate, String endDate) {
		
		List<Map<String, String>> urlList = new ArrayList<Map<String, String>>();
		List<ErrorLog> errorLogList = new ArrayList<ErrorLog>();
		
		try {
			
			Iterable<ErrorLog> items = errorLogService.findAll();
			
			CollectionUtils.addAll(errorLogList, items.iterator());
			
			if(!errorLogList.isEmpty()) {
				for(ErrorLog errorLog : errorLogList) {
					
					Map<String, String> urlInfo = new HashMap<String, String>();
					
					if(!startDate.equals("") && !endDate.equals("")) {
						int gDate = Integer.parseInt(CalendarUtil.getString(CalendarUtil.getDate(errorLog.getTime(), "yyyy-MM-dd HH:mm:ss")));
						int sDate = Integer.parseInt(startDate);
						int eDate = Integer.parseInt(endDate);
						
						if(sDate <= gDate && gDate <= eDate) {
							if(!errorLog.getMovieId().isEmpty()) {
								urlInfo.put("movieId", errorLog.getMovieId());
								urlInfo.put("url", url + errorLog.getMovieId());
								urlList.add(urlInfo);
							}
						}
					} else {
						if(!errorLog.getMovieId().isEmpty()) {
							urlInfo.put("movieId", errorLog.getMovieId());
							urlInfo.put("url", url + errorLog.getMovieId());
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
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
		}
	}
}