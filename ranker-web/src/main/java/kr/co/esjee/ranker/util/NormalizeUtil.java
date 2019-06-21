package kr.co.esjee.ranker.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import kr.co.esjee.ranker.webapp.AppConstant;

public class NormalizeUtil implements AppConstant {

	public static Map<String, Double> normalize(Map<String, Double> data) {
		double maxScore = data.get(Collections.max(data.entrySet(), Map.Entry.comparingByValue()).getKey());
		for (String key : data.keySet()) {
			double score = Math.round(((data.get(key) / maxScore) - 0.01) * 100d) / 100d;
			data.put(key, score);
		}

		return data;
	}

	public static List<JSONObject> normalizeToJson(Map<String, Double> data) {
		Map<String, Double> map = normalize(data);

		List<JSONObject> result = new ArrayList<>();
		for (String key : map.keySet()) {
			JSONObject obj = new JSONObject();
			obj.put(KEY, key);
			obj.put(SCORE, map.get(key));
			result.add(obj);
		}

		return result;
	}

}
