package kr.co.esjee.ranker.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TermResult {

	private String id;
	private String field;
	private String word;
	private double score;
	private double original;

}
