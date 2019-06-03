package kr.co.esjee.ranker.webapp.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.esjee.ranker.crawler.Crawler;
import kr.co.esjee.ranker.webapp.model.Article;

@Service
public class CrawlerService {
	
	@Autowired
	private ArticleService articleService;

	public List<Article> execute(String url, String[] urlParams, String idColumn, String dateColumn, String categoryColumn,
			String listAtrb, String listEachAtrb, String titleAtrb, String contentAtrb) throws IOException {
		
		Crawler crawler = new Crawler();
		
		List<Article> articleList = crawler.execute(url, urlParams, idColumn, dateColumn,
				categoryColumn, listAtrb, listEachAtrb, titleAtrb, contentAtrb);
		
		if(articleList.size() > 0) {
			for(Article article : articleList) {
				articleService.save(article);
			}
		}
		
		return articleList;
	}
}