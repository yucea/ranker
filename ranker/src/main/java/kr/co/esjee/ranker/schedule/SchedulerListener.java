package kr.co.esjee.ranker.schedule;

import org.springframework.cloud.stream.annotation.StreamListener;

import lombok.extern.slf4j.Slf4j;

// @EnableBinding(Processor.class)
@Slf4j
public class SchedulerListener {

	// @Autowired
	// private ScheduleService service;

	@StreamListener(Processor.SCHEDULE)
	public void scheduleHandle(String source) {
		log.info("===> {}, {}", Processor.SCHEDULE, source);

		// Gson gson = new Gson();
		// Schedule schedule = gson.fromJson(source, Schedule.class);

		// XXX crawler 호출

		// schedule.completed();
		//
		// service.save(schedule);

	}

}
