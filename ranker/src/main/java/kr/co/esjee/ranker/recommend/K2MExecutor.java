package kr.co.esjee.ranker.recommend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.esjee.ranker.util.RecommendUtil;
import kr.co.esjee.ranker.webapp.AppConstant;
import kr.co.esjee.ranker.webapp.model.recommend.K2MNode;
import kr.co.esjee.ranker.webapp.model.recommend.Keyword;
import kr.co.esjee.ranker.webapp.model.recommend.M2KNode;
import kr.co.esjee.ranker.webapp.model.recommend.Score;
import kr.co.esjee.ranker.webapp.service.RecommendService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class K2MExecutor implements AppConstant {

	public synchronized static int execute(RecommendService service) throws IOException {
		log.info("K2MExecutor.execute start...");

		long start = System.currentTimeMillis();
		long s = System.currentTimeMillis();

		List<M2KNode> nodes = service.findAllM2KNode();
		log.info("m2knode count : {}", nodes.size());

		// theme keyword...
		// final Map<String, List<String>> map = new HashMap<>();
		// log.info("themeskeyword count : {}", map.size());

		Map<String, List<M2KNode>> data = new HashMap<>();

		for (M2KNode node : nodes) {
			Keyword[] keywords = node.getKeywords();
			for (Keyword k : keywords) {
				if (k.getScore() < 1)
					continue;

				String key = k.getWord();
				List<M2KNode> list = data.containsKey(key) ? data.get(key) : new ArrayList<M2KNode>();
				list.add(node);

				data.put(key, list);
			}
		}

		List<K2MNode> result = new ArrayList<K2MNode>();

		int[] id = { 1 };
		data.forEach((key, list) -> {
			// tid가 중복일 경우.. max값으로 처리
			Map<String, Score> tidmap = new HashMap<String, Score>();

			double[] maxscore = { FIXING_KEYWORD_SCORE }; // KMDB Keyword인 경우 10으로 설정됨

			list.forEach(n -> {
				double score = n.getScoreMap().get(key);
				double weighting = RecommendUtil.getWeighting(n.getOpenDay(), n.getRating(), n.getStarScore());
				double newscore = score + weighting;

				maxscore[0] = Math.max(maxscore[0], newscore);

				Score value = null;
				if (tidmap.containsKey(n.getTid())) {
					value = tidmap.get(n.getTid());
					value.setScore(Math.max(value.getScore(), newscore));
				} else {
					value = new Score(n.getId(), n.getTitle(), n.getDirector(), n.getActor(), n.getGenre(), n.getRating(), newscore);
				}

				tidmap.put(n.getTid(), value);
			});

			if (!tidmap.isEmpty()) {
				List<Score> values = new ArrayList<Score>(tidmap.values());
				values.sort(Comparator.comparingDouble(Score::getScore).reversed());

				List<Score> scores = null;
				if (values.size() > 100) {
					scores = new ArrayList<Score>(values.subList(0, 100));
				} else {
					scores = new ArrayList<Score>(values);
				}

				if (maxscore[0] > NORMALIZING_SCORE) {
					scores.forEach(score -> {
						score.setScore(RecommendUtil.getScore(score.getScore() / maxscore[0] * NORMALIZING_SCORE));
					});
				}

				result.add(new K2MNode(id[0]++, key, scores.toArray(new Score[0])));
			}
		});

		if (!result.isEmpty()) {
			service.k2mDeleteAll();
			log.info("k2m deleted");

			service.k2mSaveAll(result);
			log.info("k2m saved : [{}]", result.size());
		}

		log.info("actual time : {}, elapsed time : {}", System.currentTimeMillis() - s, System.currentTimeMillis() - start);
		log.info("K2MExecutor Completed!");

		return result.size();
	}

}
