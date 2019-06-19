package kr.co.esjee.ranker.schedule;

import java.util.Calendar;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import kr.co.esjee.ranker.elasticsearch.ElasticOption;
import kr.co.esjee.ranker.elasticsearch.ElasticQuery;
import kr.co.esjee.ranker.elasticsearch.ElasticSearcher;
import kr.co.esjee.ranker.util.CalendarUtil;
import kr.co.esjee.ranker.webapp.AppConstant;
import kr.co.esjee.ranker.webapp.model.MovieVO;
import kr.co.esjee.ranker.webapp.service.MovieCrawlerService;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableScheduling
@Slf4j
public class Scheduler implements AppConstant {

	@Autowired
	private ElasticsearchTemplate template;
	
	@Autowired
	private MovieCrawlerService movieCrawlerService;
	
	// @Autowired
	// private RabbitTemplate rabbitTemplate;

	@Value("${spring.custom.schedule.usable}")
	private boolean usable;

	@Scheduled(cron = "${spring.custom.schedule.cron}")
	public void cronTask() throws Exception {
		if (usable) {
			
			log.info("Schedule time: {}", CalendarUtil.getCurrentDateTime());

			Calendar calendar = CalendarUtil.getToday();

			BoolQueryBuilder query = QueryBuilders.boolQuery();
			query.must(ElasticQuery.termsQuery(FINISH, false));
			query.must(ElasticQuery.termsQuery(USABLE, true));
			query.must(ElasticQuery.termsQuery(WEEK, 0, calendar.get(Calendar.DAY_OF_WEEK)));
			query.must(ElasticQuery.termsQuery(MONTH, 0, calendar.get(Calendar.MONTH) + 1));
			query.must(ElasticQuery.termsQuery(DAY, 0, calendar.get(Calendar.DATE)));
			query.must(ElasticQuery.termsQuery(HOUR, 24, calendar.get(Calendar.HOUR)));
			query.must(ElasticQuery.termsQuery(MINUTE, 60, calendar.get(Calendar.MINUTE)));

			ElasticOption option = ElasticOption.newInstance()
					.queryBuilder(query)
					.page(1, 10000);

			SearchHits hits = ElasticSearcher.search(template.getClient(), SCHEDULE, option);

			log.info("{}", hits.totalHits);

			hits.forEach(h -> {
				MovieVO movieVO = new Gson().fromJson(h.toString(), MovieVO.class);
				this.callCrawler(movieVO);
				
				// if (rabbitTemplate == null) {
				// this.callCrawler(schedule);
				// } else {
				// this.sendMessage(schedule.toString());
				// }
			});

			// XXX test
			// if (rabbitTemplate != null) {
			// JSONObject json = new JSONObject();
			// json.put(NAME, RabbitMQConfig.QUEUE_NAME);
			// json.put(TIME, CalendarUtil.getCurrentDateTime());
			// json.put(DATA, "test ranker scheduler");
			//
			// // processor.output().send(MessageBuilder.withPayload(json.toString()).build());
			// this.sendMessage(json.toString());
			// }
		}
	}

	private void callCrawler(MovieVO movieVO) {
		log.info("call crawler : {}", movieVO);
		
		// execute with movieVO
		movieCrawlerService.execute(movieVO);
	}

	// private void sendMessage(String message) {
	// this.rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, JobUtil.getRoutingKey(RabbitMQConfig.QUEUE_NAME), message);
	// }

}