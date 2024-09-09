package edu.sru.schedule.scheduleadvisement.excelreader;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.sru.schedule.scheduleadvisement.domain.ScheduleData;
import edu.sru.schedule.scheduleadvisement.repositories.ScheduleRepository;
import edu.sru.schedule.scheduleadvisement.service.ScheduleLinkedList;

/**
 * Service class for managing schedules within the application.
 * such as retrieving, adding, deleting schedules, and checking if a schedule exists.
 */
@Service
public class ScheduleService {
	private final ScheduleLinkedList<ScheduleData> scheduleList = new ScheduleLinkedList<ScheduleData>();
	
	@Autowired
	private ScheduleRepository scheduleRepository;
	
	/**
     * Retrieves the list of all schedules.
     *
     * @return A list of ScheduleData objects representing the schedules.
     */
	public ScheduleLinkedList<ScheduleData> getScheduleList(){
		return scheduleList;
	}
	
	/**
     * Adds a new schedule to the managed list and adds it to the repository.
     *
     * @param scheduleData The ScheduleData object to be added.
     */
	public void saveSchedule(ScheduleData scheduleData) {
		scheduleList.add(scheduleData);
		scheduleRepository.save(scheduleData);
	}
	
	/**
     * Removes a schedule from the managed list and deletes it from the repository by its ID.
     *
     * @param scheduleDataId The ID of the ScheduleData to be deleted.
     */
	public void deleteScheduleById(Long scheduleDataId) {
		scheduleList.removeIf(scheduleData -> scheduleData.getId() == scheduleDataId);
		scheduleRepository.deleteById(scheduleDataId);
	}
	
	/**
     * Checks if a user has a schedule by their email address.
     *
     * @param email The email address to check for existing schedules.
     * @return true if there is at least one schedule associated with the email, otherwise false.
     */
	public boolean hasUserScheduled(String email) {
		for(ScheduleData data : scheduleList) {
			if(data.getEmail().equals(email )) {
				return true;
			}
		}
		return false;
	}
	
}
