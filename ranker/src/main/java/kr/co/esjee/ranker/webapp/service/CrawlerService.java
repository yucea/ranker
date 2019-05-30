package kr.co.esjee.ranker.webapp.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.esjee.ranker.crawler.Crawler;
import kr.co.esjee.ranker.crawler.Crawler.CrawlerVO;

@Service
public class CrawlerService {

	public List<CrawlerVO> execute(String url, String listAtrb, String listEachAtrb, String titleAtrb, String contentAtrb) throws IOException {
		Crawler crawler = new Crawler();
		return crawler.execute(url, listAtrb, listEachAtrb, titleAtrb, contentAtrb);
	}
}