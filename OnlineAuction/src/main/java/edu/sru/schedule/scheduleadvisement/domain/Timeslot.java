package edu.sru.schedule.scheduleadvisement.domain;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Represents a timeslot within the system.
 * This includes start and end times, date, duration, and related advisor information.
 */
@Entity
public class Timeslot {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private boolean enabled = true;
	private Date date;
	private String startTime;
	private String endTime;
	private int length;
	private List<String> intervals;
	private LinkedList<String> individualTimes;
	private Long advisorId; // advisor information
	
	
	public Timeslot() {
		//default constructor
	}
	

	public Timeslot(Date Date, String startTime, String endTime, int length) {
		setDate(Date);
		setStartTime(startTime);
		setEndTime(endTime);
		setLength(length);
		intervals = getTimeIntervals(startTime, endTime);
		individualTimes = new LinkedList<>();
	}
	
	public List<String> getIntervals() {
		return this.intervals;
	}
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date Date) {
		this.date = Date;
	}

	public String getStartTime() {
		return this.startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return this.endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	public Long getAdvisorId() {
	    return advisorId;
	}

	public void setAdvisorId(Long advisorId) {
	    this.advisorId = advisorId;
	}
	
	public List<String> getIndividualTimes(){
		return individualTimes;
	}
	public void setIndividualTimes(LinkedList<String> individualTimes) {
		this.individualTimes = individualTimes;
	}
	public void addIndividualTime(String time) {
		individualTimes.add(time);
	}
	public void removeIndividualTime(String time) {
		individualTimes.remove(time);
	}
	
	public String toString(){
		return String.format("Date: %s ,  Start Time: %s , End Time: %s , Length: %d minutes", date, startTime, endTime, length);
	}
	
	public List<String> getTimeIntervals(String startTime, String endTime) {
        List<String> timeIntervals = new ArrayList<>();

        // Parse start time
        LocalTime start = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("h:mma"));

        // Parse end time
        LocalTime end = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("h:mma"));

        // Add intervals
        while (start.isBefore(end)) {
            timeIntervals.add(start.format(DateTimeFormatter.ofPattern("h:mma")));
            start = start.plusMinutes(getLength());
        }

        // Add the end time
        timeIntervals.add(end.format(DateTimeFormatter.ofPattern("h:mma")));

        return timeIntervals;
    }


}
