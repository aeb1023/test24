package edu.sru.schedule.scheduleadvisement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import java.security.Principal;
import java.util.Optional;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import edu.sru.schedule.scheduleadvisement.domain.user.User;
import edu.sru.schedule.scheduleadvisement.domain.AdviseeData;
import edu.sru.schedule.scheduleadvisement.domain.ScheduleData;
import edu.sru.schedule.scheduleadvisement.domain.Timeslot;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRepository;
import edu.sru.schedule.scheduleadvisement.repositories.AdviseeRepository;
import edu.sru.schedule.scheduleadvisement.repositories.ScheduleRepository;
import edu.sru.schedule.scheduleadvisement.repositories.TimeslotRepository;

/**
 * Controller responsible for managing student-specific views and interactions.
 */ 
@Controller
public class StudentController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
	private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AdviseeRepository adviseeRepository;
    
    @Autowired
    private ScheduleRepository scheduleRepository;
    
    @Autowired
    private TimeslotRepository timeslotRepository;

    
    /**
     * Displays the student dashboard with information specific to the logged-in student.
     * 
     * @param principal The security principal representing the currently authenticated user.
     * @param model The model to pass attributes used for rendering views.
     * @return The name of the student dashboard view "student".
     */
    @GetMapping("student") //student landing page
    public String studentDashboard(Principal principal, Model model) {
        String username = principal.getName(); 
        Optional<AdviseeData> adviseeDataOptional = adviseeRepository.findByEmail(username);

        if (adviseeDataOptional.isPresent()) {
            AdviseeData adviseeData = adviseeDataOptional.get();
            model.addAttribute("advisee", adviseeData);

            Optional<User> advisorOptional = userRepository.findById(adviseeData.getAdvisorId());
            if (advisorOptional.isPresent()) {
                User advisor = advisorOptional.get();
                model.addAttribute("advisor", advisor);
            } else {
                // handle the case where the advisor is not found
                model.addAttribute("advisorNotFound", true);
            }
        } else {
            // handle the case where the AdviseeData is not found
            model.addAttribute("adviseeNotFound", true);
        }

        return "student"; 
    }

    /**
     * Displays the page showing the student's meetings, including all upcoming meetings
     * with their advisor.
     * 
     * @param principal The security principal representing the currently authenticated user.
     * @param model The model to pass attributes used for rendering views.
     * @return The name of the view displaying the student's meetings "studentMeetings.
     */
    @GetMapping("/studentMeetings") 
    public String viewMeetingsStudent(Principal principal, Model model) {
        String username = principal.getName();
        Optional<AdviseeData> adviseeDataOptional = adviseeRepository.findByEmail(username);
        
        if (adviseeDataOptional.isPresent()) {
            AdviseeData advisee = adviseeDataOptional.get();
          
            List<ScheduleData> schedules = scheduleRepository.findByAdvisorId(advisee.getAdvisorId());
            List<ScheduleData> enhancedSchedules = new ArrayList<>();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 

            for (ScheduleData schedule : schedules) {
                Timeslot timeslot = timeslotRepository.findById(schedule.getTimeslotId()).orElse(null);
                if (timeslot != null) {
                    schedule.setDate(sdf.format(timeslot.getDate())); 
                    enhancedSchedules.add(schedule);
                }
            }

            model.addAttribute("schedules", enhancedSchedules);
        } else {
            model.addAttribute("error", "Advisee data not found.");
        }
        
        return "studentMeetings";
    }
    
}
