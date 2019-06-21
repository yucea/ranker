package kr.co.esjee.ranker.webapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonFilmo {
	
	private String movieId;
	private String movieTitle;
	private String movieYear;
	private String movieDirector;
	
}