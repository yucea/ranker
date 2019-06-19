package kr.co.esjee.ranker.webapp.model.recommend;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import kr.co.esjee.ranker.webapp.AppConstant;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Document(indexName = AppConstant.MV_M2K, type = AppConstant.DOC)
@Setting(settingPath = "/elasticsearch/default_settings.json")
public class M2KNode implements Node {

	@Id
	private long id;
	@Field(type = FieldType.Keyword)
	private String tid;
	@Field(type = FieldType.Text)
	private String title;
	@Field(type = FieldType.Text)
	private String director;
	@Field(type = FieldType.Text)
	private String actor;
	@Field(type = FieldType.Text)
	private String role;
	@Field(type = FieldType.Double)
	private double score; // starscore
	@Field(type = FieldType.Text)
	private String genre;
	@Field(type = FieldType.Text)
	private String grade; // 연령대
	@Field(type = FieldType.Text)
	private String openDay;
	@Field(type = FieldType.Text)
	private String[] fields;
	@Field(type = FieldType.Nested)
	private Keyword[] keyowrds;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}