package edu.sru.schedule.scheduleadvisement.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.stream.Collectors;

import edu.sru.schedule.scheduleadvisement.domain.AdviseeData;
import edu.sru.schedule.scheduleadvisement.domain.user.User;
import edu.sru.schedule.scheduleadvisement.repositories.AdviseeRepository;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRepository;

/**
 * Handles faculty interactions, particularly for managing communications
 * with advisees.
 */
@Controller
public class FacultyController {

	@Autowired
	private EmailController emailController;

	@Autowired
	private AdviseeRepository adviseeRepository;

	@Autowired
	private UserRepository userRepository;
	

	/**
     * Displays the faculty dashboard. 
     *
     * @param model the Spring Model to pass attributes to the view
     * @return the name of the file "faculty" that displays the faculty dashboard
     */
	@GetMapping({ "/faculty" }) 
	public String facultyDashboard(Model model) {
		return "faculty";
	}

	/**
     * Displays the form for sending emails to advisees. This form allows faculty members to specify
     * the email subject and body.
     *
     * @param model the Spring Model to pass attributes to the view
     * @return the name of the file "emailAdvisees" that renders the email form
     */
	@GetMapping("/emailAdvisees")
	public String showEmailAdviseesForm(Model model) {
		return "emailAdvisees";
	}

	/**
     * Processes the submission of the email form, sending the specified email to all advisees of the current
     * advisor.
     *
     * @param subject the subject of the email
     * @param body the body content of the email
     * @param authentication provides the current user's authentication information to fetch their details
     * @param model the Spring Model to pass attributes to the view
     * @return the name of the file "emailAdvisees" that confirms the email sending status
     */
	@PostMapping("/sendEmailToAdvisees")
	public String sendEmailToAdvisees(@RequestParam("subject") String subject, @RequestParam("body") String body,
			Authentication authentication, Model model) {
		String advisorEmail = authentication.getName(); // Get advisor's email (username)

		// get advisor user by email to get their ID
		User advisor = userRepository.findByUsername(advisorEmail);
		if (advisor == null) {
			model.addAttribute("errorMessage", "Advisor not found. Please ensure the advisor is correctly logged in.");
			return "emailAdvisees";
		}
		Long advisorId = advisor.getId(); // Get the advisor's ID

		// get all advisees associated with the advisor using the advisor's ID
		List<AdviseeData> advisees = adviseeRepository.findByAdvisorId(advisorId);
		List<String> emailAddresses = advisees.stream().map(AdviseeData::getEmail).collect(Collectors.toList());

		// Send an email to each advisee
		emailController.sendEmailToList(emailAddresses, subject, body);

		model.addAttribute("successMessage", "Emails sent successfully to all advisees.");
		return "emailAdvisees";
	}

}
