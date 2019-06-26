package kr.co.esjee.ranker.webapp.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import kr.co.esjee.ranker.webapp.AppConstant;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Document(indexName = AppConstant.SCHEDULE, type = AppConstant.DOC, replicas = 2)
public class Schedule implements AppConstant {

	@Id
	private long id;
	
	@Field(type = FieldType.Text)
	private String scheduleName;
	
	/* Directory Movie List */
	@Field(type = FieldType.Text)
	private String movieDirUrl = "";
	
	@Field(type = FieldType.Text)
	private String movieDirAtrb = "";

	@Field(type = FieldType.Text)
	private String movieDirPage = "";
	
	@Field(type = FieldType.Text)
	private Integer startYear;
	
	@Field(type = FieldType.Text)
	private Integer endYear;
	
	/* Movie */
	@Field(type = FieldType.Text)
	private String movieListUrl = "https://movie.naver.com/movie/running/current.nhn";
	
	@Field(type = FieldType.Text)
	private String movieUrlAtrb = "ul.lst_detail_t1 li dl a";
	
	@Field(type = FieldType.Text)
	private String movieUrl = "";
	
	@Field(type = FieldType.Text)
	private String personUrl = "https://movie.naver.com/movie/bi/mi/detail.nhn";	
		
	@Field(type = FieldType.Text)
	private String filmoUrl = "https://movie.naver.com/movie/bi/pi/filmoMission.nhn?year=0&totalCount=1000&page=1000&peopleCode=";
	
	@Field(type = FieldType.Text)
	private String removeAtrb = "div.wide_info_area";
	
	@Field(type = FieldType.Text)
	private String movieKey = "code";
	
	@Field(type = FieldType.Text)
	private String titleAtrb = "h3.h_movie a";
	
	@Field(type = FieldType.Text)
	private String orgTitleAtrb = "strong.h_movie2";
	
	@Field(type = FieldType.Text)
	private String scoreAtrb = "a#pointNetizenPersentBasic em";
	
	@Field(type = FieldType.Text)
	private String runTimeAtrb = "dl.info_spec span";
	
	@Field(type = FieldType.Text)
	private String[] genreAtrb = {"href", "/movie/sdb/browsing/bmovie.nhn?genre="};
	
	@Field(type = FieldType.Text)
	private String[] nationAtrb = {"href", "/movie/sdb/browsing/bmovie.nhn?nation="};
	
	@Field(type = FieldType.Text)
	private String[] gradeAtrb = {"href", "/movie/sdb/browsing/bmovie.nhn?grade="};
	
	@Field(type = FieldType.Text)
	private String[] openDayAtrb = {"href", "/movie/sdb/browsing/bmovie.nhn?open="};
	
	@Field(type = FieldType.Text)
	private String synopsisAtrb = "div.story_area";
	
	@Field(type = FieldType.Text)
	private String makingNoteAtrb = "div.making_note p.con_tx";
	
	
	/* Person */
	@Field(type = FieldType.Text)
	private String actorAtrb = "ul.lst_people li div.p_info a.k_name";
	
	@Field(type = FieldType.Text)
	private String roleAtrb = "ul.lst_people li div.p_info div.part p.pe_cmt span";
	
	@Field(type = FieldType.Text)
	private String directorAtrb = "div.dir_product a.k_name";	
	
	@Field(type = FieldType.Text)
	private String crewNameAtrb = "h3.h_movie a";
	
	@Field(type = FieldType.Text)
	private String crewProfileAtrb = "div.con_tx";
	
	@Field(type = FieldType.Text)
	private String crewBirthdayAtrb = "dl.info_spec dd";
		
	@Field(type = FieldType.Text)
	private String filmoListAtrb = "div.pilmo_info";
	
	@Field(type = FieldType.Text)
	private String filmoTitleAtrb = "strong.pilmo_tit a";
	
	@Field(type = FieldType.Text)
	private String filmoYearAtrb = "p.pilmo_genre a";
	
	/** Schedule */
	@Field(type = FieldType.Integer)
	private int[] week; 				// 일요일 : 1, 토요일 : 7, 매주 : 0

	@Field(type = FieldType.Integer)
	private int[] month; 				// 1-12, 매월 : 0

	@Field(type = FieldType.Integer)
	private int[] day; 					// 1-31, 매일 : 0

	@Field(type = FieldType.Integer)
	private int[] hour; 				// 0-23, 매시간 : 24

	@Field(type = FieldType.Integer)
	private int[] minute; 				// 0-59, 매분 : 60

	@Field(type = FieldType.Integer)
	private int repeat; 				// 반복회수 - 한번만 실행 : 1, 반복실행 : 0

	@Field(type = FieldType.Boolean)
	private boolean finish;

	@Field(type = FieldType.Boolean)
	private boolean usable; // 사용여부

	// public boolean isMatch(Calendar cal) {
	// if (!finish
	// && (week == 0 || week == cal.get(Calendar.DAY_OF_WEEK))
	// && (month == 0 || month == cal.get(Calendar.MONTH) + 1)
	// && (day == 0 || day == cal.get(Calendar.DATE))
	// && (hour == 24 || hour == cal.get(Calendar.HOUR))
	// && (minute == 60 || minute == cal.get(Calendar.MINUTE))) {
	// return true;
	// } else {
	// return false;
	// }
	// }

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