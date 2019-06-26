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
	private String scheduleName = "";
	
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
	private String movieListUrl = "";
	
	@Field(type = FieldType.Text)
	private String movieUrlAtrb = "";
	
	@Field(type = FieldType.Text)
	private String movieUrl = "";
	
	@Field(type = FieldType.Text)
	private String personUrl = "";	
		
	@Field(type = FieldType.Text)
	private String filmoUrl = "";
	
	@Field(type = FieldType.Text)
	private String removeAtrb = "";
	
	@Field(type = FieldType.Text)
	private String movieKey = "";
	
	@Field(type = FieldType.Text)
	private String titleAtrb = "";
	
	@Field(type = FieldType.Text)
	private String orgTitleAtrb = "";
	
	@Field(type = FieldType.Text)
	private String scoreAtrb = "";
	
	@Field(type = FieldType.Text)
	private String runTimeAtrb = "";
	
	@Field(type = FieldType.Text)
	private String[] genreAtrb;
	
	@Field(type = FieldType.Text)
	private String[] nationAtrb;
	
	@Field(type = FieldType.Text)
	private String[] gradeAtrb;
	
	@Field(type = FieldType.Text)
	private String[] openDayAtrb;
	
	@Field(type = FieldType.Text)
	private String synopsisAtrb = "";
	
	@Field(type = FieldType.Text)
	private String makingNoteAtrb = "";
	
	
	/* Person */
	@Field(type = FieldType.Text)
	private String actorAtrb = "";
	
	@Field(type = FieldType.Text)
	private String roleAtrb = "";
	
	@Field(type = FieldType.Text)
	private String directorAtrb = "";	
	
	@Field(type = FieldType.Text)
	private String crewNameAtrb = "";
	
	@Field(type = FieldType.Text)
	private String crewProfileAtrb = "";
	
	@Field(type = FieldType.Text)
	private String crewBirthdayAtrb = "";
		
	@Field(type = FieldType.Text)
	private String filmoListAtrb = "";
	
	@Field(type = FieldType.Text)
	private String filmoTitleAtrb = "";
	
	@Field(type = FieldType.Text)
	private String filmoYearAtrb = "";
	
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