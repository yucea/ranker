package kr.co.esjee.ranker.webapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import kr.co.esjee.ranker.webapp.AppConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = AppConstant.ARTICLE, type = AppConstant.DOC, replicas = 2)
public class Article {

	@Id
	private long id;

	@Field(type = FieldType.Keyword)
	private String title;

	@Field(type = FieldType.Text)
	private String content;

	@Field(type = FieldType.Text)
	private String writer;

	@Field(type = FieldType.Text)
	private String created;

}
