package kr.co.esjee.ranker.webapp.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;

import kr.co.esjee.ranker.webapp.AppConstant;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class MovieVO implements AppConstant {

	@Id
	private long id;
	
	// Base URL
	private String baseUrl = "https://movie.naver.com/movie/sdb/browsing/bmovie_open.nhn";
	
	private String attribute = "table.directory_item_other tbody tr td a";
	
	private int startYear = 2019;
	
	private int endYear = 2019;
	
	// List URL
	private String listUrl = "";
	
	// Movie URL
	private String basicUrl = "https://movie.naver.com/movie/bi/mi/basic.nhn?code=184946";
	
	// Person URL
	private String crewUrl = "https://movie.naver.com/movie/bi/mi/detail.nhn";	
	
	// Filmo URL
	private String FilmoUrl = "https://movie.naver.com/movie/bi/pi/filmoMission.nhn?year=0&totalCount=1000&page=1000&peopleCode=";
	
	// 영화키 파라미터
	private String keyParam = "code";
	
	// 페이지 파라미터
	private String pagingParam = "page";
	
	// 페이지 목록 갯수
	private Integer maxCount = 20;
	
	// 필요 없는 요소
	private String removeAtrb = "div.wide_info_area";
	
	// 영화 제목
	private String titleAtrb = "h3.h_movie a";
	
	// 영화 원제목
	private String orgTitleAtrb = "strong.h_movie2";
	
	// 평점
	private String scoreAtrb = "a#pointNetizenPersentBasic em";
	
	// 상영시간
	private String runTimeAtrb = "dl.info_spec span";
	
	// 장르
	private String[] genreAtrb = {"href", "/movie/sdb/browsing/bmovie.nhn?genre="};
	
	// 제작국가
	private String[] nationAtrb = {"href", "/movie/sdb/browsing/bmovie.nhn?nation="};
	
	// 등급
	private String[] gradeAtrb = {"href", "/movie/sdb/browsing/bmovie.nhn?grade="};
	
	// 개봉일
	private String[] openDayAtrb = {"href", "/movie/sdb/browsing/bmovie.nhn?open="};
	
	// 시놉시스
	private String synopsisAtrb = "div.story_area";
	
	// 제작노트
	private String makingNoteAtrb = "div.making_note p.con_tx";
	
	// 배우 이름	
	private String actorAtrb = "ul.lst_people li div.p_info a.k_name";
	
	// 배우 역활
	private String roleAtrb = "ul.lst_people li div.p_info div.part p.pe_cmt span";
	
	// 감독 이름
	private String directorAtrb = "div.dir_product a.k_name";	
	
	// 출연자
	private String crewNameAtrb = "h3.h_movie a";
	
	// 출연자 프로필
	private String crewProfileAtrb = "div.con_tx";
	
	// 출연자 생년월일
	private String crewBirthdayAtrb = "dl.info_spec dd";
	
	// 출연 목록	
	private String filmoListAtrb = "div.pilmo_info";
	
	// 출연 제목
	private String filmoTitleAtrb = "strong.pilmo_tit a";
	
	// 출연 연도
	private String filmoYearAtrb = "p.pilmo_genre a";

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}