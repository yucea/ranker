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
	
	// List URL
	@Field(type = FieldType.Text)
	private String listUrl = "https://movie.naver.com/movie/sdb/browsing/bmovie_open.nhn";
	
	// Movie URL
	@Field(type = FieldType.Text)
	private String basicUrl = "https://movie.naver.com/movie/bi/mi/basic.nhn?code=162876";
	
	// Person URL
	@Field(type = FieldType.Text)
	private String crewUrl = "https://movie.naver.com/movie/bi/mi/detail.nhn";	
	
	// Filmo URL
	@Field(type = FieldType.Text)
	private String FilmoUrl = "https://movie.naver.com/movie/bi/pi/filmoMission.nhn?year=0&totalCount=1000&page=1000&peopleCode=";
	
	// Key Parameter
	@Field(type = FieldType.Text)
	private String keyParam = "code";
	
	@Field(type = FieldType.Text)
	private String pagingParam = "page";
	
	@Field(type = FieldType.Text)
	private Integer maxCount = 20;
	
	// 필요 없는 요소
	@Field(type = FieldType.Text)
	private String removeAtrb = "div.wide_info_area";
	
	// 영화 제목
	@Field(type = FieldType.Text)
	private String titleAtrb = "h3.h_movie a";
	
	// 영화 원제목
	@Field(type = FieldType.Text)
	private String orgTitleAtrb = "strong.h_movie2";
	
	// 평점
	@Field(type = FieldType.Text)
	private String scoreAtrb = "a#pointNetizenPersentBasic em";
	
	// 상영시간
	@Field(type = FieldType.Text)
	private String runTimeAtrb = "dl.info_spec span";
	
	// 장르
	@Field(type = FieldType.Text)
	private String[] genreAtrb = {"href", "/movie/sdb/browsing/bmovie.nhn?genre="};
	
	// 제작국가
	@Field(type = FieldType.Text)
	private String[] nationAtrb = {"href", "/movie/sdb/browsing/bmovie.nhn?nation="};
	
	// 등급
	@Field(type = FieldType.Text)
	private String[] gradeAtrb = {"href", "/movie/sdb/browsing/bmovie.nhn?grade="};
	
	// 개봉일
	@Field(type = FieldType.Text)
	private String[] openDayAtrb = {"href", "/movie/sdb/browsing/bmovie.nhn?open="};
	
	// 시놉시스
	@Field(type = FieldType.Text)
	private String synopsisAtrb = "div.story_area";
	
	// 제작노트
	@Field(type = FieldType.Text)
	private String makingNoteAtrb = "div.making_note p.con_tx";
	
	// 배우 이름	
	@Field(type = FieldType.Text)
	private String actorAtrb = "ul.lst_people li div.p_info a.k_name";
	
	// 배우 역활
	@Field(type = FieldType.Text)
	private String roleAtrb = "ul.lst_people li div.p_info div.part p.pe_cmt span";
	
	// 감독 이름
	@Field(type = FieldType.Text)
	private String directorAtrb = "div.dir_product a.k_name";	
	
	// 출연자
	@Field(type = FieldType.Text)
	private String crewNameAtrb = "h3.h_movie a";
	
	// 출연자 프로필
	@Field(type = FieldType.Text)
	private String crewProfileAtrb = "div.con_tx";
	
	// 출연자 생년월일
	@Field(type = FieldType.Text)
	private String crewBirthdayAtrb = "dl.info_spec dd";
	
	// 출연 목록
	@Field(type = FieldType.Text)
	private String filmoListAtrb = "div.pilmo_info";
	
	// 출연 제목
	@Field(type = FieldType.Text)
	private String filmoTitleAtrb = "strong.pilmo_tit a";
	
	// 출연 연도
	@Field(type = FieldType.Text)
	private String filmoYearAtrb = "p.pilmo_genre a";

	@Field(type = FieldType.Integer)
	private int[] week; // 일요일 : 1, 토요일 : 7, 매주 : 0

	@Field(type = FieldType.Integer)
	private int[] month; // 1-12, 매월 : 0

	@Field(type = FieldType.Integer)
	private int[] day; // 1-31, 매일 : 0

	@Field(type = FieldType.Integer)
	private int[] hour; // 0-23, 매시간 : 24

	@Field(type = FieldType.Integer)
	private int[] minute; // 0-59, 매분 : 60

	@Field(type = FieldType.Integer)
	private int repeat; // 반복회수 - 한번만 실행 : 1, 반복실행 : 0

	@Field(type = FieldType.Boolean)
	private boolean finish;

	//@Field(type = FieldType.Keyword)
	//private String lastRuntime;

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
