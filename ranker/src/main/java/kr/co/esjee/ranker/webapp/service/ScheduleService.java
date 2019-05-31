package kr.co.esjee.ranker.webapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.esjee.ranker.webapp.model.Schedule;
import kr.co.esjee.ranker.webapp.repository.ScheduleRepository;

@Service
public class ScheduleService extends AppService {

	@Autowired
	private ScheduleRepository repository;

	public Schedule save(Schedule schedule) {
		if (schedule.getId() == 0) {
			schedule.setId(super.getNextId(INDICES.schedule));
		}

		return repository.save(schedule);
	}

}
