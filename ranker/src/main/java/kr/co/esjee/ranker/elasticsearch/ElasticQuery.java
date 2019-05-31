package kr.co.esjee.ranker.elasticsearch;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.CommonTermsQueryBuilder;
import org.elasticsearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;

/**
 * 참고 : https://wedul.site/533
 */
public class ElasticQuery {

	/**
	 * 정확한 구문검색 시 사용 기본 검색
	 */
	public static QueryBuilder searchQuery(Object text, String... fieldNames) {
		if (text != null && StringUtils.isNotEmpty(String.valueOf(text)) && fieldNames != null && fieldNames.length > 0) {
			BoolQueryBuilder query = QueryBuilders.boolQuery();
			for (String fieldName : fieldNames) {
				query.should(QueryBuilders.matchPhraseQuery(fieldName.trim(), text));
			}

			return query;
		} else {
			return QueryBuilders.matchAllQuery();
		}
	}

	/**
	 * 필드에 정확한 텀을 포함하고 있는지 확인하는 쿼리
	 */
	public static TermQueryBuilder termQuery(String name, Object value) {
		return QueryBuilders.termQuery(name, value);
	}

	/**
	 * 필드에 정확한 텀을 포함하고 있는지 확인하는 쿼리
	 */
	public static TermsQueryBuilder termsQuery(String name, Object... value) {
		return QueryBuilders.termsQuery(name, value);
	}

	/**
	 * 전체 텍스트 쿼리를 수행하기 위한 쿼리 (fuzzy 매치와 phrase, 근접 쿼리를 포함). 전체 텍스트에서 특정 부분이 포함되는지 여부를 확인하는데 fuzzy를 이용하여 유사도가 어느정도 되는 쿼리들도 조회 됨
	 */
	public static MultiMatchQueryBuilder matchQuery(String searchKey, String... fieldNames) {
		return QueryBuilders.multiMatchQuery(searchKey, fieldNames);
	}

	/**
	 * match와 같지만 정확한 구문이나 근접하게 매치되는 단어를 찾기위해 사용되는 쿼리
	 */
	public static MatchPhraseQueryBuilder matchPhraseQuery(String fieldName, String searchKey) {
		return QueryBuilders.matchPhraseQuery(fieldName, searchKey);
	}

	/**
	 * match_phrase와 유사하지만 마지막 단어에서 와일드카드 사용됨. (자동완성 기능에서 자주 실행 됨)
	 */
	public static MatchPhrasePrefixQueryBuilder matchPhrasePrefixQuery(String fieldName, String searchKey) {
		return QueryBuilders.matchPhrasePrefixQuery(fieldName, searchKey);
	}

	/**
	 * 자주 발생되는 단어를 제외하고 진짜 의미있는 단어만 검색하기 위해서 사용
	 */
	public static CommonTermsQueryBuilder commonTermQuery(String fieldName, String text) {
		return QueryBuilders.commonTermsQuery(fieldName, text);
	}

	/**
	 * 단일 쿼리 문자열 내에서 AND | OR | NOT 조건과 다중 필드 검색을 지정
	 */
	public static QueryStringQueryBuilder queryStringQuery(String queryString, String... fieldNames) {
		QueryStringQueryBuilder query = QueryBuilders.queryStringQuery(queryString);
		for (String fieldName : fieldNames) {
			query.field(fieldName);
		}

		return query;
	}

	/**
	 * 와이드카드 *, ? 를 사용해서 검색하는 방법. 전체를 다 뒤져서 찾아야하기 때문에 성능에 아주 안좋은 결과
	 */
	public static BoolQueryBuilder wildcardQuery(String searchKey, String... fieldNames) {
		String queryString = ("*" + searchKey + "*").replaceAll("\\*+", "\\*");

		BoolQueryBuilder query = QueryBuilders.boolQuery();
		for (String fieldName : fieldNames) {
			query.should(QueryBuilders.wildcardQuery(fieldName, queryString));
		}

		return query;
	}

	/**
	 * 마지막에 와이드카드 *, ? 를 사용해서 검색하는 방법. 전체를 다 뒤져서 찾아야하기 때문에 성능에 아주 안좋은 결과
	 */
	public static BoolQueryBuilder wildcardPostQuery(String searchKey, String... fieldNames) {
		String queryString = (searchKey + "*").replaceAll("\\*+", "\\*");

		BoolQueryBuilder query = QueryBuilders.boolQuery();
		for (String fieldName : fieldNames) {
			query.should(QueryBuilders.wildcardQuery(fieldName, queryString));
		}

		return query;
	}

	/**
	 * 검색하고자 하는 필드에서 입력한 값으로 시작하는 문장이 있는지 검색할 때 사용
	 */
	public static PrefixQueryBuilder prefixQuery(String fieldName, String value) {
		return new PrefixQueryBuilder(fieldName, value);
	}

	/**
	 * 기간을 설정하여 검색
	 */
	public static RangeQueryBuilder rangeQuery(String fieldName, String from, String to) {
		if (StringUtils.isNotEmpty(fieldName) || StringUtils.isNotEmpty(from) || StringUtils.isNotEmpty(to)) {
			RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(fieldName);
			if (StringUtils.isNotEmpty(from))
				rangeQuery.gte(from);

			if (StringUtils.isNotEmpty(to))
				rangeQuery.lte(to);

			return rangeQuery;
		} else {
			return null;
		}
	}

	public static RangeQueryBuilder rangeQuery(ElasticOption.Range range) {
		if (range != null)
			return rangeQuery(range.getFieldName(), range.getFrom(), range.getTo());
		else
			return null;
	}

}
