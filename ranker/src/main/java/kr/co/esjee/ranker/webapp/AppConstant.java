package kr.co.esjee.ranker.webapp;

public interface AppConstant {

	String EMPTY = "";
	String SUCCESS = "success";
	String MESSAGE = "message";

	String RESULT = "result";
	String ERROR = "error";
	String SCORE = "score";
	String KEY = "key";
	String TERM = "term";
	String TYPE = "type";
	String COUNT = "count";

	String NNP = "NNP";
	String HWP = "hwp";
	String DATA = "data";

	String ID = "id";
	long ES_START_ID = 10001;

	int ES_FROM = 0;
	int ES_SIZE = 10;

	String USER_DICTIONARY_PATH = "index.analysis.tokenizer.nori_user_dict.user_dictionary";
	String SYNONYM_PATH = "index.analysis.filter.synonym.synonyms_path";

	enum SENTIMENT_TYPE {
		positive, neutral, negative;

		public String getName() {
			return this.name();
		}

		public int getBagOfWordsScore() {
			if (this == positive)
				return 1;
			else if (this == negative)
				return 0;
			else
				return -1;
		}

		public int getAnalyzeScore() {
			if (this == positive)
				return 1;
			else if (this == negative)
				return -1;
			else
				return 0;
		}
	}

	enum PARSER_TYPE {
		elasticsearch, twitter;

		public boolean isElasticsearch() {
			return this == elasticsearch;
		}

		public boolean isTwitter() {
			return this == twitter;
		}
	}

}
