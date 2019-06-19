package kr.co.esjee.ranker.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import kr.co.esjee.ranker.webapp.AppConstant;

public class RecommendUtil implements AppConstant {

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

	public static Map<String, Object> getFieldNameValues(final Object obj, boolean publicOnly) throws Exception {
		Class<? extends Object> c1 = obj.getClass();
		Map<String, Object> map = new HashMap<String, Object>();
		Field[] fields = c1.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			String name = fields[i].getName();
			if (publicOnly) {
				if (Modifier.isPublic(fields[i].getModifiers())) {
					Object value = fields[i].get(obj);
					map.put(name, value);
				}
			} else {
				fields[i].setAccessible(true);
				Object value = fields[i].get(obj);
				map.put(name, value);
			}
		}

		return map;
	}

	public static boolean isFieldValueString(final Object obj, String text) throws Exception {
		return StringUtils.join(getFieldNameValues(obj, false).values(), ",").contains(text);
	}
}
