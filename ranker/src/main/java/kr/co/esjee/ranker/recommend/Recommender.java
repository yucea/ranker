package kr.co.esjee.ranker.recommend;

import java.util.List;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import kr.co.esjee.ranker.config.AppConfig;
import kr.co.esjee.ranker.webapp.AppConstant;
import kr.co.esjee.ranker.webapp.model.recommend.M2KNode;
import kr.co.esjee.ranker.webapp.service.RecommendService;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Recommender implements AppConstant {

	private static Recommender self = null;
	private static int threadSize = 6;
	private static int interval = 1000;

	@Autowired
	private RecommendService service;

	@Value("${spring.recommend.thread.size}")
	private void setThreadSize(int size) {
		threadSize = size;
	}

	@Value("${spring.recommend.thread.interval}")
	private void setInterval(int count) {
		interval = count;
	}

	public static Recommender getInstance() {
		if (self == null) {
			self = new Recommender();
		}

		return self;
	}

	public void execute(Client client, String indexName, String typeName, String id) throws Exception {
		log.info("Recommender.execute start");

		long s = System.currentTimeMillis();

		m2kRunner(service, client, indexName, typeName);

		m2mRunner(service, threadSize, interval);

		k2mRunner(service, client, indexName, typeName);

		log.info("Recommender.execute completed - {}", System.currentTimeMillis() - s);
	}

	public static void m2kRunner(RecommendService service, Client client, String indexName, String typeName) throws Exception {
		long s = System.currentTimeMillis();
		log.info("Recommender.m2kRunner start");

		int count = M2KExecutor.execute(service, client, indexName, typeName, AppConfig.getStopwords());

		log.info("Recommender.m2kRunner finish - count : {}, actual time : {}(ms)", count, System.currentTimeMillis() - s);
	}

	public static void m2mRunner(RecommendService service, int threadSize, int interval) throws Exception {
		long s = System.currentTimeMillis();
		log.info("Recommender.m2mRunner start");

		List<M2KNode> nodes = service.findAllM2KNodes();

		long count = M2MExecutor.execute(service, nodes, threadSize, interval);

		log.info("Recommender.m2mRunner finish - count : {}, actual time : {}(ms)", count, System.currentTimeMillis() - s);
	}

	public static void k2mRunner(RecommendService service, Client client, String indexName, String typeName) {
		// TODO Auto-generated method stub

	}

}
