package edu.sru.schedule.scheduleadvisement.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RestController;

import edu.sru.schedule.scheduleadvisement.domain.user.User;
import jakarta.servlet.ServletContext;



/**
 * EmailController handles all email operations within the system, including sending notifications,
 * appointment confirmations, two-factor authentication codes, and account verification links.
 */
@RestController
public class EmailController {

	@Value("${spring.mail.port}")
	private int mailPort;

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private JavaMailSender javaMailSender;

	/**
     * Sends an email to a list of recipients.
     *
     * @param recipients a list of email addresses to receive the message
     * @param subject the subject line of the email
     * @param body the main content of the email
     */
	@Async
	public void sendEmailToList(List<String> recipients, String subject, String body) {
		for (String recipient : recipients) {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(recipient);
			message.setSubject(subject);
			message.setText(body);
			javaMailSender.send(message);
		}
	}
	
	/**
     * Sends an appointment confirmation email.
     *
     * @param email the email address to send the confirmation to
     * @param appointmentDetails details about the appointment
     */
	@Async
	public void sendAppointmentConfirmationEmail(String email, String appointmentDetails) {
	    SimpleMailMessage message = new SimpleMailMessage();
	    try {
	        message.setTo(email);
	        message.setSubject("Appointment Confirmation");
	        message.setText(String.format(
	                "Thank you for scheduling your appointment.\n"
	                + "Here are the details of your appointment: %s\n"
	                + "If you have any questions, please contact us.",
	                appointmentDetails));

	        javaMailSender.send(message);
	        System.out.println("Appointment confirmation email sent successfully to: " + email);
	    } catch (MailException e) {
	        System.err.println("Failed to send appointment confirmation email to: " + email);
	        e.printStackTrace();
	    }
	}

	/**
     * Sends a verification email to verify an account. Currently not in use as user sign-up is not required.
     *
     * @param recipient the user to whom the email is sent
     * @param code the verification code
     */
	@Async
	public void sendTwoFactorAuthEmail(User user, String code) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setSubject("<no-reply> Two Factor Authentication");
		message.setText("Your two factor authentication code is: " + code + "\n"
				+ "If you have received this email in error please contact us immediately.");
		javaMailSender.send(message);
	}

	/**
     * Sends a verification email to verify an account. Currently not in use as user sign-up is not required.
     *
     * @param recipient the user to whom the email is sent
     * @param code the verification code
     */
	public void verificationEmail(User recipient, String code) {
		User user = recipient;
		user.setEmailVerification(code);
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setSubject("<no-reply> Welcome!");

		String verificationLink = String.format("%s/emailverification", servletContext.getContextPath());
		try {
			message.setText(String.format(
					"Hello %s please use the following link http://%s:%s%s to verify your account with code:\n%s",
					user.getUsername(), InetAddress.getLocalHost().getHostAddress(), mailPort, verificationLink, code));
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
		javaMailSender.send(message);
	}
    
	 /**
     * Sends a username recovery email.
     *
     * @param user the user whose username is being recovered
     */
	public void usernameRecovery(User user) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setSubject("<no-reply> Username Recovery");
		message.setText("Your username is: " + user.getUsername() + "\n"
				+ "If you have received this email in error please contact us immediately.");
		javaMailSender.send(message);
	}

}

