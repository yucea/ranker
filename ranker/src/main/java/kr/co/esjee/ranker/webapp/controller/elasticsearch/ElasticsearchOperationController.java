package kr.co.esjee.ranker.webapp.controller.elasticsearch;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import kr.co.esjee.ranker.elasticsearch.ElasticOperator;
import kr.co.esjee.ranker.webapp.controller.AppController;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/rest/elasticsearch/operation")
@Api(value = "operation")
@Slf4j
public class ElasticsearchOperationController extends AppController {

	@ApiOperation(value = "Alias로 indexName 조회")
	@ApiImplicitParam(name = "aliasName", value = "별칭", required = true, dataType = "string", paramType = "query")
	@RequestMapping(value = "/indexName", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String indexName(@RequestParam(required = true) String aliasName) throws JSONException {
		JSONObject result = new JSONObject();

		try {
			String indexName = ElasticOperator.getIndexName(super.getClient(), aliasName);

			log.info("alias : {}, index : {}", aliasName, indexName);

			result.put(SUCCESS, true);
			result.put(INDEX_NAME, indexName);
		} catch (Exception e) {
			e.printStackTrace();
			result.put(SUCCESS, false);
			result.put(MESSAGE, e.getLocalizedMessage());
		}

		return result.toString();
	}

	@ApiOperation(value = "Reindex 데이터 복제")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "indexName", value = "인덱스명", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "targetName", value = "대상인덱스명", required = true, dataType = "string", paramType = "query")
	})
	@RequestMapping(value = "/reindex", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String reindex(@RequestParam(required = true) String indexName, @RequestParam(required = true) String targetName) throws JSONException {
		JSONObject result = new JSONObject();

		try {
			ElasticOperator.reindex(super.getClient(), indexName, targetName);

			// Gson gson = new GsonBuilder().serializeNulls().serializeSpecialFloatingPointValues().create();
			// String json = gson.toJson(response);
			// JSONObject jsonObject = new JSONObject(json);

			log.info("reindex - indexName : {}, targetName : {}", indexName, targetName);

			result.put(SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put(SUCCESS, false);
			result.put(MESSAGE, e.getLocalizedMessage());
		}

		return result.toString();
	}

	@ApiOperation(value = "Add alias")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "indexName", value = "인덱스명", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "aliasName", value = "별칭", required = true, dataType = "string", paramType = "query")
	})
	@RequestMapping(value = "/addAdlias", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String addAdlias(@RequestParam(required = true) String indexName, @RequestParam(required = true) String aliasName) throws JSONException {
		JSONObject result = new JSONObject();

		try {
			ElasticOperator.addAlias(super.getClient(), indexName, aliasName);

			log.info("addAdlias - indexName : {}, aliasName : {}", indexName, aliasName);

			result.put(SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put(SUCCESS, false);
			result.put(MESSAGE, e.getLocalizedMessage());
		}

		return result.toString();
	}

	@ApiOperation(value = "Delete alias")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "indexName", value = "인덱스명", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "aliasName", value = "별칭", required = true, dataType = "string", paramType = "query")
	})
	@RequestMapping(value = "/delAdlias", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String delAdlias(@RequestParam(required = true) String indexName, @RequestParam(required = true) String aliasName) throws JSONException {
		JSONObject result = new JSONObject();

		try {
			ElasticOperator.delAlias(super.getClient(), indexName, aliasName);

			log.info("delAdlias - indexName : {}, aliasName : {}", indexName, aliasName);

			result.put(SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put(SUCCESS, false);
			result.put(MESSAGE, e.getLocalizedMessage());
		}

		return result.toString();
	}

	@ApiOperation(value = "Create index")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "indexName", value = "인덱스명", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "typeName", value = "타입명", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "schema", value = "스키마(settings, mappings)", required = true, dataType = "string", paramType = "query")
	})
	@RequestMapping(value = "/createIndex", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String createIndex(@RequestParam(required = true) String indexName,
			@RequestParam(required = true) String typeName,
			@RequestParam(required = true) String schema) throws JSONException {
		JSONObject result = new JSONObject();

		try {
			JSONObject data = new JSONObject(schema);
			String settings = data.get("settings").toString();
			String mappings = data.get("mappings").toString();

			ElasticOperator.createIndex(super.getClient(), indexName, typeName, settings, mappings);

			log.info("createIndex - indexName : {}, typeName : {}", indexName, typeName);

			result.put(SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put(SUCCESS, false);

			String errorMessage = e.getLocalizedMessage();
			if (errorMessage.contains("ResourceAlreadyExistsException"))
				result.put(MESSAGE, "Already exists index : " + indexName);
			else
				result.put(MESSAGE, e.getLocalizedMessage());
		}

		return result.toString();
	}

	@ApiOperation(value = "Remove index")
	@ApiImplicitParam(name = "indexName", value = "인덱스명", required = true, dataType = "string", paramType = "query")
	@RequestMapping(value = "/removeIndex", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String removeIndex(@RequestParam(required = true) String indexName) throws JSONException {
		JSONObject result = new JSONObject();

		try {
			ElasticOperator.removeIndex(super.getClient(), indexName);

			log.info("removeIndex - indexName : {}", indexName);

			result.put(SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put(SUCCESS, false);

			String errorMessage = e.getLocalizedMessage();
			if (errorMessage.contains("IndexNotFoundException"))
				result.put(MESSAGE, "Index not found : " + indexName);
			else
				result.put(MESSAGE, e.getLocalizedMessage());
		}

		return result.toString();
	}

	@ApiOperation(value = "Build index - 인덱스 생성, 데이터복제, 별칭 생성")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "aliasName", value = "별칭", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "indexName", value = "신규 인덱스명", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "typeName", value = "타입명", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "schema", value = "스키마(settings, mappings)", required = true, dataType = "string", paramType = "query")
	})
	@RequestMapping(value = "/buildIndex", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String buildIndex(@RequestParam(required = true) String aliasName,
			@RequestParam(required = true) String indexName,
			@RequestParam(required = true) String typeName,
			@RequestParam(required = true) String schema) throws JSONException {
		JSONObject result = new JSONObject();

		try {
			JSONObject data = new JSONObject(schema);
			String settings = data.get("settings").toString();
			String mappings = data.get("mappings").toString();

			ElasticOperator.buildIndex(super.getClient(), aliasName, indexName, typeName, settings, mappings);

			log.info("buildIndex - aliasName : {}, indexName : {}", aliasName, indexName);

			result.put(SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put(SUCCESS, false);

			String errorMessage = e.getLocalizedMessage();
			if (errorMessage.contains("ResourceAlreadyExistsException"))
				result.put(MESSAGE, "Already exists index : " + indexName);
			else
				result.put(MESSAGE, e.getLocalizedMessage());
		}

		return result.toString();
	}

}
