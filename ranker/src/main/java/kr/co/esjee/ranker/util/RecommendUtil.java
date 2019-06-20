package kr.co.esjee.ranker.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import kr.co.esjee.ranker.webapp.AppConstant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecommendUtil implements AppConstant {

	public static Map<String, Integer> ratings = new LinkedHashMap<String, Integer>();

	static {
		ratings.put("19세관람가", 19);
		ratings.put("18세미만불가", 19);
		ratings.put("제한상영가", 19);
		ratings.put("제한관람가", 19);
		ratings.put("전체관람가NR", 19);
		ratings.put("전체관람가PG", 19);
		ratings.put("전체관람가G", 19);
		ratings.put("전체관람가PG-13", 19);
		ratings.put("NR", 19);
		ratings.put("NotRated", 19);

		ratings.put("R", 18);
		ratings.put("NC-17", 18);
		ratings.put("NC17", 18);
		ratings.put("XNC17", 18);
		ratings.put("18세미만인자는관람할수없는등급", 18);
		ratings.put("18세관람가청소년관람불가", 18);
		ratings.put("18세가청소년불가", 18);
		ratings.put("18세관람가", 18);
		ratings.put("미성년자관람불가", 18);
		ratings.put("청소년관람불가", 18);
		ratings.put("청소년관람불가R", 18);
		ratings.put("청소년관람불가NR", 18);
		ratings.put("청소년관람불가NC-17", 18);
		ratings.put("청소년관람불가PG-13", 18);
		ratings.put("청소년이용불가", 18);
		ratings.put("청소년불가", 18);
		ratings.put("연소자불가", 18);
		ratings.put("연소자관람불가", 18);

		ratings.put("고등학생이상관람가", 15);
		ratings.put("15세미만인자는관람할수없는등급", 15);
		ratings.put("15세이상관람가", 15);
		ratings.put("15세미만불가", 15);
		ratings.put("15세이상가", 15);
		ratings.put("15세가", 15);
		ratings.put("고등학생가", 15);
		ratings.put("15세관람가", 15);
		ratings.put("15세관람가NR", 15);
		ratings.put("15세관람가R", 15);
		ratings.put("15세관람가PG-13", 15);
		ratings.put("PG-13", 15);
		ratings.put("PG13", 15);

		ratings.put("중학생이상", 12);
		ratings.put("중학생이상관람가", 12);
		ratings.put("중학생이상연령층관람가", 12);
		ratings.put("12세미만인자는관람할수없는등급", 12);
		ratings.put("12세미만불가", 12);
		ratings.put("12세이상관람가", 12);
		ratings.put("12세관람가PG", 12);
		ratings.put("12세관람가NR", 12);
		ratings.put("12세관람가PG-13", 12);
		ratings.put("12세이상가", 12);
		ratings.put("12세관람가R", 12);
		ratings.put("12세가", 12);

		ratings.put("중학생가", 12);
		ratings.put("국민학생관람불가", 12);
		ratings.put("국민학생이상관람가", 12);
		ratings.put("12세관람가", 12);

		ratings.put("*", 0);
		ratings.put("G", 0);
		ratings.put("PG", 0);
		ratings.put("모두관람가", 0);
		ratings.put("미성년자관람가", 0);
		ratings.put("전체관람가", 0);
		ratings.put("전체가", 0);
		ratings.put("연소자관람가", 0);
		ratings.put("모든관람객이관람할수있는등급", 0);
	}

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

	public static int getRating(String id, String rating) {
		String text = StringUtils.deleteWhitespace(rating);
		if (StringUtils.isEmpty(text))
			return 19;

		String key = StringUtils.substringBefore(StringUtils.substringBefore(text, "("), "<");

		if (StringUtils.isNumeric(key)) {
			return Integer.parseInt(key);
		}

		if (!ratings.containsKey(key)) {
			int[] rate = { 19 };
			ratings.forEach((k, v) -> {
				if (text.contains(k)) {
					rate[0] = v;
					return;
				}
			});

			log.warn("{} - {}, {}", id, text, rate[0]);
			return rate[0];
		}

		return ratings.get(key);
	}

	public static double getScore(double score) {
		return Math.round(score * 100d) / 100d;
	}

	/**
	 * 같은 경우 : 1, 하나라도 포함된 경우 : 0.9, 2개 이상이면서 하나라도 같은 경우 : 0.8, 다른경우 : 0.7
	 * 
	 * @param genre
	 * @param target
	 * @return
	 */
	public static double getGenreScore(String genre, String target) {
		double variable = 0.4;

		if (StringUtils.isEmpty(genre) || genre.equals(target)) {
			variable = 1;
		} else if (StringUtils.isEmpty(target)) {
			variable = 0.4;
		} else if (StringUtils.contains(genre, target) || StringUtils.contains(target, genre)) {
			variable = 0.8;
		} else {
			List<String> genres = Arrays.asList(genre.split(","));
			int size = genres.size();
			if (size > 1) {
				int count = 0;
				for (String g : genres) {
					if (StringUtils.contains(target, g)) {
						count++;
					}
				}

				if (size == count) {
					if (target.split(",").length == size) {
						variable = 1;
					} else {
						variable = 0.8;
					}
				} else if (count == 0) {
					variable = 0.4;
				} else {
					variable = 0.6;
				}
			} else {
				variable = 0.4;
			}
		}

		return variable;
	}

	public static double getWeighting(String openDay, int rating, double starScore) {
		return getDateScore(openDay) + getRatingScore(rating) + (starScore * 0.5);
	}

	public static int getDateScore(String source) {
		if (StringUtils.isNotEmpty(source)) {
			try {
				Date date = CalendarUtil.getDate(source);
				if (CalendarUtil.within3Months(date)) {
					return 3;
				} else if (CalendarUtil.withinYear(date)) {
					return 2;
				} else if (CalendarUtil.withinTenYear(date)) {
					return 1;
				}
			} catch (Exception e) {
			}
		}

		return 0;
	}

	private static double getRatingScore(int rating) {
		if (rating == 0)
			return 2;
		else if (rating <= 13)
			return 1.5;
		else if (rating <= 15)
			return 1;
		else
			return -1;
	}
}
