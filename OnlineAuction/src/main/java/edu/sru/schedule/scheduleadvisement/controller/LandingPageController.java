package edu.sru.schedule.scheduleadvisement.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.sru.schedule.scheduleadvisement.domain.AdminSettings;
import edu.sru.schedule.scheduleadvisement.domain.user.User;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRepository;
import edu.sru.schedule.scheduleadvisement.secure.TwoFactorAuthentication;
import edu.sru.schedule.scheduleadvisement.service.AdminSettingsService;
import edu.sru.schedule.scheduleadvisement.service.UserService;


/**
 * Controller for handling the main landing pages and user settings.
 * This includes password changes, email updates, and displaying various landing pages
 * depending on the user's role.
 */
@Controller
public class LandingPageController {

	@Autowired
	private UserService userService;

	@Autowired
	private TwoFactorAuthentication twoFactorAuthentication;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AdminSettingsService adminSettingsService;



	/**
     * Displays the main homepage for logged-in users, showing user details and administrative settings.
     * 
     * @param model The model to add attributes to for rendering views.
     * @param principal The security context of the authenticated user.
     * @return The name of the home page view "homePage".
     */
	@RequestMapping({ "homePage" })
	public String showHomePage(Model model, Principal principal) {
		String username = principal.getName();
		User user = userService.getUserByUsername(username);
		model.addAttribute("user", user);
		model.addAttribute("roles", user.getUserRole().getAuthority());

		// Fetch admin settings
		// "Not set" if values have not been updated by admin (null)
		AdminSettings settings = adminSettingsService.getSettings();
		model.addAttribute("academicYearStart", settings != null ? settings.getAcademicYearStart() : "Not Set");
		model.addAttribute("academicYearEnd", settings != null ? settings.getAcademicYearEnd() : "Not Set");
		model.addAttribute("semester", settings != null ? settings.getSemester() : "Not Set");
		model.addAttribute("registrationStartDate", settings != null ? settings.getRegistrationStartDate() : "Not Set");
	    model.addAttribute("registrationEndDate", settings != null ? settings.getRegistrationEndDate() : "Not Set");

		return "homePage";
	}

	@RequestMapping({ "/updateUser" })
	public String showHomePage(Model model, @RequestParam(value = "2FA", required = false) String is2FAEnabled,
			Principal principal) {
		String username = principal.getName();
		User user = userService.getUserByUsername(username);
		model.addAttribute("user", user);

		boolean twoFactorEnabled = "on".equals(is2FAEnabled);
		if (twoFactorEnabled)
			twoFactorAuthentication.set2FAEnabled(user, true);
		else
			twoFactorAuthentication.set2FAEnabled(user, false);

		model.addAttribute("user", user);
		userRepository.save(user);

		return "homePage";
	}

	/**
     * Displays the admin landing page.
     *
     * @param model The model to add attributes to for rendering views.
     * @param principal The security context of the authenticated user.
     * @return The name of the admin view "admin".
     */
	@RequestMapping("/admin")
	public String showAdminPage(Model model, Principal principal) {
		String username = principal.getName();
		User user = userService.getUserByUsername(username);
		model.addAttribute("user", user);

		return "admin";
	}


	/**
     * Displays the student dashboard.
     *
     * @param model The model to add attributes to for rendering views.
     * @param principal The security context of the authenticated user.
     * @return The name of the student dashboard view "student".
     */
	@RequestMapping("/student")
	public String showStudentDashboard(Model model, Principal principal) {
		String username = principal.getName();
		User user = userService.getUserByUsername(username);
		model.addAttribute("user", user);

		return "student";
	}


	/**
     * Displays the faculty landing page.
     *
     * @param model The model to add attributes to for rendering views.
     * @param principal The security context of the authenticated user.
     * @return The name of the faculty view "faculty".
     */
	@RequestMapping("/faculty")
	public String showFacultyPage(Model model, Principal principal) {
		String username = principal.getName();
		User user = userService.getUserByUsername(username);
		model.addAttribute("user", user);

		return "faculty";
	}

	 /**
     * Displays the settings page where users can change their password, email, and other settings.
     *
     * @param model The model to add attributes to for rendering views.
     * @return The name of the user settings view "userSettings".
     */
	@GetMapping("/userSettings") 
	public String showSettings(Model model) {
		User userForm = new User();
		model.addAttribute("userForm", userForm);
		return "userSettings";

	}
	
	/**
     * Displays the password change form.
     *
     * @param model The model to add attributes to for rendering views.
     * @return The name of the change password view "changePassword".
     */
	@GetMapping("/changePassword") 
	public String changepassword(Model model) {
		User userForm = new User();
		model.addAttribute("userForm", userForm);
		return "changePassword";

	}

