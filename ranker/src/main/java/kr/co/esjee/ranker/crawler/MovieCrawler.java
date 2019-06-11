package kr.co.esjee.ranker.crawler;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import kr.co.esjee.ranker.webapp.AppConstant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MovieCrawler implements AppConstant {
	
	private final static String CONTENT_TYPE = "application/json;charset=UTF-8";
	private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:65.0) Gecko/20100101 Firefox/65.0";
	
	public Document jsoupConnect(String connectUrl) {
		
		Document document = null;
		
		try {
			document = Jsoup.connect(connectUrl)
					.header("Content-Type", CONTENT_TYPE)
					.userAgent(USER_AGENT)
					.maxBodySize(Integer.MAX_VALUE)
					.ignoreContentType(true)
					.get();
			
		} catch (IOException ioe) {
			log.error("Jsoup Connect Error = {}", ioe.getLocalizedMessage());
		}
		
		return document;
	}
	
	
	public void execute(String connectUrl, String removeAtrb, String titleAtrb, String orgTitleAtrb,
			String scoreAtrb, String[] genreAtrb, String[] nationAtrb, String[] gradeAtrb, String[] openDayAtrb) {
		
		Document basicInfoDoc = jsoupConnect(connectUrl);
		
		if(basicInfoDoc == null) {
			try {

				Thread.sleep(10000);
				basicInfoDoc = jsoupConnect(connectUrl);
				
			} catch (InterruptedException ie) {
				log.error("doc is null = {}", ie.getLocalizedMessage());
			}
		}
		
		// 제거할 요소가 있으면 제거한다.
		if(StringUtils.isNotEmpty(removeAtrb)) {
			basicInfoDoc.select(removeAtrb).remove();
		}

		// 제목
		String title = basicInfoDoc.select(titleAtrb).first().text();
		System.out.println("title : " + title);
		
		// 원제목
		String orgTitle = basicInfoDoc.select(orgTitleAtrb).text();
		System.out.println("orgTitle : " + orgTitle);
		
		// 평점
		String score = basicInfoDoc.select(scoreAtrb).text();
		System.out.println("score : " + StringUtils.replace(score, " ", ""));
		
		// 장르
		String genre = (genreAtrb.length > 1) ?
				basicInfoDoc.getElementsByAttributeValueContaining(genreAtrb[0], genreAtrb[1]).text() :
					basicInfoDoc.select(genreAtrb[0]).text();
				
		System.out.println("genre : " + StringUtils.replace(genre, " ", ","));	
		
		
		// 제작 국가
		String nation = (nationAtrb.length > 1) ? 
				basicInfoDoc.getElementsByAttributeValueContaining(nationAtrb[0], nationAtrb[1]).text() : 
					basicInfoDoc.select(nationAtrb[0]).text();
				
		System.out.println("nation : " + nation);		
		
		// 등급
		String grade = (gradeAtrb.length > 1) ?
				basicInfoDoc.getElementsByAttributeValueContaining(gradeAtrb[0], gradeAtrb[1]).text() :
					basicInfoDoc.select(gradeAtrb[0]).text();
				
		System.out.println("grade : " + grade);
				
		// 개봉일자
		String openDay = (openDayAtrb.length > 1) ?
				basicInfoDoc.getElementsByAttributeValueContaining(openDayAtrb[0], openDayAtrb[1]).text() :
					basicInfoDoc.select(openDayAtrb[0]).text();
				
		System.out.println("openDay : " + StringUtils.replace(StringUtils.replace(openDay, " ", ""), ".", ""));
		
		// 시놉시스
		String synopsis = basicInfoDoc.select("div.story_area").text();
		System.out.println("synopsis : " + synopsis);		
		
		// 제작노트
		String makingNote = basicInfoDoc.select("div.making_note p.con_tx").text();
		System.out.println("makingNote : " + makingNote);
		
	}
	
	public void basicInfo(Document basicInfoDoc) {
		
	}
}