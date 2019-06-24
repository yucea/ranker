package kr.co.esjee.ranker.webapp.model;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Score {

	@Field(type = FieldType.Long)
	private long id;
	@Field(type = FieldType.Text)
	private String title;
	@Field(type = FieldType.Text)
	private String director;
	@Field(type = FieldType.Text)
	private String actor;
	@Field(type = FieldType.Text)
	private String genre;
	@Field(type = FieldType.Integer)
	private int raking;
	@Field(type = FieldType.Double)
	private double score;

}