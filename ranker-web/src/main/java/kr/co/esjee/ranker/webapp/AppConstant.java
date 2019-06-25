package kr.co.esjee.ranker.webapp;

public interface AppConstant {

	String EMPTY = "";
	String SUCCESS = "success";
	String MESSAGE = "message";

	String LIST = "list";
	String INFO = "info";
	String RESULT = "result";
	String ERROR = "error";
	String SCORE = "score";
	String DATA = "data";
	String DRAW = "draw";
	String RECORDS_TOTAL = "recordsTotal";
	String RECORDS_FILTERED = "recordsFiltered";

	String KEY = "key";
	String TITLE = "title";
	String TERM = "term";
	String TYPE = "type";
	String COUNT = "count";
	String TOTAL_COUNT = "totalCount";
	String TOTAL_PAGES = "totalPages";

	String INDEX_NAME = "indexName";

	String MONTH = "month";
	String WEEK = "week";
	String DAY = "day";
	String HOUR = "hour";
	String MINUTE = "minute";
	String STATUS = "status";
	String FINISH = "finish";
	String USABLE = "usable";

	String NAME = "name";
	String TIME = "time";
	String OPTION = "option";

	String ID = "id";
	String TID = "tid";
	String PID = "pid";

	long ES_START_ID = 10001;

	int ES_FROM = 0;
	int ES_SIZE = 10;

	String USER_DICTIONARY_PATH = "index.analysis.tokenizer.nori_user_dict.user_dictionary";
	String SYNONYM_PATH = "index.analysis.filter.synonym.synonyms_path";

	String SEQUENCE = "sequence";
	String ARTICLE = "article";
	String SCHEDULE = "schedule";
	String DOC = "doc";
	
	String RECOMMEND_M2K = "mv_m2k";
	String RECOMMEND_M2M = "mv_m2m";
	String RECOMMEND_K2M = "mv_k2m";
	
	String MOVIE = "mv_basic_info";
	String PERSON = "mv_person_info";
	
	enum INDICES {
		schedule
	}

}
