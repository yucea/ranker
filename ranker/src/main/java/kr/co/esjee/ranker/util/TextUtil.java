package kr.co.esjee.ranker.util;

import org.apache.commons.lang3.StringUtils;

import kr.co.esjee.ranker.webapp.AppConstant;

public class TextUtil implements AppConstant {

	private static final String MATCH_PATTERN = "[^\uAC00-\uD7A3xfe_0-9a-zA-Z\\s]";

	public static String getPlanText(String text) {
		if (StringUtils.isEmpty(text))
			return "";
		else
			return text.trim().replaceAll("\t+", " ")
					.replaceAll("\r\n+", " ")
					.replaceAll("\n+", " ")
					.replaceAll(MATCH_PATTERN, " ")
					.replaceAll(" +", " ");
	}

	public static String getFileSubject(String filename) {
		return getPlanText(StringUtils.substringBeforeLast(filename, "."));
	}

	public static String getExtension(String filename) {
		return filename.contains(".") ? StringUtils.substringAfterLast(filename, ".").toLowerCase() : null;
	}

}
