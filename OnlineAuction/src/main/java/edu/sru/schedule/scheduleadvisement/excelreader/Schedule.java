package edu.sru.schedule.scheduleadvisement.excelreader;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.text.SimpleDateFormat;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.sru.schedule.scheduleadvisement.controller.EmailController;
import edu.sru.schedule.scheduleadvisement.domain.ScheduleData;
import edu.sru.schedule.scheduleadvisement.domain.Timeslot;
import edu.sru.schedule.scheduleadvisement.domain.user.User;
import edu.sru.schedule.scheduleadvisement.domain.user.UserRole;
import edu.sru.schedule.scheduleadvisement.repositories.AdviseeRepository;
import edu.sru.schedule.scheduleadvisement.repositories.AdvisorRepository;
import edu.sru.schedule.scheduleadvisement.repositories.ScheduleRepository;
import edu.sru.schedule.scheduleadvisement.repositories.TimeslotRepository;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRepository;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRoleRepository;
import edu.sru.schedule.scheduleadvisement.service.ScheduleLinkedList;
import edu.sru.schedule.scheduleadvisement.service.UserService;

/**
 * Controller responsible for managing scheduling functionalities within the application.
 * This includes viewing, selecting, canceling, and deleting schedule timeslots.
 */
@Controller
public class Schedule {
	
	@Autowired
	private TimeService timeService;
	private User user;
	private UserRole userRole;
	
	@Autowired
	private ScheduleService scheduleService;
	
	private final ScheduleRepository scheduleRepository;
	private final TimeslotRepository timeslotRepository;
	private final UserRoleRepository userRoleRepository;
	private final AdviseeRepository adviseeRepository;
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EmailController emailController;

	
	public Schedule(ScheduleRepository scheduleRepository, TimeslotRepository timeslotRepository, 
			UserRoleRepository userRoleRepository,
			AdviseeRepository adviseeRepository,
			UserRepository userRepository) {
		this.scheduleRepository = scheduleRepository;
		this.timeslotRepository = timeslotRepository;
		this.userRoleRepository = userRoleRepository;
		this.adviseeRepository = adviseeRepository;
		this.userRepository = userRepository;
	}
	
	@Autowired
	private UserService userService;

	 /**
     * Displays the schedule page with timeslots associated with the logged-in advisor or student.
     *
     * @param model The Spring MVC Model.
     * @param principal The authenticated user.
     * @return The name of the view "schedule".
     */
	@GetMapping({ "/schedule" })
	public String getSchedule(Model model, Principal principal) {
		String username = principal.getName();
		User user = userService.getUserByUsername(username);
		LinkedList<Timeslot> timeslotList = timeService.getTimeslotList();	
		ScheduleLinkedList<ScheduleData> scheduleList = scheduleService.getScheduleList();
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String advisorEmail = authentication.getName();
		User advisor = userRepository.findByUsername(advisorEmail);
		Long advisorId = advisor.getId();
		
		model.addAttribute("roles", user.getUserRole().getAuthority());	
		
		// only the timeslots associated with the logged in advisor
		List<Timeslot> timeslots = timeslotRepository.findByAdvisorId(advisorId);
		
		model.addAttribute("timeslots", timeslotList);
		
		System.out.println("\nDEBUG: schedule list before" + scheduleList);
		System.out.println("DEBUG: timeslot list before" + timeslotList);
		
		
		// separate schedule between advisors
		if(user.getUserRole().getAuthorityName().equals("ROLE_FACULTY")) {
			timeslotList.removeIf(scheduleData-> !scheduleData.getAdvisorId().equals(user.getId()));
			System.out.println("\nDEBUG: user ID " + user.getId());
			System.out.println("DEBUG: timeslot list after" + timeslotList);
		}
		
		// separate schedules between advisors with students
		else {
		adviseeRepository.findByEmail(username).ifPresent(studentData -> {
			Long advisorsId = studentData.getAdvisorId();
			timeslotList.removeIf(scheduleData-> !scheduleData.getAdvisorId().equals(advisorsId));
			System.out.println("\nDEBUG: advisor ID " + advisorsId);
			System.out.println("DEBUG: timeslotlist after" + timeslotList);
		});
		}
		
		
		model.addAttribute("schedules", scheduleList);
		System.out.println("DEBUG: list after" + scheduleList);
		
		
		return "schedule";
	}
	
