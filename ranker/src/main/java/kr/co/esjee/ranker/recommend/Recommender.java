package kr.co.esjee.ranker.recommend;

import java.io.IOException;

import kr.co.esjee.ranker.config.AppConfig;
import kr.co.esjee.ranker.webapp.AppConstant;
import kr.co.esjee.ranker.webapp.service.RecommendService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Recommender implements AppConstant {

	public static void execute(RecommendService service, int threadSize, int interval) throws Exception {
		log.info("Recommender.execute start");

		long s = System.currentTimeMillis();

		m2kRunner(service, MOVIE, DOC);

		m2mRunner(service, threadSize, interval);

		k2mRunner(service);

		log.info("Recommender.execute completed - {}", System.currentTimeMillis() - s);
	}

	public static void m2kRunner(RecommendService service, String indexName, String typeName) throws Exception {
		long s = System.currentTimeMillis();
		log.info("Recommender.m2kRunner start");

		int count = M2KExecutor.execute(service, indexName, typeName, AppConfig.getStopwords());

		log.info("Recommender.m2kRunner finish - count : {}, actual time : {}(ms)", count, System.currentTimeMillis() - s);
	}

	public static void m2mRunner(RecommendService service, int threadSize, int interval) throws Exception {
		long s = System.currentTimeMillis();
		log.info("Recommender.m2mRunner start");

		long count = M2MExecutor.execute(service, threadSize, interval);

		log.info("Recommender.m2mRunner finish - count : {}, actual time : {}(ms)", count, System.currentTimeMillis() - s);
	}

	public static void k2mRunner(RecommendService service) throws IOException {
		long s = System.currentTimeMillis();
		log.info("Recommender.k2mRunner start");

		long count = K2MExecutor.execute(service);

		log.info("Recommender.k2mRunner finish - count : {}, actual time : {}(ms)", count, System.currentTimeMillis() - s);
	}

}
