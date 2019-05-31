package kr.co.esjee.ranker.webapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.esjee.ranker.webapp.model.Schedule;
import kr.co.esjee.ranker.webapp.repository.ScheduleRepository;

@Service
public class ScheduleService {

	@Autowired
	private ScheduleRepository repository;

	public Schedule save(Schedule schedule) {
		return repository.save(schedule);
	}

	public List<Schedule> findByStatusAndLastRuntimeLessThan(String status, String time) {
		return repository.findByStatusAndLastRuntimeLessThan(status, time);
	}
}