	/**
     * Handles the submission to select a timeslot.
     *
     * @param firstName The first name of the person scheduling.
     * @param lastName The last name of the person scheduling.
     * @param email The email of the person scheduling.
     * @param time The selected time.
     * @param phoneNumber The phone number of the person scheduling.
     * @param timeslotId The id of the selected timeslot.
     * @param advisorId The id of the advisor for the timeslot.
     * @param model The Spring MVC Model.
     * @param principal The authenticated user.
     * @param redirectAttributes Attributes for a redirect scenario.
     * @return The redirection path.
     */
	@PostMapping("/select")
	public String selectTimeslot(@RequestParam("firstName") String firstName,
	                             @RequestParam("lastName") String lastName,
	                             @RequestParam("email") String email,
	                             @RequestParam("time") String time, 
	                             @RequestParam("phoneNumber") String phoneNumber,
	                             @RequestParam("timeslotId") long timeslotId, 
	                             @RequestParam("advisorId") Long advisorId, 
	                             Model model, Principal principal,
	                             RedirectAttributes redirectAttributes) {  
	    String username = principal.getName();
	    User user = userService.getUserByUsername(username);
	    long id = -1;

	    List<String> successMessages = new ArrayList<>();
	    List<String> errorMessages = new ArrayList<>();

	    if(user.getUserRole().getAuthorityName().equalsIgnoreCase("user")) {
	        id = user.getId();
	    } else {
	        for(User u : userService.getAllUsers()) {
	            if(u != null && u.getEmail() != null && u.getEmail().equalsIgnoreCase(user.getEmail())) {
	                id = u.getId();
	            }
	        }
	    }

	    System.out.println("Advisor ID: " + advisorId);
	    System.out.println("Clicked on timeslot ID: " + timeslotId);

	    if(firstName.isEmpty() || lastName.isEmpty() || time.isEmpty() || phoneNumber.isEmpty() || email.isEmpty()) {
	        redirectAttributes.addFlashAttribute("errorMessage", "One or more fields are empty.");
	        System.out.println("DEBUG: one or more fields are empty");
	        return "redirect:/schedule";
	    }

	    ScheduleData newSchedule = new ScheduleData(time, firstName, lastName, email, phoneNumber, timeslotId);

	    boolean isScheduleSaved = scheduleService.hasUserScheduled(email);

	    String userName = user.getUsername();
	    String userPhone = user.getPhoneNumber();

	    System.out.println("DEBUG: user "+ userName);
	    System.out.println("DEBUG: role " + user.getUserRole().getAuthorityName());

	    if (newSchedule != null && userName.contains(email) && userPhone.contains(userPhone)) {
	        if (!isScheduleSaved) {
	            newSchedule.setAdvisorId(advisorId);
	            scheduleService.saveSchedule(newSchedule);
	            scheduleRepository.save(newSchedule);
	            redirectAttributes.addFlashAttribute("successMessage", "Sign up successful.");

	            // Send appointment confirmation email
	            User advisor = userService.getUserById(advisorId);
	            Timeslot timeslot = timeService.getById(timeslotId);  // get the timeslot for the date information
	            if (advisor != null && timeslot != null) {
	                String advisorName = advisor.getFirstName() + " " + advisor.getLastName();
	                String advisorEmail = advisor.getEmail();
	                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	                String dateString = dateFormat.format(timeslot.getDate());
	                String appointmentDetails = String.format(
	                    "Date: %s\nTime: %s\nAdvisor: %s\nAdvisor Email: %s", 
	                    dateString, 
	                    time, 
	                    advisorName, 
	                    advisorEmail
	                );
	                emailController.sendAppointmentConfirmationEmail(email, appointmentDetails);
	            }
	        } else {
	            System.out.println("\nDEBUG: checking sign up advisee fail");
	            redirectAttributes.addFlashAttribute("errorMessage", user.getEmail() + " is already scheduled.");
	        }
	    } else if(newSchedule != null && user.getUserRole().getAuthorityName().equals("ROLE_FACULTY")) {
	        if(userRepository.findByEmail(email) != null) {
	            if(!isScheduleSaved) {
	                newSchedule.setAdvisorId(advisorId);
	                scheduleService.saveSchedule(newSchedule);
	                scheduleRepository.save(newSchedule);
	                redirectAttributes.addFlashAttribute("successMessage", "Sign up successful.");
	            } else {
	                System.out.println("\nDEBUG: checking sign up advisor error");
	                redirectAttributes.addFlashAttribute("errorMessage", firstName + " " + lastName + " (" + email + ") is already scheduled.");
	            }
	        } else {
	            redirectAttributes.addFlashAttribute("errorMessage", "Not a valid advisee.");
	            System.out.println("DEBUG: valid faculty, not valid sign up");
	        }
	    } else if(user.getUserRole().getAuthorityName().equals("ROLE_USER")) {
	        redirectAttributes.addFlashAttribute("errorMessage", "Please enter " + user.getFirstName() + " " + user.getLastName() + "'s (" + user.getEmail() + ") information to schedule.");
	        System.out.println("DEBUG: Not a valid email and/or phone number");
	    } else {
	        redirectAttributes.addFlashAttribute("errorMessage", "Not a valid email and/or phone number.");
	        System.out.println("DEBUG: Not a valid email and/or phone number");
	    }

	    return "redirect:/schedule";
	}

	
	/**
     * Handles requests to cancel a selected timeslot.
     *
     * @param scheduleId The id of the schedule to cancel.
     * @param model The Spring MVC Model
     * @param redirectAttributes Attributes for a redirect scenario.
     * @param principal The authenticated user.
     * @return The redirection path.
     */
	@PostMapping("/cancel")
    public String cancelTimeslot(@RequestParam("scheduleId") long scheduleId,
    		@RequestParam("time") String time, 
    		@RequestParam("timeslotId") long timeslotId,
    		@RequestParam("advisorId") Long advisorId,
    		Model model, RedirectAttributes redirectAttributes, Principal principal) {
        if(time != null) {
        	System.out.println("DEBUG: Canceled " + time + " in schedule");
        	System.out.println("DEBUG: schedule ID " + scheduleId);
        	System.out.println("DEBUG: timeslot ID " + timeslotId);
//        	System.out.println("DEBUG: advisor ID " + advisorId);
        	String username = principal.getName();
        	User user = userService.getUserByUsername(username);
        	String email = user.getEmail();
        	
        	if(scheduleId == -1) {
        		System.out.println("DEBUG: Time not scheduled");
        	}
        	
        	// lets the advisee cancel
        	else if(scheduleService.hasUserScheduled(email) && user.getUserRole().getAuthorityName().equals("ROLE_USER")) {
        		scheduleService.deleteScheduleById(scheduleId);
        		redirectAttributes.addFlashAttribute("successMessage", "You have successfully canceled your appointment.");
        	
        	// lets the faculty cancel
        	}else if(user.getUserRole().getAuthorityName().equals("ROLE_FACULTY")) {
        		scheduleService.deleteScheduleById(scheduleId);
        		redirectAttributes.addFlashAttribute("successMessage", "Appointment successfully canceled.");
        	}
        	// prevents advisees from canceling for other students
        	else {
        		redirectAttributes.addFlashAttribute("errorMessage", "You can only cancel for yourself.");
        	}
	    }else {
	    	System.out.println("DEBUG: No ID provided");
	    }
        return "redirect:/schedule";
    }
	
