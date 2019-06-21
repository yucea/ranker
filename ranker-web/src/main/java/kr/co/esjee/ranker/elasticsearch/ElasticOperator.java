package kr.co.esjee.ranker.elasticsearch;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest.AliasActions;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesAction;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequestBuilder;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexAction;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexAction;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.segments.IndexSegments;
import org.elasticsearch.action.admin.indices.segments.IndicesSegmentResponse;
import org.elasticsearch.action.admin.indices.segments.IndicesSegmentsAction;
import org.elasticsearch.action.admin.indices.segments.IndicesSegmentsRequestBuilder;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequestBuilder;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.ReindexAction;
import org.elasticsearch.index.reindex.ReindexRequestBuilder;

import com.google.common.collect.Lists;

import kr.co.esjee.ranker.webapp.AppConstant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ElasticOperator implements AppConstant {

	public static void buildIndex(Client client, String aliasName, String newIndexName, String typeName, String settings, String mappings) throws Exception {
		// alias에 설정된 index명
		String oldIndexName = getIndexName(client, aliasName);
		log.info("current index name : {}", oldIndexName);
		// index 생성
		createIndex(client, newIndexName, typeName, settings, mappings);
		log.info("create index : {}", newIndexName);
		// 데이터 복제
		reindex(client, oldIndexName, newIndexName);
		log.info("reindex : {}", newIndexName);
		// alias 생성
		addAlias(client, newIndexName, aliasName);
		log.info("add alias : {} - {}", newIndexName, aliasName);
		// alias 삭제
		delAlias(client, oldIndexName, aliasName);
		log.info("delete alias : {} - {}", oldIndexName, aliasName);
	}

	public static BulkByScrollResponse reindex(Client client, String oldIndexName, String newIndexName) throws Exception {
		ReindexRequestBuilder builder = ReindexAction.INSTANCE.newRequestBuilder(client);
		builder.source(oldIndexName).destination(newIndexName);
		builder.source().setSize(100);

		ActionFuture<BulkByScrollResponse> future = builder.execute();
		return future.get();
	}

	public static String getIndexName(Client client, String aliasName) {
		List<String> list = getAliasIndex(client, aliasName);
		return list != null && list.size() == 0 ? aliasName : list.get(0);
	}

	public static List<String> getAliasIndex(Client client, String aliasName) {
		GetAliasesRequestBuilder builder = new GetAliasesRequestBuilder(client, GetAliasesAction.INSTANCE, aliasName);
		GetAliasesResponse response = builder.get();
		return Lists.newArrayList(response.getAliases().keysIt());
	}

	public static boolean addAlias(Client client, String indexName, String aliasName) throws Exception {
		return alias(client, indexName, aliasName, AliasActions.Type.ADD);
	}

	public static boolean delAlias(Client client, String indexName, String aliasName) throws Exception {
		return alias(client, indexName, aliasName, AliasActions.Type.REMOVE);
	}

	private static boolean alias(Client client, String indexName, String aliasName, AliasActions.Type type) throws Exception {
		IndicesAliasesRequest request = new IndicesAliasesRequest();
		AliasActions aliasAction = new AliasActions(type)
				.index(indexName)
				.alias(aliasName);
		request.addAliasAction(aliasAction);

		IndicesAliasesResponse response = client.admin().indices().aliases(request).get();
		return response.isAcknowledged();
	}

	public static CreateIndexResponse createIndex(Client client, String indexName, String typeName, String settings, String mappings) throws Exception {
		CreateIndexRequestBuilder builder = new CreateIndexRequestBuilder(client, CreateIndexAction.INSTANCE);
		builder.setIndex(indexName);

		if (StringUtils.isNotEmpty(settings))
			builder.setSettings(settings, XContentType.JSON);
		if (StringUtils.isNotEmpty(mappings))
			builder.addMapping(typeName, mappings, XContentType.JSON);

		ActionFuture<CreateIndexResponse> future = builder.execute();
		return future.get();
	}

	public static Map<String, IndexSegments> selectIndex(Client client) {
		IndicesSegmentsRequestBuilder builder = new IndicesSegmentsRequestBuilder(client, IndicesSegmentsAction.INSTANCE);
		IndicesSegmentResponse response = builder.get();
		return response.getIndices();
	}

	public static boolean removeIndex(Client client, String... indexNames) throws Exception {
		DeleteIndexRequestBuilder builder = new DeleteIndexRequestBuilder(client, DeleteIndexAction.INSTANCE, indexNames);
		ActionFuture<DeleteIndexResponse> execute = builder.execute();
		DeleteIndexResponse response = execute.get();
		return response.isAcknowledged();
	}

	public static Settings getSettings(Client client, String aliasName) {
		String indexName = ElasticOperator.getIndexName(client, aliasName);

		GetSettingsRequestBuilder builder = client.admin().indices().prepareGetSettings(aliasName);
		GetSettingsResponse response = builder.get();

		return response.getIndexToSettings().get(indexName);
	}

	public static String getUserDictionaryPath(Settings settings) {
		return settings.get(USER_DICTIONARY_PATH);
	}

	public static String getSynonymPath(Settings settings) {
		return settings.get(SYNONYM_PATH);
	}

}
