package kr.co.esjee.ranker.webapp.model.recommend;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kr.co.esjee.ranker.webapp.AppConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
	private double starScore; // starscore
	@Field(type = FieldType.Text)
	private String genre;
	@Field(type = FieldType.Integer)
	private int rating; // 연령대
	@Field(type = FieldType.Text)
	private String openDay;
	@Field(type = FieldType.Text)
	private String[] fields;
	@Field(type = FieldType.Nested)
	private Keyword[] keywords;

	@JsonIgnore
	private Map<String, Double> scoreMap = null;

	@JsonIgnore
	public Map<String, Double> getScoreMap() {
		if (scoreMap == null) {
			scoreMap = new HashMap<String, Double>();
			for (Keyword k : keywords) {
				scoreMap.put(k.getWord(), k.getScore());
			}
		}

		return scoreMap;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}