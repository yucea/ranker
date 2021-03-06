package kr.co.esjee.ranker.webapp.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.esjee.ranker.webapp.model.Schedule;
import kr.co.esjee.ranker.webapp.repository.ScheduleRepository;

@Service
public class ScheduleService extends AppService {

	@Autowired
	private ScheduleRepository repository;

	public Page<Schedule> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
	
	public Page<Schedule> findByScheduleNameLike(String searchKey, Pageable pageable) {
		return repository.findByScheduleNameLike(searchKey, pageable);
	}

	public Schedule findById(long id) {
		Optional<Schedule> optional = repository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		} else {
			throw new IllegalArgumentException("Item not found :" + id);
		}
	}
	
	public Schedule save(Schedule schedule) {
		if (schedule.getId() == 0) {
			schedule.setId(super.getNextId(INDICES.schedule));
		}

		return repository.save(schedule);
	}
	
	public void remove(long id) {
		repository.deleteById(id);
	}
}