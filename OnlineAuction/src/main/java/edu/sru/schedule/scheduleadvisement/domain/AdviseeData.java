package edu.sru.schedule.scheduleadvisement.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Represents an advisee within the system. 
 * Stores personal and academic information about advisees.
 */
@Entity
public class AdviseeData {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private String password;
	private Long advisorId; //advisor information
	
	public AdviseeData() {
	}

	public AdviseeData(String first, String last, String mail, String phone, String pass) {
		this.firstName = first;
		this.lastName = last;
		this.email = mail;
		this.phoneNumber = phone;
		this.password = pass;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String toString(){
		return String.format("First Name: %s , Last Name: %s , Email: %s , Phone Number: %s", firstName, lastName, email, phoneNumber);
	}

	public Long getAdvisorId() {
	    return advisorId;
	}

	public void setAdvisorId(Long advisorId) {
	    this.advisorId = advisorId;
	}
}

