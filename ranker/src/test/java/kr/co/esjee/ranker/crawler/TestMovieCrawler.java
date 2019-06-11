package kr.co.esjee.ranker.crawler;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;
	
//@RunWith(SpringRunner.class)
//@SpringBootTest
@Slf4j
public class TestMovieCrawler {

	@Test
	public void testCrawler(){ 
		
		String connectUrl = "https://movie.naver.com/movie/bi/mi/basic.nhn?code=164125";
		String removeAtrb = "div.wide_info_area";
		String titleAtrb = "h3.h_movie a";
		String orgTitleAtrb = "strong.h_movie2";
		String scoreAtrb = "a.ntz_score div.star_score em";
		
		String[] genreAtrb = {"href", "/movie/sdb/browsing/bmovie.nhn?genre="};
		String[] nationAtrb = {"href", "/movie/sdb/browsing/bmovie.nhn?nation="};
		String[] gradeAtrb = {"href", "/movie/sdb/browsing/bmovie.nhn?grade="};
		String[] openDayAtrb = {"href", "/movie/sdb/browsing/bmovie.nhn?open="};
		
		try {
			MovieCrawler movieCrawler = new MovieCrawler();
			
			movieCrawler.execute(connectUrl, removeAtrb, titleAtrb, orgTitleAtrb, scoreAtrb, genreAtrb, nationAtrb, gradeAtrb, openDayAtrb);
			
		} catch (Exception e) {
			log.error("Error = {}", e.getLocalizedMessage());
		}
	}
}