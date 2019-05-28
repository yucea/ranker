package kr.co.esjee.ranker.elasticsearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse.AnalyzeToken;
import org.elasticsearch.action.admin.indices.analyze.DetailAnalyzeResponse;
import org.elasticsearch.client.Client;

import com.google.common.collect.Lists;

import kr.co.esjee.ranker.webapp.AppConstant;
import lombok.AllArgsConstructor;
import lombok.Data;

public class ElasticAnalyzer implements AppConstant {

	public static List<AnalyzeToken> analyze(Client client, String index, String analyzer, String text) throws Exception {
		AnalyzeRequest request = new AnalyzeRequest();
		request.index(index);
		request.analyzer(analyzer);
		request.text(text);

		ActionFuture<AnalyzeResponse> future = client.admin().indices().analyze(request);
		AnalyzeResponse response = future.get();
		return response.getTokens();
	}

	public static List<Result> simple(Client client, String index, String analyzer, String text) throws Exception {
		List<AnalyzeToken> tokens = analyze(client, index, analyzer, text);

		Map<String, Result> data = new HashMap<>();

		tokens.forEach(token -> {
			String term = token.getTerm();
			if (term.length() > 1) {
				int count = data.containsKey(term) ? data.get(term).getCount() + 1 : 1;
				data.put(term, new Result(term, count));
			}
		});

		List<Result> result = Lists.newArrayList(data.values());
		result.sort((o1, o2) -> o2.getCount() - o1.getCount());

		return result;
	}

	public static List<String> keyword(Client client, String index, String analyzer, String text) throws Exception {
		Set<String> result = new TreeSet<>();

		List<AnalyzeToken> tokens = analyze(client, index, analyzer, text);
		for (AnalyzeToken token : tokens) {
			String term = token.getTerm();
			if (term.length() > 1)
				result.add(term);
		}

		return Lists.newArrayList(result);
	}
	
	public static AnalyzeToken[] explain(Client client, String index, String analyzer, String text) throws Exception {
		AnalyzeRequestBuilder builder = new AnalyzeRequestBuilder(client, AnalyzeAction.INSTANCE, index, text);
		builder.setAnalyzer(analyzer);
		builder.setExplain(true);

		DetailAnalyzeResponse response = builder.get().detail();

		return response.tokenizer().getTokens();
	}

	@Data
	@AllArgsConstructor
	public static class Result {

		String term = null;
		int count;

	}

}