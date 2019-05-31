package kr.co.esjee.ranker.text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import kr.co.esjee.ranker.util.TextUtil;
import kr.co.esjee.ranker.webapp.AppConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Wordrank implements AppConstant {

	private static final String SEPARATOR = "_";
	private static final String LEFT = ":L";
	private static final String RIGHT = ":R";

	private int minCount;
	private int maxLength;
	private Map<String, Integer> vocabulary = new HashMap<String, Integer>();
	private Map<String, Integer> keymap = new HashMap<String, Integer>();
	private Map<String, Integer> links = new HashMap<String, Integer>();

	private List<String> stopwords = new ArrayList<String>();
	private Map<String, String> themes = new HashMap<String, String>();

	private String[] exclude;
	private boolean match = true; // 제외단어 일치 여부 - true : 일치, false : 포함
	private boolean verbose = false;
	private List<String> corpus = new ArrayList<String>(); // 단어 사전

	public Wordrank(int minCount, int maxLength) {
		this(minCount, maxLength, false);
	}

	/**
	 * @param minCount - 최소 중복횟수
	 * @param maxLength - 최대 단어길이
	 * @param verbose - 로그여부
	 */
	public Wordrank(int minCount, int maxLength, boolean verbose) {
		this.minCount = minCount;
		this.maxLength = maxLength;
		this.verbose = verbose;
	}

	public List<Word> execute(String... docs) {
		return execute(Lists.newArrayList(docs));
	}

	public List<Word> execute(List<String> docs) {
		this.vocabulary.clear();
		this.keymap.clear();

		// corpus, theme 적용
		List<String> data = this.preprocessor(docs);
		// 사전 생성
		this.scanVocabulary(data);
		if (verbose)
			this.printVocabulary();

		// :L:R 형태로 변환
		this.matrix(data);
		// key, 반복회수 형태로 추출
		Map<String, Integer> source = this.makeSource();
		// 반복횟수가 같은 단어 병합
		this.merge(source);
		// corpus, theme 처리
		Map<String, Integer> result = this.postprocessor(source);
		// score 표준화 & sorting
		return this.normalizing(result);
	}

	private List<String> preprocessor(List<String> docs) {
		for (String key : corpus) {
			themes.put(key, key);
		}

		List<String> result = new ArrayList<>();
		for (String doc : docs) {
			for (String key : themes.keySet()) {
				doc = StringUtils.replace(doc, key, StringUtils.replace(themes.get(key), " ", SEPARATOR));
			}

			result.add(doc);
		}

		return result;
	}

	private Map<String, Integer> postprocessor(Map<String, Integer> source) {
		return source.entrySet().stream()
				.collect(Collectors.toMap(
						x -> themes.containsKey(StringUtils.replace(x.getKey(), SEPARATOR, " ")) ? themes.get(StringUtils.replace(x.getKey(), SEPARATOR, " ")) : StringUtils.replace(x.getKey(), SEPARATOR, " "),
						x -> x.getValue()));
	}

	private void printVocabulary() {
		log.info("--- Vocabulary Start ---");
		Map<String, Integer> result = vocabulary.entrySet().stream()
				// .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> x, LinkedHashMap::new));

		log.info("{}", result);
		log.info("--- Vocabulary End ---");
	}

	public Map<String, Integer> makeSource() {
		Map<String, Integer> data = new HashMap<String, Integer>();

		for (String k : links.keySet()) {
			String key = StringUtils.replace(StringUtils.replace(k, LEFT, EMPTY), RIGHT, EMPTY);
			int value = this.vocabulary.containsKey(key + LEFT) ? this.vocabulary.get(key + LEFT) : 0;

			String lkey = StringUtils.substringBefore(k, LEFT);
			if (this.vocabulary.containsKey(lkey + LEFT)) {
				if (this.vocabulary.get(lkey + LEFT) > value) {
					key = lkey;
					value = this.vocabulary.get(lkey + LEFT);
				}
			}

			data.put(key, data.containsKey(key) ? Math.max(data.get(key), value) : value);
		}

		Map<String, Integer> source = data.entrySet().stream()
				.filter(x -> !this.stopwords.contains(x.getKey())
						&& !this.themes.containsKey(x.getKey())
						&& this.isNotExclude(x.getKey())
						&& x.getValue() >= this.minCount
						&& x.getKey().length() > 1)
				// .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> x, LinkedHashMap::new));

		return source;
	}

	private boolean isNotExclude(String key) {
		if (StringUtils.isNotEmpty(key) && this.exclude != null) {
			for (String str : this.exclude) {
				if (this.match ? key.equals(str) : key.startsWith(str) || key.endsWith(str))
					return false;
			}
		}

		return true;
	}

	// 단어와 단어를 포함한 단어의 반복 횟수가 같을 경우 포함된 단어를 사용. ex) 캐릭 : 20, 캐릭터 : 20 => 캐릭터
	private void merge(Map<String, Integer> data) {
		List<String> target = new ArrayList<>();
		data.forEach((k, v) -> target.add(String.format("#%s", k)));

		String source = StringUtils.join(target, " ");
		// log.info("source : {}", source);

		List<String> remove = new ArrayList<>();

		for (String key : data.keySet()) {
			// 숫자 - 4자리 보다 작을 경우
			if (StringUtils.isNumeric(key) && key.length() < 4) {
				remove.add(key);
				continue;
			}

			int value = data.get(key);
			String regex = String.format("#%s\\S+", key);
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(source);

			while (matcher.find()) {
				String k = matcher.group().replace("#", "");
				int v = data.get(k);

				if (value < v + Math.min(3, minCount)) // 보정값: 3 > 어벤져 : 83, 어벤져스 : 82 ==> 어벤져스 : 82
					remove.add(key);
			}
		}

		// remove.forEach(log::info);
		remove.forEach(data::remove);
	}

	private void matrix(List<String> docs) {
		for (String doc : docs) {
			if (StringUtils.isEmpty(doc))
				continue;

			String[] tokens = this.filterString(doc).split(" ");
			for (String token : tokens) {
				int limited = this.getLimited(token.length());

				for (int i = 0; i < limited; i++) {
					String body = token.substring(0, limited - i) + LEFT;
					String tail = token.substring(limited - i) + RIGHT;

					if (this.vocabulary.containsKey(body)) {
						String key = body;
						if (!links.containsKey(body)) {
							if (this.vocabulary.containsKey(tail)) {
								key = body + tail;
							} else {
								key = token + LEFT;
							}
						}

						int value = 1;
						if (links.containsKey(key)) {
							value = links.get(key) + 1;
						}

						links.put(key, value);
					}
				}
			}
		}

		if (verbose)
			log.info("links : {}", links);
	}

	private List<Word> normalizing(Map<String, Integer> data) {
		List<Word> list = new ArrayList<Word>();
		if (data.isEmpty())
			return list;

		double maxValue = data.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get().getValue();

		data.forEach((k, v) -> {
			list.add(new Word(k, v, Math.round(v * 9.99 / maxValue * 100) / 100d));
		});

		return list.stream().sorted(Word::countDiff).collect(Collectors.toList());
	}

	private int getLimited(int length) {
		return Math.min(length, this.maxLength + 1);
	}

	private String filterString(String text) {
		return TextUtil.getPlanText(text);
	}

	public void scanVocabulary(String[] docs) {
		scanVocabulary(Lists.newArrayList(docs));
	}

	public void scanVocabulary(List<String> docs) {
		for (String doc : docs) {
			for (String token : this.filterString(doc).split(" ")) {
				if (StringUtils.isEmpty(token)
						|| token.length() == 1
						|| StringUtils.isNumeric(token)
						|| stopwords.contains(token))
					continue;

				this.push(token + LEFT);

				if (corpus.contains(token)) {
					continue;
				} else {
					int limit = this.getLimited(token.length()) - 1;
					for (int i = limit; 1 < i; i--) {
						String k = token.substring(0, i);

						// log.info("#### {}, {}", token.substring(0, i), token.substring(i));

						this.push(k + LEFT);
						this.push(token.substring(i) + RIGHT);

						if (corpus.contains(k))
							break;
					}
				}
			}
		}

		this.vocabulary = this.keymap.entrySet().stream()
				// .filter(x -> x.getValue() >= minCount && x.getKey().length() > 1)
				.collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
	}

	private void push(String key) {
		keymap.put(key, keymap.containsKey(key) ? keymap.get(key) + 1 : 1);
	}

	public void setStopwords(List<String> stopwords) {
		this.stopwords = stopwords;
	}

	public void setThemes(Map<String, String> themes) {
		this.themes = themes;
	}

	public void setExclude(String exclude) {
		this.setExclude(exclude, true);
	}

	public void setExclude(String exclude, boolean match) {
		this.match = match;
		if (StringUtils.isNotEmpty(exclude))
			this.exclude = StringUtils.deleteWhitespace(exclude).split(",");
	}

	public void setCorpus(List<String> corpus) {
		this.corpus = corpus;
	}

	@Data
	@AllArgsConstructor
	public class Word {

		private String key;
		private int count;
		private double score;

		public int countDiff(final Word word) {
			return word.getCount() - count;
		}

	}

}
