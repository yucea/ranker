package kr.co.esjee.ranker.webapp;

public interface AppConstant {

	String EMPTY = "";
	String SUCCESS = "success";
	String MESSAGE = "message";

	String RESULT = "result";
	String ERROR = "error";
	String DATA = "data";
	String KEY = "key";
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

	long ES_START_ID = 10001;

	int ES_FROM = 0;
	int ES_SIZE = 10;
	int ES_MAXSIZE = 1000000;

	String USER_DICTIONARY_PATH = "index.analysis.tokenizer.nori_user_dict.user_dictionary";
	String SYNONYM_PATH = "index.analysis.filter.synonym.synonyms_path";

	String SEQUENCE = "sequence";
	String ARTICLE = "article";
	String SCHEDULE = "schedule";
	String MOVIE = "mv_basic_info";
	String PERSON = "mv_person_info";
	String ERROR_LOG = "mv_error_log";

	String DOC = "doc";

	String MV_M2K = "mv_m2k";
	String MV_M2M = "mv_m2m";
	String MV_K2M = "mv_k2m";

	String SYNOPSIS = "synopsis";
	String MAKING_NOTE = "makingNote";

	String TID = "tid";
	String TITLE = "title";
	String DIRECTOR = "director";
	String ACTOR = "actor";
	String ROLE = "role";
	String SCORE = "score";
	String GENRE = "genre";
	String GRADE = "grade";
	String OPENDAY = "openDay";

	double NORMALIZING_SCORE = 9.99;

	enum INDICES {
		article, schedule, mv_basic_info, mv_person_info, mv_error_log
	}

	enum RECOMMENDS {
		m2k, m2m, k2m
	}
}
