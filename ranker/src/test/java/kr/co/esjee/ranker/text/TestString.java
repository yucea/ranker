package kr.co.esjee.ranker.text;

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
}
