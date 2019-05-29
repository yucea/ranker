package kr.co.esjee.ranker.webapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import kr.co.esjee.ranker.webapp.AppConstant;
import lombok.Data;

@Data
@Document(indexName = AppConstant.SEQUENCE, type = AppConstant.DOC, replicas = 2)
public class Sequence {

	@Id
	private long id;

	@Field(type = FieldType.Keyword)
	private String name;

	@Field(type = FieldType.Long)
	private long value;

	public void increase() {
		value++;
	}

}
