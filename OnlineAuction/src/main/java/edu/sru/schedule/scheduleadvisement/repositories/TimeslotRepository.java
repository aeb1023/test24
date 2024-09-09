package edu.sru.schedule.scheduleadvisement.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import edu.sru.schedule.scheduleadvisement.domain.Timeslot;

/**
 * Repository for accessing timeslot data from the database.
 * Extends CrudRepository for basic CRUD operations on Timeslot entities.
 */
public interface TimeslotRepository  extends CrudRepository<Timeslot, Long> {
	List<Timeslot> findByAdvisorId(Long advisorId);
	
	 boolean existsByDateAndStartTimeAndEndTimeAndLengthAndAdvisorId(Date date, String startTime, 
			 String endTime, int length, Long advisorId);
}
