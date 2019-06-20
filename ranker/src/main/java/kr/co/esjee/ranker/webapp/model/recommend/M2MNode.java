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
@Document(indexName = AppConstant.RECOMMEND_M2M, type = AppConstant.DOC)
@Setting(settingPath = "/elasticsearch/default_settings.json")
public class M2MNode {

	@Id
	private long id;
	@Field(type = FieldType.Keyword)
	private String tid;
	@Field(type = FieldType.Text)
	private String title;
	@Field(type = FieldType.Text)
	private String genre;
	@Field(type = FieldType.Text)
	private int rating; // 연령대
	@Field(type = FieldType.Nested)
	private Score[] scores;

}
