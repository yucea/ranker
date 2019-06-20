package kr.co.esjee.ranker.util;

import java.util.Comparator;
import java.util.TreeMap;

import kr.co.esjee.ranker.webapp.AppConstant;
import kr.co.esjee.ranker.webapp.model.recommend.Score;

public class FixedMap extends TreeMap<Double, Score> implements AppConstant {

	private static final long serialVersionUID = 1L;
	private static final int LIMITED = 100;

	public FixedMap(Comparator<Object> comparator) {
		super(comparator);
	}

	@Override
	public Score put(Double key, Score score) {
		Score value = super.put(key, score);
		if (this.size() > LIMITED) {
			pollLastEntry();
		}

		return value;
	}

	public Score[] getValue() {
		Score[] values = this.values().toArray(new Score[0]);
		if (values.length == 0)
			return values;

		double maxScore = this.firstEntry().getValue().getScore();

		for (Score s : values) {
			double score = s.getScore();
			if (maxScore > 10) {
				score = score / maxScore * NORMALIZING_SCORE;
			}
			s.setScore(RecommendUtil.getScore(score));
		}

		return values;
	}

}
