package edu.sru.schedule.scheduleadvisement.excelreader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.LinkedList;

import org.springframework.stereotype.Service;

import edu.sru.schedule.scheduleadvisement.domain.Timeslot;


/**
 * Service class for managing timeslots.
 * Provides functionalities to add, remove, update, and retrieve timeslot details.
 */
@Service
public class TimeService {

	private final LinkedList<Timeslot> timeslotList = new LinkedList<>();
	
	/**
     * Retrieves a timeslot by its attributes.
     * 
     * @param date The date of the timeslot.
     * @param startTime The starting time of the timeslot.
     * @param endTime The ending time of the timeslot.
     * @param length The duration of the timeslot in minutes.
     * @return The timeslot matching the specified attributes or null if no match is found.
     */
	public Timeslot getTimeslotByAttribute(Date date, String startTime, String endTime, int length) {
		for(Timeslot timeslot : timeslotList) {
			if(timeslot.getDate().equals(date) && timeslot.getStartTime().equals(startTime) && timeslot.getEndTime().equals(endTime) && timeslot.getLength() == length) {
				return timeslot;
			}
		}
		return null;
	}
	
	/**
     * Adds a new timeslot to the list.
     * 
     * @param newTimeslot The new timeslot to be added.
     */
	public void addTimeslot(Timeslot newTimeslot) {
		timeslotList.add(newTimeslot);
	}
	
	/**
     * Deletes a timeslot from the list by its ID.
     * 
     * @param timeslotId The ID of the timeslot to be removed.
     */
	public void deleteTimeslotById(Long timeslotId) {
		timeslotList.removeIf(timeslot -> timeslot.getId() == timeslotId);
	}
	
	 /**
     * Updates the attributes of an existing timeslot.
     * 
     * @param timeslotId The ID of the timeslot to be updated.
     * @param date The new date of the timeslot.
     * @param startTime The new start time of the timeslot.
     * @param endTime The new end time of the timeslot.
     * @param length The new duration of the timeslot in minutes.
     */
	public void updateTimeslot(Long timeslotId, Date date, String startTime, String endTime, int length) {
		Timeslot timeslotToUpdate = getById(timeslotId);
		if(timeslotToUpdate != null) {
			timeslotToUpdate.setDate(date);
			timeslotToUpdate.setStartTime(startTime);
			timeslotToUpdate.setEndTime(endTime);
			timeslotToUpdate.setLength(length);
			
			int index = timeslotList.indexOf(timeslotToUpdate);
			if(index != -1) {
				timeslotList.set(index, timeslotToUpdate);
				System.out.println("DEBUG: update timeslot");
			}
		}
	}
	
	 /**
     * Retrieves the list of all timeslots.
     * 
     * @return A LinkedList containing all timeslots managed by this service.
     */
	public LinkedList<Timeslot> getTimeslotList(){
		return timeslotList;
	}
	
	/**
     * Retrieves a timeslot by its ID.
     * 
     * @param timeslotId The ID of the timeslot to retrieve.
     * @return The timeslot with the specified ID or null if no timeslot is found.
     */
	public Timeslot getById(Long timeslotId) {
		for(Timeslot timeslot : timeslotList) {
			if(timeslot.getId() == timeslotId) {
				return timeslot;
			}
		} 
		return null;
	}
	
	/**
     * Checks if a timeslot exists for a specified advisor.
     * 
     * @param advisorId The ID of the advisor to check for existing timeslots.
     * @return true if at least one timeslot exists for the advisor, otherwise false.
     */
	public boolean doesTimeExist(Long advisorId) {
		for(Timeslot time : timeslotList) {
			if(time.getAdvisorId().equals(advisorId)) {
				return true;
			}
		}
		return false;
	}
	
}
