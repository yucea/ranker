package kr.co.esjee.ranker.webapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import kr.co.esjee.ranker.webapp.AppConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = AppConstant.RECOMMEND_K2M, type = AppConstant.DOC)
public class K2MNode {

	@Id
	private int id;
	private String key;
	private Score[] scores;

}