package kr.co.esjee.ranker.elasticsearch;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import kr.co.esjee.ranker.webapp.AppConstant;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ElasticOption implements AppConstant {

	private QueryBuilder queryBuilder;
	private HighlightBuilder highlightBuilder;
	private List<SortBuilder<?>> sortBuilders = new ArrayList<>();
	private String[] include;
	private String[] exclude;
	private int pageNo = 0;
	private int from = ES_FROM;
	private int size = ES_SIZE;
	private boolean skipSort = false;

	private Range range;

	public static ElasticOption newInstance() {
		return new ElasticOption();
	}

	public ElasticOption queryBuilder(QueryBuilder queryBuilder) {
		this.queryBuilder = queryBuilder;
		return this;
	}

	public ElasticOption highlightTag(String tagName, String... fieldNames) {
		this.highlight(tagName, tagName, fieldNames);
		return this;
	}

	public ElasticOption highlight(String preTag, String postTag, String... fieldNames) {
		if (StringUtils.isNotEmpty(preTag) && StringUtils.isNotEmpty(postTag)) {
			this.highlightBuilder = new HighlightBuilder()
					.preTags((preTag.startsWith("<") ? "" : "<") + preTag + (preTag.endsWith(">") ? "" : ">"))
					.postTags((postTag.startsWith("</") ? "" : "</") + postTag + (postTag.endsWith(">") ? "" : ">"));

			for (String fieldName : fieldNames) {
				this.highlightBuilder.field(fieldName);
			}
		} else {
			this.highlightBuilder = null;
		}

		return this;
	}

	public ElasticOption sort(String field, SortOrder order) {
		this.sortBuilders.add(SortBuilders.fieldSort(field).order(order));
		return this;
	}

	public ElasticOption sort(SortBuilder<?> sortBuilder) {
		this.sortBuilders.add(sortBuilder);
		return this;
	}

	public ElasticOption sortSkip() {
		this.skipSort = true;
		return this;
	}

	public ElasticOption fetchSource(String[] include, String[] exclude) {
		this.include = include;
		this.exclude = exclude;
		return this;
	}

	public ElasticOption include(String... include) {
		this.include = include;
		return this;
	}

	public ElasticOption exclude(String... exclude) {
		this.exclude = exclude;
		return this;
	}

	public ElasticOption pageNo(int pageNo) {
		this.pageNo = pageNo;
		return this;
	}

	public ElasticOption page(int pageNo, int size) {
		this.pageNo = pageNo;
		this.size = size;
		return this;
	}

	public ElasticOption range(String rangeName, String from, String to) {
		this.range = new Range(rangeName, from, to);
		return this;
	}

	public void fulfill(SearchRequestBuilder builder) {
		RangeQueryBuilder rangeQuery = ElasticQuery.rangeQuery(range);
		if (rangeQuery != null) {
			if (queryBuilder != null) {
				builder.setQuery(QueryBuilders.boolQuery()
						.must(queryBuilder)
						.filter(rangeQuery));
			} else {
				builder.setQuery(rangeQuery);
			}
		} else if (queryBuilder != null) {
			builder.setQuery(queryBuilder);
		}

		if (sortBuilders == null || sortBuilders.isEmpty()) {
			if (!skipSort)
				builder.addSort(ID, SortOrder.DESC);
		} else {
			for (SortBuilder<?> sortBuilder : sortBuilders) {
				builder.addSort(sortBuilder);
			}
		}

		if (highlightBuilder != null)
			builder.highlighter(highlightBuilder);

		if (include != null || exclude != null)
			builder.setFetchSource(include, exclude);

		builder.setFrom(pageNo > 0 && size > 0 ? (pageNo - 1) * size : 0);
		builder.setSize(size);
	}

	@Data
	@AllArgsConstructor
	class Range {
		private String fieldName;
		private String from;
		private String to;
	}

}
