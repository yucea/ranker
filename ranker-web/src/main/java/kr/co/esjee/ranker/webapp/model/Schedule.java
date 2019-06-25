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
	
	/* Directory Movie List */
	private String movieDirUrl = "";
	
	private String movieDirAtrb = "";

	private String movieDirPage = "";
	
	private Integer startYear;
	
	private Integer endYear;
	
	
	/* Movie */
	private String movieListUrl = "";
	
	private String movieUrlAtrb = ""; 
	
	private String movieUrl = "";
	
	private String personUrl = "";	
	
	private String filmoUrl = "";
	
	private String removeAtrb = "";
	
	private String movieKey = "";
	
	private String titleAtrb = "";
	
	private String orgTitleAtrb = "";
	
	private String scoreAtrb = "";
	
	private String runTimeAtrb = "";
	
	private String[] genreAtrb;
	
	private String[] nationAtrb;
	
	private String[] gradeAtrb;
	
	private String[] openDayAtrb;
	
	private String synopsisAtrb = "";
	
	private String makingNoteAtrb = "";
	
	
	/* Person */
	private String actorAtrb = "";
	
	private String roleAtrb = "";
	
	private String directorAtrb = "";	
	
	private String crewNameAtrb = "";
	
	private String crewProfileAtrb = "";
	
	private String crewBirthdayAtrb = "";
		
	private String filmoListAtrb = "";
	
	private String filmoTitleAtrb = "";
	
	private String filmoYearAtrb = "";
	
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