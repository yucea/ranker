package kr.co.esjee.ranker.elasticsearch;

import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;

import kr.co.esjee.ranker.webapp.AppConstant;

public class ElasticSearcher implements AppConstant {
	
	public static long count(Client client, String indexName) {
		SearchRequestBuilder builder = new SearchRequestBuilder(client, SearchAction.INSTANCE);
		builder.setIndices(indexName);
		builder.setQuery(QueryBuilders.matchAllQuery());

		SearchResponse response = builder.get();
		return response.getHits().totalHits;
	}
	
}