	/**
     * Deletes an entire appointment timeslot from the schedule.
     * 
     * @param scheduleId The ID of the schedule associated with the timeslot to delete.
     * @param timeslotId The ID of the timeslot to be deleted.
     * @param advisorId The ID of the advisor associated with this timeslot. This parameter may be used for validation or logging.
     * @param time The scheduled time string, used for logging or display purposes before deletion.
     * @param model The Spring MVC Model, used to add attributes to the view, if needed, post-deletion.
     * @return A redirect back to the schedule view.
     */
	@PostMapping("/delete")
	public String deleteTimeslot(@RequestParam("scheduleId") Long scheduleId, 
			@RequestParam("timeslotId") Long timeslotId,
			@RequestParam("advisorId") Long advisorId,
			@RequestParam("time") String time, Model model) {
		Optional<ScheduleData> optionalSchedule = scheduleRepository.findById(scheduleId);
		if(optionalSchedule.isPresent()) {
			scheduleService.deleteScheduleById(scheduleId);
		    }
		Timeslot timeslot = timeService.getById(timeslotId);
		List<String> times = timeslot.getIntervals();
		if(!times.contains(time)) {
			System.out.println("DEBUG time: " + time + " not found");
		}else {
			System.out.println("DEBUG time: " + time + " found");
			times.remove(time);
			timeslotRepository.save(timeslot);
			System.out.println("DEBUG - current time list: " + times);
		}
	    return "redirect:/schedule";
	}


}

