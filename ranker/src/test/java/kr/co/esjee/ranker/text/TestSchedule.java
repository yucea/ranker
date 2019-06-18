package kr.co.esjee.ranker.text;

import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;

import kr.co.esjee.ranker.elasticsearch.ElasticOption;
import kr.co.esjee.ranker.elasticsearch.ElasticQuery;
import kr.co.esjee.ranker.elasticsearch.ElasticSearcher;
import kr.co.esjee.ranker.elasticsearch.TestElasticsearch;
import kr.co.esjee.ranker.webapp.model.Schedule;
import kr.co.esjee.ranker.webapp.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Slf4j
public class TestSchedule extends TestElasticsearch {

	private Client client = null;
	private String indexName = "schedule";

	@Before
	public void init() throws UnknownHostException {
		client = super.client();
	}

	@After
	public void destroy() {
		client.close();
	}

	@Autowired
	private ScheduleService service;

	@Test
	public void testRunner() throws Exception {
		int[] week = { 1, 2 };
 
		Schedule schedule = new Schedule();
		schedule.setWeek(week);
		schedule.setMonth(new int[] { 1, 2, 3 });

		service.save(schedule);
	}

	@Test
	public void testSearch() throws Exception {
		BoolQueryBuilder query = QueryBuilders.boolQuery();
		query.must(ElasticQuery.termsQuery(WEEK, 2));

		ElasticOption option = ElasticOption.newInstance()
				.queryBuilder(query)
				.page(1, 10000);

		SearchHits hits = ElasticSearcher.search(client, indexName, option);
		hits.forEach(h -> {
			log.info("{}", h.toString());
			Schedule schedule = new Gson().fromJson(h.toString(), Schedule.class);
			
			System.out.println(schedule);
		});
	}
	
	@Test
	public void testInsSchedule() throws Exception {
		
		int[] week = {0};		// 일요일 : 1, 토요일 : 7, 매주 : 0
		int[] month = {0}; 		// 1-12, 매월 : 0		
		int[] day = {0}; 		// 1-31, 매일 : 0		
		int[] hour = {24}; 		// 0-23, 매시간 : 24		
		int[] minute = {60}; 	// 0-59, 매분 : 60
 
		Schedule schedule = new Schedule();
		
		schedule.setWeek(week);
		schedule.setMonth(month);
		schedule.setDay(day);
		schedule.setHour(hour);
		schedule.setMinute(minute);
		schedule.setUsable(true);

		service.save(schedule);
	}
}
