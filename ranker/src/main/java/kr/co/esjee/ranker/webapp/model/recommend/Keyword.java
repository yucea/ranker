package kr.co.esjee.ranker.webapp.model.recommend;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Data;

@Data
public class Keyword {

	@Field(type = FieldType.Keyword)
	private String word;
	@Field(type = FieldType.Double)
	private double score;
	@Field(type = FieldType.Nested)
	private Map<String, Double> source = new LinkedHashMap<>();

	public Keyword(String word, double score) {
		this.word = word;
		this.score = score;
	}

}
