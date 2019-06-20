package kr.co.esjee.ranker.recommend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveAction;

import kr.co.esjee.ranker.util.FixedMap;
import kr.co.esjee.ranker.util.RecommendUtil;
import kr.co.esjee.ranker.webapp.model.recommend.Keyword;
import kr.co.esjee.ranker.webapp.model.recommend.M2KNode;
import kr.co.esjee.ranker.webapp.model.recommend.M2MNode;
import kr.co.esjee.ranker.webapp.model.recommend.Score;
import kr.co.esjee.ranker.webapp.service.RecommendService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class M2MRecursiveAction extends RecursiveAction {

	private static final long serialVersionUID = 1270965747364391906L;
	private RecommendService service;
	private List<M2KNode> nodes;
	private int interval = 1000;
	private int fromindex = -1;
	private int toindex = -1;

	public M2MRecursiveAction(RecommendService service, List<M2KNode> nodes, int interval) {
		this.service = service;
		this.nodes = nodes;
		this.interval = interval;
	}

	private M2MRecursiveAction(RecommendService service, List<M2KNode> nodes, int fromindex, int toindex) {
		this.service = service;
		this.nodes = nodes;
		this.fromindex = fromindex;
		this.toindex = toindex;
	}

	@Override
	protected void compute() {
		if (fromindex == -1) {
			List<M2MRecursiveAction> tasks = new ArrayList<M2MRecursiveAction>();

			int totalSize = nodes.size();
			int size = totalSize / interval + (totalSize % interval == 0 ? 0 : 1);

			for (int i = 0; i < size; i++) {
				int start = i * interval;
				int limit = (i + 1) * interval;
				if (limit > totalSize)
					limit = totalSize;

				M2MRecursiveAction action = new M2MRecursiveAction(service, nodes, start, limit);
				action.fork();
				tasks.add(action);

				log.info("start : {}, limit : {}", start, limit - 1);
			}

			log.info("size : {}", tasks.size());

			if (tasks.size() > 0) {
				tasks.forEach(task -> {
					task.join();
				});
			}
		} else {
			log.info("fromindex : {}, toindex : {}", fromindex, toindex - 1);

			List<M2MNode> values = new ArrayList<M2MNode>();

			for (int index = fromindex; index < toindex; index++) {
				M2KNode node = nodes.get(index);
				FixedMap map = new FixedMap(Collections.reverseOrder());
				Map<String, Double> scoremap = node.getScoreMap();

				if (scoremap.isEmpty()) {
					continue;
				}

				for (int i = 0; i < nodes.size(); i++) {
					M2KNode n = nodes.get(i);

					if (n == node) {
						continue;
					} else {
						double sum = 0;
						double matchCount = 0;

						Keyword[] keywords = n.getKeywords();
						for (Keyword keyword : keywords) {
							if (scoremap.containsKey(keyword.getWord())) {
								sum += Math.pow(scoremap.get(keyword.getWord()), 2);
								matchCount++;
							}
						}

						if (matchCount > 0) {
							double similarity = RecommendUtil.getScore(Math.sqrt(sum) * matchCount);

							if (similarity > 0) {
								double weighting = RecommendUtil.getWeighting(n.getOpenDay(), n.getRating(), n.getStarScore());
								// genre * similarity + weighting
								double score = RecommendUtil.getGenreScore(node.getGenre(), n.getGenre()) * similarity + weighting;

								if (score > 0)
									map.put(score + 0.00000001 * i, new Score(n.getId(), n.getTitle(), n.getDirector(), n.getActor(), n.getGenre(), n.getRating(), score < 0 ? 0 : score));
							}
						}
					}
				}

				Score[] list = map.getValue();
				if (list.length > 0) {
					values.add(new M2MNode(node.getId(), node.getTid(), node.getTitle(), node.getGenre(), node.getRating(), list));
				} else {
					log.warn("result is null : {}, {}", node.getId(), node.getTid());
				}

				if (values.size() == 1000) {
					service.m2mSaveAll(values);

					log.info("fromindex : {}, toindex : {}, index : {}, save : {}", fromindex, toindex - 1, index, values.size());
					values.clear();
				}
			}

			if (!values.isEmpty()) {
				service.m2mSaveAll(values);
				log.info("fromindex : {}, toindex : {}, size : {}", fromindex, toindex - 1, values.size());
			}
		}
	}

}