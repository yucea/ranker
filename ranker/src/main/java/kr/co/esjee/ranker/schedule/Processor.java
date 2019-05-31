package kr.co.esjee.ranker.schedule;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface Processor {

	// String SCHEDULE_INPUT = "ranker-schedule-exchange";
	String SCHEDULE = "ranker-schedule";

	@Input(SCHEDULE)
	SubscribableChannel input();

	// @Output(SCHEDULE_OUTPUT)
	// MessageChannel output();

}
