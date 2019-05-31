package kr.co.esjee.ranker.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import kr.co.esjee.ranker.config.RabbitMQConfig;
import kr.co.esjee.ranker.elasticsearch.ElasticOption;
import kr.co.esjee.ranker.elasticsearch.ElasticQuery;
import kr.co.esjee.ranker.elasticsearch.ElasticSearcher;
import kr.co.esjee.ranker.util.CalendarUtil;
import kr.co.esjee.ranker.util.JobUtil;
import kr.co.esjee.ranker.webapp.AppConstant;
import kr.co.esjee.ranker.webapp.model.Schedule;
import kr.co.esjee.ranker.webapp.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableScheduling
@Slf4j
public class Scheduler implements AppConstant {

	@Autowired
	private ElasticsearchTemplate template;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private ScheduleService service;

	@Value("${spring.custom.schedule.usable}")
	private boolean usable;

	@Value("${spring.custom.schedule.process.minute}")
	private int processMinute;

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
			query.must(ElasticQuery.termsQuery(MINUTE, 24, calendar.get(Calendar.MINUTE)));
			query.must(ElasticQuery.termsQuery(HOUR, 60, calendar.get(Calendar.HOUR)));
			query.must(ElasticQuery.termsQuery(STATUS, STATUS_TYPE.COMPLETED.name()));

			ElasticOption option = ElasticOption.newInstance()
					.queryBuilder(query)
					.page(1, 10000);

			SearchHits hits = ElasticSearcher.search(template.getClient(), SCHEDULE, option);

			log.info("{}", hits.totalHits);

			List<Long> ids = new ArrayList<>();

			hits.forEach(h -> {
				Schedule schedule = new Gson().fromJson(h.getSourceAsMap().toString(), Schedule.class);
				schedule.setStatus(STATUS_TYPE.READY.name());
				schedule.setLastRuntime(CalendarUtil.getCurrentDateTime());
				ids.add(schedule.getId());

				if (rabbitTemplate == null) {
					this.callCrawler(schedule);
				} else {
					this.sendMessage(schedule.toString());
				}

				// status: ready, lastRuntime 설정
				service.save(schedule);
			});

			// status가 ready 상태가 지정된 시간 이상 지속되면 재 실행
			List<Schedule> list = service.findByStatusAndLastRuntimeLessThan(STATUS_TYPE.READY.name(), CalendarUtil.getPreviousTime(processMinute));
			list.forEach(s -> {
				if (ids.contains(s.getId())) {
					return;
				} else {
					if (rabbitTemplate == null) {
						this.callCrawler(s);
					} else {
						this.sendMessage(s.toString());

						s.setLastRuntime(CalendarUtil.getCurrentDateTime());
						service.save(s);
					}
				}
			});

			// XXX test
			JSONObject json = new JSONObject();
			json.put(NAME, RabbitMQConfig.QUEUE_NAME);
			json.put(TIME, CalendarUtil.getCurrentDateTime());
			json.put(DATA, "test ranker scheduler");

			// processor.output().send(MessageBuilder.withPayload(json.toString()).build());
			this.sendMessage(json.toString());
		}
	}

	private void callCrawler(Schedule schedule) {
		// TODO crawler 직접 호출
		log.info("call crawler : {}", schedule);
	}

	private void sendMessage(String message) {
		this.rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, JobUtil.getRoutingKey(RabbitMQConfig.QUEUE_NAME), message);
	}

}
