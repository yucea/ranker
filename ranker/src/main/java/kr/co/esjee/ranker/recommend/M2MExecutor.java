package kr.co.esjee.ranker.recommend;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import kr.co.esjee.ranker.webapp.AppConstant;
import kr.co.esjee.ranker.webapp.model.recommend.M2KNode;
import kr.co.esjee.ranker.webapp.service.RecommendService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class M2MExecutor implements AppConstant {

	public synchronized static long execute(RecommendService service, int threadSize, int interval) throws Exception {
		long start = System.currentTimeMillis();

		List<M2KNode> nodes = service.findAllM2KNode();
		log.info("M2KNode size : {}", nodes.size());

		M2MRecursiveAction task = new M2MRecursiveAction(service, nodes, interval);

		ForkJoinPool pool = threadSize == 0 ? ForkJoinPool.commonPool() : new ForkJoinPool(threadSize); // ForkJoinPool.commonPool();
		pool.execute(task);

		do {
			TimeUnit.SECONDS.sleep(60);
			log.info("parallelism : {}, Active : {}, tasks : {}, steals : {}, actual Time : {}", pool.getParallelism(), pool.getActiveThreadCount(), pool.getQueuedTaskCount(), pool.getStealCount(), System.currentTimeMillis() - start);
		} while (!task.isDone());

		pool.shutdown();

		log.info("elapsed time : {}", System.currentTimeMillis() - start);
		log.info("M2MExecutor Completed!");

		return service.m2mCount();
	}

}
