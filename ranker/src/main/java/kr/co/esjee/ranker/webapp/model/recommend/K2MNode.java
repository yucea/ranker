package kr.co.esjee.ranker.webapp.model.recommend;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import kr.co.esjee.ranker.webapp.AppConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = AppConstant.RECOMMEND_K2M, type = AppConstant.DOC)
@Setting(settingPath = "/elasticsearch/default_settings.json")
public class K2MNode {

	@Id
	private int id;
	@Field(type = FieldType.Keyword)
	private String key;
	@Field(type = FieldType.Nested)
	private Score[] scores;

}
