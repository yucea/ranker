package kr.co.esjee.ranker.webapp.model;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Keyword {

	private String word;
	private double score;
	private Map<String, Double> source = new LinkedHashMap<>();

}
