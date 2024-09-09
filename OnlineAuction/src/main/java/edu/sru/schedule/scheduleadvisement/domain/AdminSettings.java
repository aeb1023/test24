package edu.sru.schedule.scheduleadvisement.domain;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Represents administrative settings within the application.
 * used to store and manage configuration settings such as academic year, semester, and registration dates.
 */
@Entity
public class AdminSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Academic Year Start is required.")
    private String academicYearStart;

    @NotBlank(message = "Academic Year End is required.")
    private String academicYearEnd;
    
    @NotBlank(message = "Semester is required.")
    private String semester;

    @NotBlank(message = "Support contact email is required.")
    @Email(message = "Please provide a valid email address.")
    private String supportContactEmail;
    
    @NotBlank(message = "Start Date is required.")
    private LocalDate registrationStartDate;
    @NotBlank(message = "End Date is required.")
    private LocalDate registrationEndDate;


    
    public LocalDate getRegistrationStartDate() {
		return registrationStartDate;
	}


	public void setRegistrationStartDate(LocalDate registrationStartDate) {
		this.registrationStartDate = registrationStartDate;
	}


	public LocalDate getRegistrationEndDate() {
		return registrationEndDate;
	}


	public void setRegistrationEndDate(LocalDate registrationEndDate) {
		this.registrationEndDate = registrationEndDate;
	}


	public AdminSettings() {
    }

 
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAcademicYearStart() {
        return academicYearStart;
    }

    public void setAcademicYearStart(String academicYearStart) {
        this.academicYearStart = academicYearStart;
    }

    public String getAcademicYearEnd() {
        return academicYearEnd;
    }

    public void setAcademicYearEnd(String academicYearEnd) {
        this.academicYearEnd = academicYearEnd;
    }

    public String getSupportContactEmail() {
        return supportContactEmail;
    }

    public void setSupportContactEmail(String supportContactEmail) {
        this.supportContactEmail = supportContactEmail;
    }
    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}

