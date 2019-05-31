package kr.co.esjee.ranker.util;

public class JobUtil {

	public static String getRoutingKey(String queueName) {
		return String.format("k.c.e.%s", queueName.trim().replaceAll("-", ".").replaceAll("_", "."));
	}

}
