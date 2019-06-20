package kr.co.esjee.ranker.crawler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.co.esjee.ranker.webapp.AppConstant;
import kr.co.esjee.ranker.webapp.model.ErrorLog;
import kr.co.esjee.ranker.webapp.model.Movie;
import kr.co.esjee.ranker.webapp.model.MovieInfo;
import kr.co.esjee.ranker.webapp.model.MovieVO;
import kr.co.esjee.ranker.webapp.model.Person;
import kr.co.esjee.ranker.webapp.model.PersonFilmo;
import kr.co.esjee.ranker.webapp.service.ErrorLogService;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MovieCrawler implements AppConstant {
	
	private static final int DELAY_TIME = 5000;	
	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
	
	@Autowired
	private ErrorLogService errorLogService;
	
	/**
	 * Jsoup Connect
	 * 
	 * @param connectUrl
	 * @return Document
	 */
	public Document jsoupConnect(String connectUrl) {
		
		Document document = null;
		
		try {
			document = Jsoup.connect(connectUrl)
					.header("Content-Type", "application/json;charset=UTF-8")
					.header("Host", "movie.naver.com")
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:65.0) Gecko/20100101 Firefox/65.0")
					.maxBodySize(Integer.MAX_VALUE)
					.ignoreContentType(true)
					.get();
			
			if(document == null) {
				try {
					Thread.sleep(DELAY_TIME);
					
					document = Jsoup.connect(connectUrl)
							.header("Content-Type", "application/json;charset=UTF-8")
							.header("Host", "movie.naver.com")
							.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:65.0) Gecko/20100101 Firefox/65.0")
							.maxBodySize(Integer.MAX_VALUE)
							.ignoreContentType(true)
							.get();		
				} catch (InterruptedException ie) {
					log.error("Thread Sleep Error = {}", ie.getLocalizedMessage());
				}
			}
			
		} catch (IOException ioe) {
			log.error("Jsoup Connect Error = {}", ioe.getLocalizedMessage());
		}
		
		return document;
	}
	
	/**
	 * execute
	 * 
	 * @param movieVO
	 * @return
	 */
	public MovieInfo execute(MovieVO movieVO) { 
		
		MovieInfo movieInfo = new MovieInfo();
		
		// Movie Key
		String movieKey = (getQueryMap(movieVO.getBasicUrl()) != null && getQueryMap(movieVO.getBasicUrl()).get(movieVO.getKeyParam()) != null) ? 
				getQueryMap(movieVO.getBasicUrl()).get(movieVO.getKeyParam()) : "";
				
		try {
			
			// 영화 정보 연결
			Document basicInfoDoc = jsoupConnect(movieVO.getBasicUrl());
			
			// 인물 정보 연결
			Document crewInfoDoc = jsoupConnect(movieVO.getCrewUrl() + (movieKey.isEmpty() ? "" : "?" + movieVO.getKeyParam() + "="+ movieKey));
			
			if(basicInfoDoc != null && crewInfoDoc != null) {
				
				// 영화 정보
				movieInfo.setMovieInfo(movieInfo(basicInfoDoc, crewInfoDoc, movieVO));
					
				// 인물 정보
				movieInfo.setPersonInfo(personInfo(crewInfoDoc, movieVO));
				
			} else {
				ErrorLog errorLog = new ErrorLog();
				errorLog.setMovieId(movieKey);
				errorLog.setMessage("HTTP error fetching URL");
				errorLog.setTime(format.format(Calendar.getInstance().getTime()));
				
				errorLogService.save(errorLog);
				
				log.error("Execute Error, MovieId = {}, Error = HTTP error fetching URL, Time = {}", errorLog.getMovieId(), errorLog.getTime());
			}
			
		} catch (Exception e) {
			ErrorLog errorLog = new ErrorLog();
			errorLog.setMovieId(movieKey);
			errorLog.setMessage(e.getLocalizedMessage());
			errorLog.setTime(format.format(Calendar.getInstance().getTime()));
			
			errorLogService.save(errorLog);
			
			log.error("Execute Error, MovieId = {}, Error = {}, Time = {}", errorLog.getMovieId(), e.getLocalizedMessage(), errorLog.getTime());
		}
		
		return movieInfo;
	}
	
	/**
	 * Movie Info Crawler
	 * 
	 * @param basicInfoDoc
	 * @param crewInfoDoc
	 * @param movieVO
	 * @return
	 */
	public Movie movieInfo(Document basicInfoDoc, Document crewInfoDoc, MovieVO movieVO) {
		
		Movie movie = new Movie();
		
		// 제거할 요소가 있으면 제거한다.
		if(StringUtils.isNotEmpty(movieVO.getRemoveAtrb())) {
			basicInfoDoc.select(movieVO.getRemoveAtrb()).remove();
		}
		
		// Movie ID
		String movieId = (getQueryMap(movieVO.getBasicUrl()) != null && getQueryMap(movieVO.getBasicUrl()).get(movieVO.getKeyParam()) != null) ? 
				getQueryMap(movieVO.getBasicUrl()).get(movieVO.getKeyParam()) : "";
		movie.setMovieId(movieId);		
		
		// tID
		movie.setTid("N_" + movieId);
		
		// Title
		movie.setTitle(basicInfoDoc.select(movieVO.getTitleAtrb()).first().text());
		
		// Original Title
		movie.setOrgTitle(basicInfoDoc.select(movieVO.getOrgTitleAtrb()).text());
		
		// Score
		movie.setScore(StringUtils.replace(basicInfoDoc.select(movieVO.getScoreAtrb()).text(), " ", ""));
		
		// Genre	
		movie.setGenre((movieVO.getGenreAtrb().length > 1) ?
				StringUtils.replace(basicInfoDoc.getElementsByAttributeValueContaining(movieVO.getGenreAtrb()[0], movieVO.getGenreAtrb()[1]).text(), " ", ",") :
					StringUtils.replace(basicInfoDoc.select(movieVO.getGenreAtrb()[0]).text(), " ", ","));		
		
		// Runtime
		Elements runTimeElements = basicInfoDoc.select(movieVO.getRunTimeAtrb());
		String runTime = "";
		for (int r = 0; r < runTimeElements.size(); r++) {
			if (runTimeElements.eq(r).text().trim().matches("^[0-9]+[분]")) {
				runTime = runTimeElements.eq(r).text().trim();
			}
		}
		
		movie.setRunTime(runTime);
		
		// Nation		
		movie.setNation((movieVO.getNationAtrb().length > 1) ? 
				StringUtils.replace(basicInfoDoc.getElementsByAttributeValueContaining(movieVO.getNationAtrb()[0], movieVO.getNationAtrb()[1]).text(), " ", ",") :
					basicInfoDoc.select(movieVO.getNationAtrb()[0]).text());
		
		// Grade	
		movie.setGrade((movieVO.getGradeAtrb().length > 1) ?
				basicInfoDoc.getElementsByAttributeValueContaining(movieVO.getGradeAtrb()[0], movieVO.getGradeAtrb()[1]).text() :
					basicInfoDoc.select(movieVO.getGradeAtrb()[0]).text());
				
		// OpenDay
		String openDay = (movieVO.getOpenDayAtrb().length > 1) ?
				basicInfoDoc.getElementsByAttributeValueContaining(movieVO.getOpenDayAtrb()[0], movieVO.getOpenDayAtrb()[1]).text() :
					basicInfoDoc.select(movieVO.getOpenDayAtrb()[0]).text();				
		movie.setOpenDay(StringUtils.replace(StringUtils.replace(openDay, " ", ""), ".", ""));
		
		// Actors
		movie.setActor(StringUtils.replace(crewInfoDoc.select(movieVO.getActorAtrb()).text(), " ", ","));
		
		// Roles
		String role = "";
		for(Element roles : crewInfoDoc.select(movieVO.getRoleAtrb())) {
			role += roles.text() + ",";
		}
		
		movie.setRole(StringUtils.substring(role, 0, role.length() - 1));
		
		// Director
		String director = "";
		for(Element directors : crewInfoDoc.select(movieVO.getDirectorAtrb())) {
			director += directors.text() + ",";
		}
		
		movie.setDirector(StringUtils.substring(director, 0, director.length() - 1));		
		
		// Synopsis
		String synopsis =basicInfoDoc.select(movieVO.getSynopsisAtrb()).text();
		synopsis = StringUtils.replace(StringUtils.replace(synopsis, "줄거리 ", ""), " 제작노트 보기", "");
		movie.setSynopsis(synopsis);
		
		// MakingNote
		movie.setMakingNote(basicInfoDoc.select(movieVO.getMakingNoteAtrb()).text());
		
		return movie;		
	}
	
	/**
	 * Person Info Crawler
	 * 
	 * @param crewInfoDoc
	 * @param movieVO
	 * @return List<Person> 
	 */
	public List<Person> personInfo(Document crewInfoDoc, MovieVO movieVO) {
		
		List<Person> personList = new ArrayList<Person>();
		
		// 배우
		for(Element element : crewInfoDoc.select(movieVO.getActorAtrb())) {
			
			Person person = new Person();
			
			String crewUrl = element.select("a").attr("abs:href").toString();
			
			Document crewDoc = jsoupConnect(crewUrl);					
			
			// Remove Attribute
			if(StringUtils.isNotEmpty(movieVO.getRemoveAtrb())) {
				crewDoc.select(movieVO.getRemoveAtrb()).remove();
			}
			
			// 인물 ID
			String pId = (getQueryMap(crewUrl) != null && getQueryMap(crewUrl).get(movieVO.getKeyParam()) != null) ? 
					getQueryMap(crewUrl).get(movieVO.getKeyParam()) : "";
					
			person.setPid(pId);
			
			// 인물 이름
			person.setName(crewDoc.select(movieVO.getCrewNameAtrb()).text());
			
			// XXX
			// 인물 생년월일
			// person.setBirthday(crewDoc.select(movieVO.getCrewBirthdayAtrb()).first().text());
			
			// 인물 프로필
			person.setProfile(crewDoc.select(movieVO.getCrewProfileAtrb()).text());
			
			// 필모그래피 주소
			String filmoUrl = movieVO.getFilmoUrl() + pId;
			
			Document filmoDoc = jsoupConnect(filmoUrl);
			
			List<PersonFilmo> filmoList = new ArrayList<PersonFilmo>();
			
			for(Element filmoElement : filmoDoc.select(movieVO.getFilmoListAtrb())) {
				
				PersonFilmo filmo = new PersonFilmo();
				
				String movieUrl = filmoElement.select(movieVO.getFilmoTitleAtrb()).attr("abs:href");
				
				// 영화 ID
				filmo.setMovieId((getQueryMap(movieUrl) != null && getQueryMap(movieUrl).get(movieVO.getKeyParam()) != null) ? 
						getQueryMap(movieUrl).get(movieVO.getKeyParam()) : "");
				
				// 영화제목
				filmo.setMovieTitle(filmoElement.select(movieVO.getFilmoTitleAtrb()).text());
				
				// 영화 개봉년도
				filmo.setMovieYear(filmoElement.select(movieVO.getFilmoYearAtrb()).first().text());

				Document directorDoc = jsoupConnect(movieVO.getCrewUrl() + (filmo.getMovieId().isEmpty() ? "" : "?" + movieVO.getKeyParam() + "="+ filmo.getMovieId()));
				
				// 감독
				String director = "";
				for(Element directors : directorDoc.select(movieVO.getDirectorAtrb())) {
					director += directors.text() + ",";
				}
				
				filmo.setMovieDirector(StringUtils.substring(director, 0, director.length() - 1));
				
				filmoList.add(filmo);
			}
			
			person.setFilmo(filmoList);
			personList.add(person);
		}
		
		// 감독
		for(Element element : crewInfoDoc.select(movieVO.getDirectorAtrb())) {
			
			Person person = new Person();
			
			String crewUrl = element.select("a").attr("abs:href").toString();
			
			Document crewDoc = jsoupConnect(crewUrl);					
			
			// Remove Attribute
			if(StringUtils.isNotEmpty(movieVO.getRemoveAtrb())) {
				crewDoc.select(movieVO.getRemoveAtrb()).remove();
			}
			
			// 인물 ID
			String pId = (getQueryMap(crewUrl) != null && getQueryMap(crewUrl).get(movieVO.getKeyParam()) != null) ? 
					getQueryMap(crewUrl).get(movieVO.getKeyParam()) : "";
					
			person.setPid(pId);
			
			// 인물 이름
			person.setName(crewDoc.select(movieVO.getCrewNameAtrb()).text());
			
			// XXX
			// 인물 생년월일
			// person.setBirthday(crewDoc.select(movieVO.getCrewBirthdayAtrb()).first().text());
			
			// 인물 프로필
			person.setProfile(crewDoc.select(movieVO.getCrewProfileAtrb()).text());
			
			// 필모그래피 주소
			String filmoUrl = movieVO.getFilmoUrl() + pId;
			
			Document filmoDoc = jsoupConnect(filmoUrl);
			
			List<PersonFilmo> filmoList = new ArrayList<PersonFilmo>();
			
			for(Element filmoElement : filmoDoc.select(movieVO.getFilmoListAtrb())) {
				
				PersonFilmo filmo = new PersonFilmo();
				
				String movieUrl = filmoElement.select(movieVO.getFilmoTitleAtrb()).attr("abs:href");
				
				// 영화 ID
				filmo.setMovieId((getQueryMap(movieUrl) != null && getQueryMap(movieUrl).get(movieVO.getKeyParam()) != null) ? 
						getQueryMap(movieUrl).get(movieVO.getKeyParam()) : "");
				
				// 영화제목
				filmo.setMovieTitle(filmoElement.select(movieVO.getFilmoTitleAtrb()).text());
				
				// 영화 개봉년도
				filmo.setMovieYear(filmoElement.select(movieVO.getFilmoYearAtrb()).first().text());

				Document directorDoc = jsoupConnect(movieVO.getCrewUrl() + (filmo.getMovieId().isEmpty() ? "" : "?" + movieVO.getKeyParam() + "="+ filmo.getMovieId()));
				
				// 감독
				String director = "";
				for(Element directors : directorDoc.select(movieVO.getDirectorAtrb())) {
					director += directors.text() + ",";
				}
				
				filmo.setMovieDirector(StringUtils.substring(director, 0, director.length() - 1));
				
				filmoList.add(filmo);
			}
			
			person.setFilmo(filmoList);
			personList.add(person);
		}
		
		return personList;
	}
	
	/**
	 * URL List
	 * 
	 * @param movieVO
	 * @return
	 */
	public List<Map<String, Object>> getUrlList(String url, String attribute, int startYear, int endYear) {
		
		List<Map<String, Object>> urlList = new ArrayList<Map<String, Object>>();
		
		MovieCrawler mc = new MovieCrawler();
		
		Document doc = mc.jsoupConnect(url);
		
		if(doc != null) {
			for(Element element : doc.select(attribute)) {
				
				Map<String, Object> urlMap = new HashMap<String, Object>();
				
				String strYear = element.text();
				int year = Integer.parseInt(strYear.replaceAll("[^0-9]", ""));
				
				if(year >= startYear && year <= endYear) {
					urlMap.put("year", year);
					urlMap.put("url", element.absUrl("href"));
					urlList.add(urlMap);
				}
			}
		}
		
		return urlList;
	}
	
	/**
	 * Movie URL List
	 * 
	 * @param movieVO
	 * @return List<String>
	 * @throws Exception
	 */
	public List<String> getMovieUrlList(MovieVO movieVO) {
		
		List<String> urlList = new ArrayList<String>();
		
		if(movieVO.getPagingParam().isEmpty()) {
			
			Document doc = jsoupConnect(movieVO.getListUrl());
			
			if(doc != null) {
				Elements elements = doc.select("ul.directory_list li a");
				
				for(Element element : elements) {
					if(element.absUrl("href").contains("/movie/bi/mi/basic.nhn?code=")) {
						urlList.add(element.attr("abs:href"));
					}
				}
			}
			
		} else {
			
			int pageCount = 1;
			
			while(true) {
				
				Document doc = jsoupConnect(movieVO.getListUrl() + "&" + movieVO.getPagingParam() + "=" + pageCount);
				
				if(doc != null) {
					Elements elements = doc.select("ul.directory_list li a");
					
					int urlCount = 1;
					for(Element element : elements) {
						if(element.absUrl("href").contains("/movie/bi/mi/basic.nhn?code=")) {
							urlList.add(element.attr("abs:href"));
							urlCount ++;
						}
					}
					
					if(urlCount < movieVO.getMaxCount()) {
						break;
					}
					
					pageCount++;
				}
			}
		}
		
		return urlList;
	}
	
	
	/**
	 * Query Map
	 * 
	 * @param query
	 * @return Map
	 */
	private static Map<String, String> getQueryMap(String query) {

		if (query == null) return null;

		int pos = query.indexOf("?");

		if (pos >= 0) {
			query = query.substring(pos + 1);
		}

		String[] params = StringUtils.split(query, "&");

		Map<String, String> map = new HashMap<String, String>();

		for (String param : params) {				
			String name = StringUtils.split(param, "=")[0];
			String value = StringUtils.split(param, "=")[1];
			map.put(name, value);
		}

		return map;
	}
}