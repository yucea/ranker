package kr.co.esjee.ranker.util;

import org.apache.commons.lang3.StringUtils;

import kr.co.esjee.ranker.webapp.AppConstant;

public class TextUtil implements AppConstant {

	private static final String MATCH_PATTERN = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";

	public static String getPureString(String text) {
		if (StringUtils.isNotEmpty(text)) {
			return getPlanText(text.replaceAll(MATCH_PATTERN, " "));
		} else {
			return "";
		}
	}

	public static String getPlanText(String text) {
		if (StringUtils.isEmpty(text))
			return "";
		else
			return text.trim().replaceAll("\t+", " ")
					.replaceAll("\r\n+", " ")
					.replaceAll("\n+", " ")
					.replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", " ")
					.replaceAll(" +", " ");
	}

	public static String getFileSubject(String filename) {
		return getPlanText(StringUtils.substringBeforeLast(filename, "."));
	}

	public static String getExtension(String filename) {
		if (filename.contains("."))
			return StringUtils.substringAfterLast(filename, ".").toLowerCase();
		else
			return null;
	}

}
