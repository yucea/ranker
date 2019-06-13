package kr.co.esjee.ranker.webapp.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieInfo {
	
	Movie movieInfo;
	List<Person> personInfo;
	
}