	/**
     * Allows users to update their password. Checks old password for correctness and confirms that new passwords match.
     *
     * @param oldPassword The user's old password.
     * @param newPassword The user's new password.
     * @param confirmPassword Confirmation of the new password.
     * @param principal The security context of the authenticated user.
     * @param redirectAttributes Attributes for passing messages or data while redirecting.
     * @return Redirect path after updating the password.
     */
	@PostMapping("/change-password")
	public String changeStudentPassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, @RequestParam("confirmPassword") String confirmPassword,
			Principal principal, RedirectAttributes redirectAttributes) {
		String username = principal.getName();
		User user = userRepository.findByUsername(username); // Retrieve the logged in username

		if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
			// If the old password doesn't match the current password - error
			redirectAttributes.addFlashAttribute("error", "Your current password is incorrect.");
			return "redirect:/changePassword";
		}

		if (!newPassword.equals(confirmPassword)) {
			// If the new password and confirm new password don't match - error
			redirectAttributes.addFlashAttribute("error", "The new passwords do not match.");
			return "redirect:/changePassword";
		}

		// Update the user's password
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);

		// Add a success message
		redirectAttributes.addFlashAttribute("success", "Your password has been updated successfully.");

		return "redirect:/userSettings";
	}
	
	/**
     * Displays the change email page.
     *
     * @param model The model to add attributes to for rendering views.
     * @return The name of the change email view "changeEmail".
     */
	@GetMapping("/changeEmail") 
	public String changeEmail(Model model) {
		User userForm = new User();
		model.addAttribute("userForm", userForm);
		return "changeEmail";

	}
	
	/**
     * Processes the request to change the user's email.
     *
     * @param oldEmail The user's old email.
     * @param newEmail The user's new email.
     * @param confirmEmail Confirmation of the new email to ensure accuracy.
     * @param principal The security context of the authenticated user.
     * @param redirectAttributes Attributes for passing messages or data while redirecting.
     * @return Redirect path after attempting to update the email.
     */
	@PostMapping("/change-email")
	public String changeStudentEmail(@RequestParam("oldEmail") String oldEmail,
			@RequestParam("newEmail") String newEmail, @RequestParam("confirmEmail") String confirmEmail,
			Principal principal, RedirectAttributes redirectAttributes) {
		String username = principal.getName();
		User user = userRepository.findByUsername(username); // Retrieve the logged in username


		if (!newEmail.equals(confirmEmail)) {
			redirectAttributes.addFlashAttribute("error", "The new emails do not match.");
			return "redirect:/changeEmail";
		}

		userRepository.save(user);

		// Add a success message
		redirectAttributes.addFlashAttribute("success", "Your email has been updated successfully.");

		return "redirect:/changeEmail";
	}
	
	 /**
     * Displays the form for changing the user's phone number.
     *
     * @param model The model to add attributes to for rendering views.
     * @return The name of the phone number change form view "changePhoneNumber.
     */
	@GetMapping("/changePhoneNumber") 
	public String changePhoneNumber(Model model) {
		User userForm = new User();
		model.addAttribute("userForm", userForm);
		return "changePhoneNumber";

	}
	
	 /**
     * Processes the request to change the user's phone number. Validates that the new phone numbers match.
     *
     * @param oldPhoneNumber The user's old phone number.
     * @param newPhoneNumber The user's new phone number.
     * @param confirmPhoneNumber Confirmation of the new phone number to ensure accuracy.
     * @param principal The security context of the authenticated user.
     * @param redirectAttributes Attributes for passing messages or data while redirecting.
     * @return Redirect path after attempting to update the phone number.
     */
	@PostMapping("/change-phoneNumber")
	public String changePhoneNumber(@RequestParam("oldPhoneNumber") String oldPhoneNumber,
			@RequestParam("newPhoneNumber") String newPhoneNumber, @RequestParam("confirmPhoneNumber") String confirmPhoneNumber,
			Principal principal, RedirectAttributes redirectAttributes) {
		String phoneNumber = principal.getName();
		User user = userRepository.findByUsername(phoneNumber); 


		if (!newPhoneNumber.equals(confirmPhoneNumber)) {
			redirectAttributes.addFlashAttribute("error", "The new phone numbers do not match.");
			return "redirect:/changePhoneNumber";
		}

		userRepository.save(user);

		// Add a success message
		redirectAttributes.addFlashAttribute("success", "Your phone number has been updated successfully.");

		return "redirect:/changePhoneNumber";
	}
}


