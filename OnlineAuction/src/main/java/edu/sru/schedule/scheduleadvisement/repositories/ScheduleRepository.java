package edu.sru.schedule.scheduleadvisement.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import edu.sru.schedule.scheduleadvisement.domain.ScheduleData;

import edu.sru.schedule.scheduleadvisement.domain.user.User;

/**
 * Repository interface for accessing and managing ScheduleData entities.
 * Extends CrudRepository for common CRUD operations.
 */
public interface ScheduleRepository extends CrudRepository<ScheduleData, Long>{

	ScheduleData findByEmail(String email);
	List<User> findByEmailContaining(String query);
  List<ScheduleData> findByAdvisorId(Long advisorId);
}

