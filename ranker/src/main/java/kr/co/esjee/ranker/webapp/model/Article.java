package kr.co.esjee.ranker.webapp.model;

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
@Document(indexName = AppConstant.ARTICLE, type = AppConstant.DOC)
@Setting(settingPath = "/elasticsearch/article_settings.json")
@Mapping(mappingPath = "/elasticsearch/article_mappings.json")
public class Article {

	@Id
	private long id;
	private String title;
	private String content;
	private String writer;
	private String created;

}
