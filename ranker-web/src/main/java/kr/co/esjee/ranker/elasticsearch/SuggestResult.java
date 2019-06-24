package kr.co.esjee.ranker.elasticsearch;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.json.JSONArray;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class SuggestResult {

	private long totalCount;
	private List<Suggest> suggests = new ArrayList<>();

	public void add(String keyword, float score, String highlight) {
		suggests.add(new Suggest(keyword, score, highlight));
	}

	public JSONArray getResult() {
		return new JSONArray(suggests.toString());
	}

	@Data
	@AllArgsConstructor
	class Suggest {

		private String keyword;
		private double score;
		private String highlight;

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
		}

	}

}
