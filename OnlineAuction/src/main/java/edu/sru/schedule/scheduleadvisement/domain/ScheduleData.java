package edu.sru.schedule.scheduleadvisement.domain;

import java.util.Date;
import java.util.LinkedList;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

/**
 * Entity class representing scheduling data for appointments between students
 * and advisors. Stores details such as time, date, and personal information
 * associated with each schedule.
 */
@Entity
public class ScheduleData {

	@Transient
	private String date;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String time;
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private long timeslotId;
	private Long advisorId; // advisor information

	public ScheduleData() {
		// default constructor
	}

	public ScheduleData(String time, String firstName, String lastName, String email, String phoneNumber,
			long timeslotId) {
		setTime(time);
		setFirstName(firstName);
		setLastName(lastName);
		setEmail(email);
		setPhoneNumber(phoneNumber);
		setTimeslotId(timeslotId);
	}

	@Column(nullable = true)
	public long getId() {
		return id;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTime() {
		return this.time;
	}

	@Column(nullable = true)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(nullable = true)
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(nullable = true)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(nullable = true)
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setTimeslotId(long id) {
		this.timeslotId = id;
	}

	public long getTimeslotId() {
		return this.timeslotId; // the timeslot that corresponds to this schedule data
	}

	public Long getAdvisorId() {
		return advisorId;
	}

	public void setAdvisorId(Long advisorId) {
		this.advisorId = advisorId;
	}

	// date
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
