package kr.co.esjee.ranker.webapp.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import kr.co.esjee.ranker.webapp.AppConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = AppConstant.MOVIE, type = AppConstant.DOC)
public class Movie {

	@Id
	private long id;	
	private String tid;
	private String movieId;
	private String title;
	private String orgTitle;
	private String score;
	private String genre;
	private String nation;
	private String grade;
	private String runTime;
	private String openDay;	
	private String actor;
	private String role;
	private String director;
	private String synopsis;
	private String makingNote;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}