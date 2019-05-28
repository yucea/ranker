package kr.co.esjee.ranker.webapp.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import kr.co.esjee.ranker.config.AppConfig;
import kr.co.esjee.ranker.text.Wordrank;
import kr.co.esjee.ranker.text.Wordrank.Word;

@Service
public class WordrankService {

	public List<Word> execute(String[] docs, int minCount, int maxLength, String[] corpus, String exclude, boolean match) throws IOException {
		Wordrank wordrank = new Wordrank(minCount, maxLength);
		wordrank.setStopwords(AppConfig.getStopwords());
		wordrank.setCorpus(Lists.newArrayList(corpus));
		wordrank.setExclude(exclude, match);

		return wordrank.execute(docs);
	}

}
