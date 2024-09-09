package edu.sru.schedule.scheduleadvisement.service;

import java.util.LinkedList;

import edu.sru.schedule.scheduleadvisement.domain.ScheduleData;

/**
 * A custom linked list extending the Java LinkedList class designed
 * to manage collections of ScheduleData objects.
 */
public class ScheduleLinkedList<E> extends LinkedList<E> {

	private static final long serialVersionUID = -6103285939375725581L;
	
	/**
     * Retrieves a ScheduleData object from the list that matches the specified ID and time.
     * 
     * @param id The timeslot ID to match in the schedule data.
     * @param time The specific time string to match in the schedule data.
     * @return The ScheduleData object if a match is found, null otherwise.
     */
	public ScheduleData getScheduleData(long id, String time) {
		for(int i = 0; i < this.size(); i++) {
			Object element = this.get(i);
			if(element instanceof ScheduleData) {
				ScheduleData temp = (ScheduleData) element;
//				System.out.println("Timeslot time: " + time);
//				System.out.println("ScheduleData time: " + temp.getTime());
				if(temp.getTimeslotId() == id && temp.getTime().equals(time)) {
					return (ScheduleData) element;
				}
			}
		}
		return null;
	}
	
}