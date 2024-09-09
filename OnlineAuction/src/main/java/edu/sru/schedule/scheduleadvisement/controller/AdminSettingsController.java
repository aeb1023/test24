package edu.sru.schedule.scheduleadvisement.controller;

import edu.sru.schedule.scheduleadvisement.domain.AdminSettings;
import edu.sru.schedule.scheduleadvisement.service.AdminSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * AdminSettingsController responsible for handling admin settings within the application.
 * allows for viewing and updating settings such as academic year, semester,
 * and registration periods.
 */
@Controller
public class AdminSettingsController {

    private final AdminSettingsService settingsService;

    /**
     * Constructs an AdminSettingsController with a specified AdminSettingsService.
     * 
     * @param settingsService the service used for accessing admin settings
     */
    @Autowired
    public AdminSettingsController(AdminSettingsService settingsService) {
        this.settingsService = settingsService;
    }
   
    /**
     * Displays the current admin settings. If no settings are found,
     * it initializes a new set of default settings.
     * 
     * @param model the model object to which the settings are added
     * @return the name of the view "adminSettings" that displays the settings
     */
    @GetMapping("/admin/settings") 
    public String showSettings(Model model) {
        AdminSettings settings = settingsService.getSettings(); 
        model.addAttribute("settings", settings == null ? new AdminSettings() : settings);
        return "adminSettings";
    }

    /**
     * Updates the administrative settings based on user-submitted form data.
     * After updating, it redirects to the settings page and displays a success message.
     * 
     * @param formSettings the settings submitted by the form
     * @param redirectAttributes attributes for flash attributes (used to pass success message)
     * @return redirect URL to the settings page after update
     */
    @PostMapping("/admin/settings")
    public String updateSettings(AdminSettings formSettings, RedirectAttributes redirectAttributes) {
        AdminSettings settings = settingsService.getSettings(); // Fetch current settings
        if (settings == null) {
            settings = new AdminSettings(); 
        }
        
        // update settings after editing
        settings.setAcademicYearStart(formSettings.getAcademicYearStart());
        settings.setAcademicYearEnd(formSettings.getAcademicYearEnd());
        settings.setSemester(formSettings.getSemester());
        settings.setRegistrationStartDate(formSettings.getRegistrationStartDate());
        settings.setRegistrationEndDate(formSettings.getRegistrationEndDate());
        settings.setSupportContactEmail(formSettings.getSupportContactEmail());
        
    
        // saves settings to database
        settingsService.saveSettings(settings);
        
        // success message 
        redirectAttributes.addFlashAttribute("success", "Settings updated successfully.");
        return "redirect:/admin/settings";
    }



}

