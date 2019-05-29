package kr.co.esjee.ranker.webapp;

public interface AppConstant {

	String EMPTY = "";
	String SUCCESS = "success";
	String MESSAGE = "message";

	String RESULT = "result";
	String ERROR = "error";
	String SCORE = "score";
	String DATA = "data";
	String KEY = "key";
	String TERM = "term";
	String TYPE = "type";
	String COUNT = "count";
	String TOTAL_COUNT = "totalCount";
	String TOTAL_PAGES = "totalPages";

	String ID = "id";

	long ES_START_ID = 10001;

	int ES_FROM = 0;
	int ES_SIZE = 10;

	String USER_DICTIONARY_PATH = "index.analysis.tokenizer.nori_user_dict.user_dictionary";
	String SYNONYM_PATH = "index.analysis.filter.synonym.synonyms_path";

	String SEQUENCE = "sequence";
	String ARTICLE = "article";
	String DOC = "doc";

	enum INDICES {
		article
	}

}
