package kr.co.esjee.ranker.recommend;

import java.io.IOException;
import java.util.List;

import org.elasticsearch.client.Client;

import kr.co.esjee.ranker.config.AppConfig;
import kr.co.esjee.ranker.webapp.AppConstant;
import kr.co.esjee.ranker.webapp.model.recommend.M2KNode;
import kr.co.esjee.ranker.webapp.service.RecommendService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Recommender implements AppConstant {

	public static void execute(RecommendService service, Client client, String indexName, String typeName, String id) throws Exception {
		log.info("Recommender.execute start");

		long s = System.currentTimeMillis();

		m2kRunner(service, client, indexName, typeName);

		m2mRunner(service, client, indexName, typeName);

		k2mRunner(service, client, indexName, typeName);

		log.info("Recommender.execute completed - {}", System.currentTimeMillis() - s);
	}

	public static void m2kRunner(RecommendService service, Client client, String indexName, String typeName) throws Exception {
		long s = System.currentTimeMillis();
		log.info("Recommender.m2kRunner start");
		List<M2KNode> nodes = M2KExecutor.execute(client, indexName, typeName, AppConfig.getStopwords());
		service.saveM2KNode(nodes);

		log.info("Recommender.m2kRunner finish - count : {}, actual time : {}(ms)", nodes.size(), System.currentTimeMillis() - s);
	}

	private static void m2mRunner(RecommendService service, Client client, String indexName, String typeName) throws Exception {
		long s = System.currentTimeMillis();
		log.info("Recommender.m2mRunner start");
		 List<M2KNode> nodes = M2MExecutor.execute(client, indexName, typeName, AppConfig.getStopwords());
		 // service.saveM2MNode(nodes);

		log.info("Recommender.m2mRunner finish - count : {}, actual time : {}(ms)", 0, System.currentTimeMillis() - s);
	}

	private static void k2mRunner(RecommendService service, Client client, String indexName, String typeName) {
		// TODO Auto-generated method stub

	}

}
