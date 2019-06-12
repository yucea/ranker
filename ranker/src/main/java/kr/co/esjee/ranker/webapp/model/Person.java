package kr.co.esjee.ranker.webapp.model;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import kr.co.esjee.ranker.webapp.AppConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = AppConstant.PERSON, type = AppConstant.DOC)

@Setting(settingPath = "/elasticsearch/person_settings.json")
@Mapping(mappingPath = "/elasticsearch/person_mappings.json")
public class Person {

	@Id
	private long id;
	private String pid;
	private String name;
	private String birthday;
	private String profile;
	private List<PersonFilmo> filmo;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}