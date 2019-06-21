package kr.co.esjee.ranker.webapp.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import kr.co.esjee.ranker.webapp.AppConstant;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Document(indexName = AppConstant.SCHEDULE, type = AppConstant.DOC, replicas = 2)
public class Schedule implements AppConstant {

	@Id
	private long id;
	private String scheduleName = "";
	private String baseUrl = "";
	private String attribute = "";
	private int startYear;
	private int endYear;
	private String listUrl = "";	
	private String basicUrl = "";	
	private String crewUrl = "https://movie.naver.com/movie/bi/mi/detail.nhn";
	private String FilmoUrl = "https://movie.naver.com/movie/bi/pi/filmoMission.nhn?year=0&totalCount=1000&page=1000&peopleCode=";
	private String keyParam = "code";	
	private String pagingParam = "page";
	private String removeAtrb = "div.wide_info_area";
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
	private String actorAtrb = "ul.lst_people li div.p_info a.k_name";
	private String roleAtrb = "ul.lst_people li div.p_info div.part p.pe_cmt span";
	private String directorAtrb = "div.dir_product a.k_name";
	private String crewNameAtrb = "h3.h_movie a";
	private String crewProfileAtrb = "div.con_tx";
	private String crewBirthdayAtrb = "dl.info_spec dd";
	private String filmoListAtrb = "div.pilmo_info";	
	private String filmoTitleAtrb = "strong.pilmo_tit a";
	private String filmoYearAtrb = "p.pilmo_genre a";
	
	private int[] week; 	// 일요일 : 1, 토요일 : 7, 매주 : 0
	private int[] month; 	// 1-12, 매월 : 0
	private int[] day; 		// 1-31, 매일 : 0
	private int[] hour; 	// 0-23, 매시간 : 24
	private int[] minute; 	// 0-59, 매분 : 60
	private int repeat; 	// 반복회수 - 한번만 실행 : 1, 반복실행 : 0
	private boolean finish;	
	private boolean usable; // 사용여부

	public void completed() {
		if (this.repeat == 1) {
			this.finish = true;
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}