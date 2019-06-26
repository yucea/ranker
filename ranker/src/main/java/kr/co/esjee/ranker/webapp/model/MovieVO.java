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
	
	// Directory Movie List
	private String movieDirUrl = "";
	
	private String movieDirAtrb = "";

	private String movieDirPage = "page";
	
	private Integer startYear = null;
	
	private Integer endYear = null;
	
	
	// Movie List
	// private String movieListUrl = "https://movie.naver.com/movie/running/current.nhn";
	private String movieListUrl = "";
	
	// private String movieUrlAtrb = "ul.lst_detail_t1 li dl a";
	private String movieUrlAtrb = "";
	
	
	// Movie
	// private String movieUrl = "https://movie.naver.com/movie/bi/mi/basic.nhn?code=99094";
	private String movieUrl = "https://movie.naver.com/movie/bi/mi/basic.nhn?code=99094";
	
	
	// Movie Info
	private String removeAtrb = "div.wide_info_area";
	
	private String movieKey = "code";
	
	private String titleAtrb = "h3.h_movie a";
	
	private String orgTitleAtrb = "strong.h_movie2";
	
	private String scoreAtrb = "a#pointNetizenPersentBasic em";
	
	private String runTimeAtrb = "dl.info_spec span";
	
	private String[] genreAtrb = {"href", "/movie/sdb/browsing/bmovie.nhn?genre="};
	
	private String[] nationAtrb = {"href", "/movie/sdb/browsing/bmovie.nhn?nation="};
	
	private String[] gradeAtrb = {"href", "/movie/sdb/browsing/bmovie.nhn?grade="};
	
	private String[] openDayAtrb = {"href", "/movie/sdb/browsing/bmovie.nhn?open="};
	
	private String synopsisAtrb = "div.story_area";
	
	private String makingNoteAtrb = "div.making_note p.con_tx";
	
	
	// Person
	private String personUrl = "https://movie.naver.com/movie/bi/mi/detail.nhn";	
	
	private String filmoUrl = "https://movie.naver.com/movie/bi/pi/filmoMission.nhn?year=0&totalCount=1000&page=1000&peopleCode=";
	
	private String actorAtrb = "ul.lst_people li div.p_info a.k_name";
	
	private String roleAtrb = "ul.lst_people li div.p_info div.part p.pe_cmt span";
	
	private String directorAtrb = "div.dir_product a.k_name";	
	
	private String crewNameAtrb = "h3.h_movie a";
	
	private String crewProfileAtrb = "div.con_tx";
	
	private String crewBirthdayAtrb = "dl.info_spec dd";
		
	private String filmoListAtrb = "div.pilmo_info";
	
	private String filmoTitleAtrb = "strong.pilmo_tit a";
	
	private String filmoYearAtrb = "p.pilmo_genre a";

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}