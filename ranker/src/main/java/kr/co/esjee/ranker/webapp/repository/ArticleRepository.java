package kr.co.esjee.ranker.webapp.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import kr.co.esjee.ranker.webapp.model.Article;

public interface ArticleRepository extends ElasticsearchRepository<Article, Long> {

}
