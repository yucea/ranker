package kr.co.esjee.ranker.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import kr.co.esjee.ranker.webapp.AppConstant;
import kr.co.esjee.ranker.webapp.model.Movie;
import kr.co.esjee.ranker.webapp.model.MovieVO;
import kr.co.esjee.ranker.webapp.model.Person;
import kr.co.esjee.ranker.webapp.model.PersonFilmo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MovieCrawler implements AppConstant {
	
	private final static String CONTENT_TYPE = "application/json;charset=UTF-8";
	private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:65.0) Gecko/20100101 Firefox/65.0";
	
	private static final String SEPARATOR = "=";
	private static final String QUESTION_SEPARATOR = "?";	    
	private static final String PARAM_SEPARATOR = "&";
	
	/**
	 * Jsoup Connect
	 * 
	 * @param connectUrl
	 * @return
	 */
	public Document jsoupConnect(String connectUrl) {
		
		Document document = null;
		
		try {
			document = Jsoup.connect(connectUrl)
					.header("Content-Type", CONTENT_TYPE)
					.header("Host", "movie.naver.com")
					.userAgent(USER_AGENT)
					.maxBodySize(Integer.MAX_VALUE)
					.ignoreContentType(true)
					.get();
			
			if(document == null) {
				try {
					Thread.sleep(10000);
					document = Jsoup.connect(connectUrl)
							.header("Content-Type", CONTENT_TYPE)
							.userAgent(USER_AGENT)
							.maxBodySize(Integer.MAX_VALUE)
							.ignoreContentType(true)
							.get();		
				} catch (InterruptedException ie) {
					log.error("Jsoup Connect Error = {}", ie.getLocalizedMessage());
				}
			}
			
		} catch (IOException ioe) {
			log.error("Jsoup Connect Error = {}", ioe.getLocalizedMessage());
		}
		
		return document;
	}
	
	
	public Movie executeMovieInfo(MovieVO movieVO) {
		
		// Movie Key
		String movieKey = (getQueryMap(movieVO.getBasicUrl()) != null && getQueryMap(movieVO.getBasicUrl()).get(movieVO.getKeyParam()) != null) ? 
				getQueryMap(movieVO.getBasicUrl()).get(movieVO.getKeyParam()) : "";
		
		// 영화 기본 정보
		Document basicInfoDoc = jsoupConnect(movieVO.getBasicUrl());
		
		// 영화 인물 정보
		Document crewInfoDoc = jsoupConnect(movieVO.getCrewUrl() + (movieKey.isEmpty() ? "" : "?" + movieVO.getKeyParam() + "="+ movieKey));
		
		return movieInfo(basicInfoDoc, crewInfoDoc, movieVO);
		
	}
	
	public List<Person> executePersonInfo(MovieVO movieVO) {
		
		// Movie Key
		String movieKey = (getQueryMap(movieVO.getBasicUrl()) != null && getQueryMap(movieVO.getBasicUrl()).get(movieVO.getKeyParam()) != null) ? 
					getQueryMap(movieVO.getBasicUrl()).get(movieVO.getKeyParam()) : "";
	
		// 영화 인물 정보
		Document crewInfoDoc = jsoupConnect(movieVO.getCrewUrl() + (movieKey.isEmpty() ? "" : "?" + movieVO.getKeyParam() + "="+ movieKey));
		
		return personInfo(crewInfoDoc, movieVO);
	}
	
	public Movie movieInfo(Document basicInfoDoc, Document crewInfoDoc, MovieVO movieVO) {
		
		Movie movie = new Movie();
		
		// 제거할 요소가 있으면 제거한다.
		if(StringUtils.isNotEmpty(movieVO.getRemoveAtrb())) {
			basicInfoDoc.select(movieVO.getRemoveAtrb()).remove();
		}
		
		// 영화 ID
		String movieId = (getQueryMap(movieVO.getBasicUrl()) != null && getQueryMap(movieVO.getBasicUrl()).get(movieVO.getKeyParam()) != null) ? 
				getQueryMap(movieVO.getBasicUrl()).get(movieVO.getKeyParam()) : "";
		movie.setMovieId(movieId);		
		
		// T_ID
		movie.setTid("N_" + movieId);
		
		// 제목
		movie.setTitle(basicInfoDoc.select(movieVO.getTitleAtrb()).first().text());
		
		// 원제목
		movie.setOrgTitle(basicInfoDoc.select(movieVO.getOrgTitleAtrb()).text());
		
		// 평점
		movie.setScore(StringUtils.replace(basicInfoDoc.select(movieVO.getScoreAtrb()).text(), " ", ""));
		
		// 장르		
		movie.setGenre((movieVO.getGenreAtrb().length > 1) ?
				basicInfoDoc.getElementsByAttributeValueContaining(movieVO.getGenreAtrb()[0], movieVO.getGenreAtrb()[1]).text() :
					basicInfoDoc.select(movieVO.getGenreAtrb()[0]).text());		
		
		// 제작 국가		
		movie.setNation((movieVO.getNationAtrb().length > 1) ? 
				basicInfoDoc.getElementsByAttributeValueContaining(movieVO.getNationAtrb()[0], movieVO.getNationAtrb()[1]).text() : 
					basicInfoDoc.select(movieVO.getNationAtrb()[0]).text());
		
		// 등급			
		movie.setGrade((movieVO.getGradeAtrb().length > 1) ?
				basicInfoDoc.getElementsByAttributeValueContaining(movieVO.getGradeAtrb()[0], movieVO.getGradeAtrb()[1]).text() :
					basicInfoDoc.select(movieVO.getGradeAtrb()[0]).text());
				
		// 개봉일자
		String openDay = (movieVO.getOpenDayAtrb().length > 1) ?
				basicInfoDoc.getElementsByAttributeValueContaining(movieVO.getOpenDayAtrb()[0], movieVO.getOpenDayAtrb()[1]).text() :
					basicInfoDoc.select(movieVO.getOpenDayAtrb()[0]).text();				
		movie.setOpenDay(StringUtils.replace(StringUtils.replace(openDay, " ", ""), ".", ""));
		
		// 배우
		movie.setActor(StringUtils.replace(crewInfoDoc.select(movieVO.getActorAtrb()).text(), " ", ","));
		
		// 배역
		String role = "";
		for(Element roles : crewInfoDoc.select(movieVO.getRoleAtrb())) {
			role += roles.text() + ",";
		}		
		movie.setRole(StringUtils.substring(role, 0, role.length() - 1));
		
		// 감독
		movie.setDirector(crewInfoDoc.select(movieVO.getDirectorAtrb()).text());		
		
		// 시놉시스
		movie.setSynopsis(basicInfoDoc.select(movieVO.getSynopsisAtrb()).text());
		
		// 제작노트
		movie.setMakingNote(basicInfoDoc.select(movieVO.getMakingNoteAtrb()).text());
		
		return movie;		
	}
	
	public List<Person> personInfo(Document crewInfoDoc, MovieVO movieVO) {
		
		List<Person> personList = new ArrayList<Person>();
		
		for(Element element : crewInfoDoc.select(movieVO.getActorAtrb())) {
			
			Person person = new Person();
			
			String crewUrl = element.select("a").attr("abs:href").toString();
			
			Document crewDoc = jsoupConnect(crewUrl);					
			
			// 제거할 요소가 있으면 제거한다.
			if(StringUtils.isNotEmpty(movieVO.getRemoveAtrb())) {
				crewDoc.select(movieVO.getRemoveAtrb()).remove();
			}
			
			// 배우 ID
			String pId = (getQueryMap(crewUrl) != null && getQueryMap(crewUrl).get(movieVO.getKeyParam()) != null) ? 
					getQueryMap(crewUrl).get(movieVO.getKeyParam()) : "";
					
			person.setPid(pId);
			
			// 배우 이름
			person.setName(crewDoc.select(movieVO.getCrewNameAtrb()).text());
			
			// 배우 생년월일
			person.setBirthday(crewDoc.select(movieVO.getCrewBirthdayAtrb()).first().text());
			
			// 배우 프로필
			person.setProfile(crewDoc.select(movieVO.getCrewProfileAtrb()).text());
			
			String filmoUrl = "https://movie.naver.com/movie/bi/pi/filmoMission.nhn?peopleCode=" + pId + "&year=0&totalCount=1000&page=1000";			
			
			Document filmoDoc = jsoupConnect(filmoUrl);
			
			if(filmoDoc == null) {			
				try {
					Thread.sleep(10000);
					filmoDoc = jsoupConnect(filmoUrl);				
				} catch (InterruptedException ie) {
					log.error("doc is null = {}", ie.getLocalizedMessage());
				}
			}
			
			List<PersonFilmo> filmoList = new ArrayList<PersonFilmo>();
			
			for(Element filmoElement : filmoDoc.select(movieVO.getFilmoListAtrb())) {
				
				PersonFilmo filmo = new PersonFilmo();
				
				String movieUrl = filmoElement.select(movieVO.getFilmoTitleAtrb()).attr("href");
				
				// 출연작 ID
				filmo.setMovieId((getQueryMap(movieUrl) != null && getQueryMap(movieUrl).get(movieVO.getKeyParam()) != null) ? 
						getQueryMap(movieUrl).get(movieVO.getKeyParam()) : "");
				
				// 출연작
				filmo.setMovieTitle(filmoElement.select(movieVO.getFilmoTitleAtrb()).text());
				
				// 출연 연도
				filmo.setMovieYear(filmoElement.select(movieVO.getFilmoYearAtrb()).first().text());
				
				// 출연작 감독
				Document directorDoc = jsoupConnect(movieVO.getCrewUrl() + (filmo.getMovieId().isEmpty() ? "" : "?" + movieVO.getKeyParam() + "="+ filmo.getMovieId()));
				filmo.setMovieDirector(directorDoc.select(movieVO.getDirectorAtrb()).text());
				
				filmoList.add(filmo);
				
			}
			
			person.setFilmo(filmoList);
			
			personList.add(person);
		}
		
		return personList;
	}
	
	private static Map<String, String> getQueryMap(String query) {

		if (query == null) return null;

		int pos = query.indexOf(QUESTION_SEPARATOR);

		if (pos >= 0) {
			query = query.substring(pos + 1);
		}

		String[] params = StringUtils.split(query, PARAM_SEPARATOR);

		Map<String, String> map = new HashMap<String, String>();

		for (String param : params) {				
			String name = StringUtils.split(param, SEPARATOR)[0];
			String value = StringUtils.split(param, SEPARATOR)[1];
			map.put(name, value);
		}

		return map;
	}
}