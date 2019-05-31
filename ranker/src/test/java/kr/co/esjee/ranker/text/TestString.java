package kr.co.esjee.ranker.text;

import java.util.Calendar;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestString {

	private static final String MATCH_PATTERN = "[^\uAC00-\uD7A3xfe_0-9a-zA-Z\\s]";

	@Test
	public void text() {
		String text = "미녀와_야수";

		log.info(text.replaceAll(MATCH_PATTERN, " "));
	}

	@Test
	public void calendar() {
		Calendar calendar = Calendar.getInstance();

		log.info("현재 월의 몇째주 : {}", calendar.get(Calendar.WEEK_OF_MONTH));
		log.info("현재 년도의 몇번째 주 : {}", calendar.get(Calendar.WEEK_OF_YEAR));
		log.info("현재 요일 : {}", calendar.get(Calendar.DAY_OF_WEEK));

		Calendar calendar2 = Calendar.getInstance();
		calendar2.add(Calendar.DATE, -2);

		log.info("{}", calendar2.getTime());

		log.info("{}", Math.abs(calendar.getTimeInMillis() - calendar2.getTimeInMillis()) / (1000 * 60 * 60));
	}

}
