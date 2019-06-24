package kr.co.esjee.ranker.webapp.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import kr.co.esjee.ranker.webapp.AppConstant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(indexName = AppConstant.RECOMMEND_M2K, type = AppConstant.DOC)
public class M2KNode {

	@Id
	private long id;
	private String tid;
	private String title;
	private String director;
	private String actor;
	private String role;
	private double starScore;
	private String genre;
	private int rating; // 연령대
	private String openDay;
	private String[] fields;
	private Keyword[] keywords;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}