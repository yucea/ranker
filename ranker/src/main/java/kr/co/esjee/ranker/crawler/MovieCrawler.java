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

	private Document jsoupConnect(String connectUrl) {
		
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

	public MovieInfo execute(MovieVO movieVO) { 
		
		MovieInfo movieInfo = new MovieInfo();
		
		// Movie Key
		String movieKey = (getQueryMap(movieVO.getMovieUrl()) != null && getQueryMap(movieVO.getMovieUrl()).get(movieVO.getMovieKey()) != null) ? 
				getQueryMap(movieVO.getMovieUrl()).get(movieVO.getMovieKey()) : "";
				
		try {
			
			// 영화 정보 연결
			Document basicInfoDoc = jsoupConnect(movieVO.getMovieUrl());
			
			// 인물 정보 연결
			Document crewInfoDoc = jsoupConnect(movieVO.getPersonUrl() + (movieKey.isEmpty() ? "" : "?" + movieVO.getMovieKey() + "="+ movieKey));
			
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
			e.printStackTrace();
			ErrorLog errorLog = new ErrorLog();
			errorLog.setMovieId(movieKey);
			errorLog.setMessage(e.getLocalizedMessage());
			errorLog.setTime(format.format(Calendar.getInstance().getTime()));
			
			errorLogService.save(errorLog);
			
			log.error("Execute Error, MovieId = {}, Error = {}, Time = {}", errorLog.getMovieId(), e.getLocalizedMessage(), errorLog.getTime());
		}
		
		return movieInfo;
	}

	private Movie movieInfo(Document basicInfoDoc, Document crewInfoDoc, MovieVO movieVO) {
		
		Movie movie = new Movie();
		
		// 제거할 요소가 있으면 제거한다.
		if(StringUtils.isNotEmpty(movieVO.getRemoveAtrb())) {
			basicInfoDoc.select(movieVO.getRemoveAtrb()).remove();
		}
		
		// Movie ID
		String movieId = (getQueryMap(movieVO.getMovieUrl()) != null && getQueryMap(movieVO.getMovieUrl()).get(movieVO.getMovieKey()) != null) ? 
				getQueryMap(movieVO.getMovieUrl()).get(movieVO.getMovieKey()) : "";
		movie.setMovieId(movieId);		
		
		// tID
		movie.setTid("N_" + movieId);
		
		// Title
		movie.setTitle(basicInfoDoc.select(movieVO.getTitleAtrb()).isEmpty() ? "" : basicInfoDoc.select(movieVO.getTitleAtrb()).first().text());
		
		// Original Title
		movie.setOrgTitle(basicInfoDoc.select(movieVO.getOrgTitleAtrb()).isEmpty() ? "" : basicInfoDoc.select(movieVO.getOrgTitleAtrb()).text());
		
		// Score
		String score = StringUtils.replace(basicInfoDoc.select(movieVO.getScoreAtrb()).isEmpty() ? "" : basicInfoDoc.select(movieVO.getScoreAtrb()).text(), " ", "");
		movie.setScore(score.isEmpty() ? "0.00" : score);
		
		// Genre	
		movie.setGenre((movieVO.getGenreAtrb().length > 1) ?
				StringUtils.replace(basicInfoDoc.getElementsByAttributeValueContaining(movieVO.getGenreAtrb()[0], movieVO.getGenreAtrb()[1]).isEmpty() ? "" : basicInfoDoc.getElementsByAttributeValueContaining(movieVO.getGenreAtrb()[0], movieVO.getGenreAtrb()[1]).text(), " ", ",") :
					StringUtils.replace(basicInfoDoc.select(movieVO.getGenreAtrb()[0]).isEmpty() ? "" : basicInfoDoc.select(movieVO.getGenreAtrb()[0]).text(), " ", ","));
		
		// Runtime
		String runTime = "";
		if(!basicInfoDoc.select(movieVO.getRunTimeAtrb()).isEmpty()) {
			Elements runTimeElements = basicInfoDoc.select(movieVO.getRunTimeAtrb());
			for (int r = 0; r < runTimeElements.size(); r++) {
				if (runTimeElements.eq(r).text().trim().matches("^[0-9]+[분]")) {
					runTime = runTimeElements.eq(r).text().trim();
				}
			}
		}
		movie.setRunTime(!runTime.isEmpty() ? runTime : "");
		
		// Nation	
		movie.setNation((movieVO.getNationAtrb().length > 1) ? 
				StringUtils.replace(basicInfoDoc.getElementsByAttributeValueContaining(movieVO.getNationAtrb()[0], movieVO.getNationAtrb()[1]).isEmpty() ? "" : basicInfoDoc.getElementsByAttributeValueContaining(movieVO.getNationAtrb()[0], movieVO.getNationAtrb()[1]).text(), " ", ",") :
					basicInfoDoc.select(movieVO.getNationAtrb()[0]).isEmpty() ? "" : basicInfoDoc.select(movieVO.getNationAtrb()[0]).text());
		
		// Grade	
		movie.setGrade((movieVO.getGradeAtrb().length > 1) ?
				basicInfoDoc.getElementsByAttributeValueContaining(movieVO.getGradeAtrb()[0], movieVO.getGradeAtrb()[1]).isEmpty() ? "" : basicInfoDoc.getElementsByAttributeValueContaining(movieVO.getGradeAtrb()[0], movieVO.getGradeAtrb()[1]).text() :
					basicInfoDoc.select(movieVO.getGradeAtrb()[0]).isEmpty() ? "" : basicInfoDoc.select(movieVO.getGradeAtrb()[0]).text());
			
		String openDay = (movieVO.getOpenDayAtrb().length > 1) ?
				basicInfoDoc.getElementsByAttributeValueContaining(movieVO.getOpenDayAtrb()[0], movieVO.getOpenDayAtrb()[1]).text() :
					basicInfoDoc.select(movieVO.getOpenDayAtrb()[0]).text();	
		openDay = StringUtils.replace(StringUtils.replace(StringUtils.replace(openDay, " .", "."), " ", ","), ".", "");
		movie.setOpenDay(openDay.isEmpty() ? "" : StringUtils.split(openDay, ",")[0]);
		
		// Actors
		// Director
		String actor = "";
		if(!crewInfoDoc.select(movieVO.getActorAtrb()).isEmpty()) {
			StringBuilder actorBuilder = new StringBuilder();
			for(Element actors : crewInfoDoc.select(movieVO.getActorAtrb())) {
				actorBuilder.append(actors.text()).append(",");
			}
			actor = actorBuilder.toString();
		}
		movie.setActor(StringUtils.substring(actor, 0, actor.length() - 1));
		
		// Roles
		String role = "";
		if(!crewInfoDoc.select(movieVO.getRoleAtrb()).isEmpty()) {
			StringBuilder roleBuilder = new StringBuilder();
			for(Element roles : crewInfoDoc.select(movieVO.getRoleAtrb())) {
				roleBuilder.append(roles.text()).append(",");
			}
			role = roleBuilder.toString();
		}
		movie.setRole(StringUtils.substring(role, 0, role.length() - 1));
		
		// Director
		String director = "";
		if(!crewInfoDoc.select(movieVO.getDirectorAtrb()).isEmpty()) {
			StringBuilder directorBuilder = new StringBuilder();
			for(Element directors : crewInfoDoc.select(movieVO.getDirectorAtrb())) {
				directorBuilder.append(directors.text()).append(",");
			}
			director = directorBuilder.toString();
		}
		movie.setDirector(StringUtils.substring(director, 0, director.length() - 1));		
		
		// Synopsis
		String synopsis = basicInfoDoc.select(movieVO.getSynopsisAtrb()).isEmpty() ? "" : basicInfoDoc.select(movieVO.getSynopsisAtrb()).text();
		movie.setSynopsis(synopsis);
		
		// MakingNote
		movie.setMakingNote(basicInfoDoc.select(movieVO.getMakingNoteAtrb()).isEmpty() ? "" : basicInfoDoc.select(movieVO.getMakingNoteAtrb()).text());
		
		return movie;		
	}

	private List<Person> personInfo(Document crewInfoDoc, MovieVO movieVO) {
		
		List<Person> personList = new ArrayList<>();
		
		// 배우
		for(Element element : crewInfoDoc.select(movieVO.getActorAtrb())) {
			personList.add(getPersonInfo(element, movieVO));
		}
		
		// 감독
		for(Element element : crewInfoDoc.select(movieVO.getDirectorAtrb())) {
			personList.add(getPersonInfo(element, movieVO));
		}
		
		return personList;
	}

	private Person getPersonInfo(Element element, MovieVO movieVO) {

		Person person = new Person();

		String crewUrl = element.select("a").attr("abs:href");

		Document crewDoc = jsoupConnect(crewUrl);

		// Remove Attribute
		if(StringUtils.isNotEmpty(movieVO.getRemoveAtrb())) {
			crewDoc.select(movieVO.getRemoveAtrb()).remove();
		}

		// 인물 ID
		String pId = (getQueryMap(crewUrl) != null && getQueryMap(crewUrl).get(movieVO.getMovieKey()) != null) ?
				getQueryMap(crewUrl).get(movieVO.getMovieKey()) : "";

		person.setPid(pId);

		// 인물 이름
		person.setName(crewDoc.select(movieVO.getCrewNameAtrb()).isEmpty() ? "" : crewDoc.select(movieVO.getCrewNameAtrb()).text());
		person.setJob(crewDoc.select(movieVO.getCrewNameAtrb()).isEmpty() ? "" : "감독");

		// 인물 생년월일
		if(!crewDoc.select("dl.info_spec dt em").isEmpty()) {
			String birthday = crewDoc.select("dl.info_spec dt em").first().text();
			if(birthday.contains("출생")) {
				person.setBirthday(crewDoc.select(movieVO.getCrewBirthdayAtrb()).first().text());
			} else {
				person.setBirthday("");
			}
		}

		// 인물 프로필
		person.setProfile(crewDoc.select(movieVO.getCrewProfileAtrb()).isEmpty() ? "" : crewDoc.select(movieVO.getCrewProfileAtrb()).text());

		// 필모그래피 주소
		String filmoUrl = movieVO.getFilmoUrl() + pId;

		Document filmoDoc = jsoupConnect(filmoUrl);

		List<PersonFilmo> filmoList = new ArrayList<>();

		for(Element filmoElement : filmoDoc.select(movieVO.getFilmoListAtrb())) {

			PersonFilmo filmo = new PersonFilmo();

			String movieUrl = filmoElement.select(movieVO.getFilmoTitleAtrb()).attr("abs:href");

			// 영화 ID
			filmo.setMovieId((getQueryMap(movieUrl) != null && getQueryMap(movieUrl).get(movieVO.getMovieKey()) != null) ?
					getQueryMap(movieUrl).get(movieVO.getMovieKey()) : "");

			// 영화제목
			filmo.setMovieTitle(filmoElement.select(movieVO.getFilmoTitleAtrb()).isEmpty() ? "" : filmoElement.select(movieVO.getFilmoTitleAtrb()).text());

			// 영화 개봉년도
			filmo.setMovieYear(filmoElement.select(movieVO.getFilmoYearAtrb()).isEmpty() ? "" : filmoElement.select(movieVO.getFilmoYearAtrb()).first().text());

			Document directorDoc = jsoupConnect(movieVO.getPersonUrl() + (filmo.getMovieId().isEmpty() ? "" : "?" + movieVO.getMovieKey() + "="+ filmo.getMovieId()));

			// 감독
			String filmoDirector = "";
			if(!directorDoc.select(movieVO.getDirectorAtrb()).isEmpty()) {
				StringBuilder filmoDirectorBder = new StringBuilder();
				for(Element directors : directorDoc.select(movieVO.getDirectorAtrb())) {
					filmoDirectorBder.append(directors.text()).append(",");
				}
				filmoDirector = filmoDirectorBder.toString();
			}
			filmo.setMovieDirector(StringUtils.substring(filmoDirector, 0, filmoDirector.length() - 1));

			filmoList.add(filmo);
		}

		person.setFilmo(filmoList);

		return person;
	}

	public List<Map<String, Object>> getMovieDirectList(String movieDirUrl, String movieDirAtrb, Integer startYear, Integer endYear) {
		
		List<Map<String, Object>> urlList = new ArrayList<>();
		
		MovieCrawler movieCrawler = new MovieCrawler();
		
		if(!movieDirUrl.isEmpty() && !movieDirAtrb.isEmpty()) {
			
			Document doc = movieCrawler.jsoupConnect(movieDirUrl);
			
			if(doc != null) {
				
				for(Element element : doc.select(movieDirAtrb)) {
					
					Map<String, Object> urlMap = new HashMap<>();
					
					if(startYear != null && endYear != null) {
						String strYear = element.text();
						Integer year = Integer.parseInt(strYear.replaceAll( "[^0-9]", ""));
						
						if(year >= startYear && year <= endYear) {
							urlMap.put("progress", year);
							urlMap.put("url", element.absUrl("href"));
							urlList.add(urlMap);
						}
					} else {
						String progress = element.text();
						urlMap.put("progress", progress);
						urlMap.put("url", element.absUrl("href"));
						urlList.add(urlMap);
					}
				}
			}
		}
		
		return urlList;
	}

	public List<String> getMovieUrlList(MovieVO movieVO) {
		
		List<String> urlList = new ArrayList<>();

		if(movieVO.getMovieDirPage().isEmpty()) {
			
			Document doc = jsoupConnect(movieVO.getMovieListUrl());
			
			if(doc != null) {
				
				Elements elements = doc.select(movieVO.getMovieUrlAtrb());
				
				for(Element element : elements) {
					if(element.absUrl("href").contains("/movie/bi/mi/basic.nhn?code=")) {
						urlList.add(element.attr("abs:href"));
					}
				}
			}
			
		} else {
			
			boolean stop = false;
			
			String prevUrl = "";
			int pageCount = 1;	
			
			while(true) {
				
				Document doc = jsoupConnect(movieVO.getMovieListUrl() + "&" + movieVO.getMovieDirPage() + "=" + pageCount);
				
				if(doc != null) {
					Elements elements = doc.select(movieVO.getMovieUrlAtrb());
					
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
		}
		
		return urlList;
	}

	private static Map<String, String> getQueryMap(String query) {

		if (query == null) return null;

		int pos = query.indexOf("?");

		if (pos >= 0) {
			query = query.substring(pos + 1);
		}

		String[] params = StringUtils.split(query, "&");

		Map<String, String> map = new HashMap<>();

		for (String param : params) {				
			String name = StringUtils.split(param, "=")[0];
			String value = StringUtils.split(param, "=")[1];
			map.put(name, value);
		}

		return map;
	}
}