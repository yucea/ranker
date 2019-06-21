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
@Document(indexName = AppConstant.RECOMMEND_M2M, type = AppConstant.DOC)
public class M2MNode {

	@Id
	private long id;
	private String tid;
	private String title;
	private String genre;
	private int rating; // 연령대
	private Score[] scores;

}
