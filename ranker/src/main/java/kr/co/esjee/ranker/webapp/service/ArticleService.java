package kr.co.esjee.ranker.webapp.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.esjee.ranker.webapp.model.Article;
import kr.co.esjee.ranker.webapp.repository.ArticleRepository;

@Service
public class ArticleService extends AppService {

	@Autowired
	private ArticleRepository repository;

	public Page<Article> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}

	public Article findById(long id) {
		Optional<Article> optional = repository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		} else {
			throw new IllegalArgumentException("Item not found : " + id);
		}
	}

	public Article save(Article article) {
		if (article.getId() == 0) {
			article.setId(super.getNextId(INDICES.article));
		}

		return repository.save(article);
	}

	public void remove(long id) {
		repository.deleteById(id);
	}

}
