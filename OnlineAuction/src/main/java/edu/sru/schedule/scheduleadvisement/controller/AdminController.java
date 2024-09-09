package edu.sru.schedule.scheduleadvisement.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.sru.schedule.scheduleadvisement.domain.AdvisorData;
import edu.sru.schedule.scheduleadvisement.domain.user.User;
import edu.sru.schedule.scheduleadvisement.domain.user.UserRole;
import edu.sru.schedule.scheduleadvisement.repositories.AdvisorRepository;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRepository;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRoleRepository;

/**
 * AdminController is responsible for handling functions such as managing user accounts
 * and advisor information within the system. This includes viewing, adding, editing,
 * and deleting user and advisor details.
 */
@Controller
public class AdminController {
 
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	private final AdvisorRepository advisorRepository;
	private final UserRoleRepository userRoleRepository;
	private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
	private final List<String> uploadMessages = new ArrayList<>();

	public AdminController(AdvisorRepository advisorRepository, UserRoleRepository userRoleRepository) {
		this.advisorRepository = advisorRepository;
		this.userRoleRepository = userRoleRepository;
	}
	
	
	/**
     * Displays a view with a list of all users.
     *
     * @param model the model object that will be populated with user data
     * @return the name of the view "viewUsers to display user information
     */
	@GetMapping("/admin/view-users")
	public String viewUsers(Model model) {
	    List<User> users = (List<User>) userRepository.findAll();
	    model.addAttribute("users", users);
	    return "viewUsers"; 
	}

	/**
     * Displays the system settings view.
     *
     * @param model the model object
     * @return the name of the view "systemSettings" for system settings
     */
	@GetMapping("/systemSettings")
	public String showSystemSettings(Model model) {
		return "systemSettings"; 
	}

	/**
     * Displays form for uploading advisor information via an Excel file.
     *
     * @param model the model object that will be populated with advisor data
     * @return the name of the view "uploadAdvisors" for uploading advisor data
     */
	@GetMapping("/admin/upload-advisors")
	public String showUploadForm(Model model) {
		List<AdvisorData> allAdvisors = (List<AdvisorData>) advisorRepository.findAll();
		model.addAttribute("allAdvisors", allAdvisors);
		return "uploadAdvisors"; // displays upload advisors page
	}

	/**
     * Processes the Excel file uploaded by the admin to add or update advisor information in the database.
     * Validates the file content and handles the creation of new advisors.
     *
     * @param file the uploaded Excel file containing advisor data
     * @param redirectAttributes attributes for passing messages or data while redirecting
     * @return redirect path to the upload form view
     */
	@PostMapping("/admin/upload-advisors")
	public String uploadAdvisors(@RequestParam("excelFile") MultipartFile file, RedirectAttributes redirectAttributes) {
	    String messageDate = String.format("[%s]", sdf.format(new Date()));
	    if (file.isEmpty()) {
	        redirectAttributes.addFlashAttribute("errorMessage", messageDate + " Please select a file to upload.");
	        return "redirect:/admin/upload-advisors";
	    } else {
	        try {
	            List<AdvisorData> advisors = readAdvisorsFromFile(file);
	            if (advisors == null) {
	                redirectAttributes.addFlashAttribute("errorMessage", "The file does not contain advisor logins.");
	                return "redirect:/admin/upload-advisors";
	            }

	            UserRole facultyRole = userRoleRepository.findByAuthorityName("ROLE_FACULTY");
	            if (facultyRole == null) {
	                redirectAttributes.addFlashAttribute("errorMessage", "ROLE_FACULTY not found in the database.");
	                return "redirect:/admin/upload-advisors";
	            }

	            List<String> errorMessages = new ArrayList<>();
	            for (AdvisorData advisor : advisors) {
	                User existingUser = userRepository.findByUsername(advisor.getEmail());
	                if (existingUser == null) {
	                    User newUser = new User();
	                    newUser.setFirstName(advisor.getFirstName());
	                    newUser.setLastName(advisor.getLastName());
	                    newUser.setUsername(advisor.getEmail());
	                    newUser.setEmail(advisor.getEmail());
	                    newUser.setPhoneNumber(advisor.getPhoneNumber());
	                    newUser.setPassword(passwordEncoder.encode(advisor.getPassword()));
	                    newUser.setUserRole(facultyRole);
	                    userRepository.save(newUser);
	                    advisorRepository.save(advisor);
	                } else {
	                    errorMessages.add("Advisor with email " + advisor.getEmail() + " already exists.");
	                }
	            }

	            if (!errorMessages.isEmpty()) {
	                redirectAttributes.addFlashAttribute("uploadMessages", errorMessages);
	            } else {
	                redirectAttributes.addFlashAttribute("successMessage", messageDate + " Advisors uploaded successfully.");
	            }
	        } catch (IOException e) {
	            redirectAttributes.addFlashAttribute("errorMessage", messageDate + " Failed to upload file: " + e.getMessage());
	        }
	    }
	    return "redirect:/admin/upload-advisors";
	}


	/**
     * Displays the form for manually adding an advisor.
     *
     * @param model the model object that will be populated with an empty advisor data object
     * @return the name of the view "addAdvisor" for adding an advisor
     */
	@GetMapping("/admin/add-advisor")
	public String showAddAdvisorForm(Model model) {
		model.addAttribute("advisor", new AdvisorData());
		return "addAdvisor"; 
	}


	
	/**
     * Handles adding a new advisor to the database, including the creation of a corresponding user account.
     *
     * @param advisorData the advisor data to be added
     * @param redirectAttributes attributes for passing messages or data while redirecting
     * @return redirect path to the advisor upload form view
     */
	@PostMapping("/admin/add-advisor")
	public String addAdvisor(@ModelAttribute AdvisorData advisorData, RedirectAttributes redirectAttributes) {
		UserRole facultyRole = userRoleRepository.findByAuthorityName("ROLE_FACULTY");
		if (facultyRole == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "ROLE_FACULTY not found in the database.");
			return "redirect:/admin/upload-advisors";
		}

		// save to advisor data table
		if (advisorRepository.existsByEmail(advisorData.getEmail()))
		{
			redirectAttributes.addFlashAttribute("errorMessage", "Please enter an unused email address");
		}
		else
		{
		advisorRepository.save(advisorData);
		}

		// Create new user
		User user = userRepository.findByUsername(advisorData.getEmail());
		if (user == null) {
			user = new User(); // If no user found create a new one
		}
		user.setFirstName(advisorData.getFirstName());
		user.setLastName(advisorData.getLastName());
		user.setUsername(advisorData.getEmail()); // Username is set to the email
		user.setEmail(advisorData.getEmail());
		user.setPhoneNumber(advisorData.getPhoneNumber());
		user.setPassword(passwordEncoder.encode(advisorData.getPassword())); // encrypts the password
		user.setUserRole(facultyRole); // Assign ROLE_FACULTY to the new advisor
		userRepository.save(user); // save to user table

		redirectAttributes.addFlashAttribute("message", "Advisor successfully added");
		return "redirect:/admin/upload-advisors";
	}

	/**
     * Displays the form for editing existing advisor details.
     *
     * @param id the ID of the advisor to be edited
     * @param model the model object that will be populated with advisor data
     * @return the name of the view "editAdvisors" for editing an advisor
     */
	@GetMapping("/admin/edit-advisor/{id}")
	public String showEditForm(@PathVariable Long id, Model model) {
		AdvisorData advisor = advisorRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Advisor not found for id " + id));
		model.addAttribute("advisor", advisor);
		return "editAdvisors"; 
	}
	
	/**
     * Processes the updated data submitted for editing an advisor, applying changes to both the advisor and user tables.
     *
     * @param id the ID of the advisor whose details are being updated
     * @param updatedAdvisor the updated advisor data
     * @param redirectAttributes attributes for passing messages or data while redirecting
     * @return redirect path to the advisor upload form view
     */
	@PostMapping("/admin/update-advisor/{id}")
	public String updateAdvisor(@PathVariable Long id, AdvisorData updatedAdvisor,
			RedirectAttributes redirectAttributes) {
		AdvisorData advisor = advisorRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Advisor not found for id " + id));
		advisor.setFirstName(updatedAdvisor.getFirstName());
		advisor.setLastName(updatedAdvisor.getLastName());
		advisor.setEmail(updatedAdvisor.getEmail());
		advisor.setPhoneNumber(updatedAdvisor.getPhoneNumber());
		advisorRepository.save(advisor); // save to advisor data table

		// Update User
		User user = userRepository.findByUsername(advisor.getEmail());
		if (user == null) {
			throw new RuntimeException("User not found for email " + advisor.getEmail());
		}

		user.setFirstName(advisor.getFirstName());
		user.setLastName(advisor.getLastName());
		user.setEmail(advisor.getEmail());
		user.setPhoneNumber(advisor.getPhoneNumber());
		userRepository.save(user); // save to user table

		redirectAttributes.addFlashAttribute("message", "Advisor updated successfully");
		return "redirect:/admin/upload-advisors";
	}
	
	/**
     * Deletes an advisor and their associated user data.
     *
     * @param id the ID of the advisor to be deleted
     * @param redirectAttributes attributes for passing messages or data while redirecting
     * @return redirect path to the advisor upload form view
     */
	@GetMapping("/admin/delete-advisor/{id}") 
	public String deleteAdvisor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		AdvisorData advisor = advisorRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Advisor not found for id " + id));

		User user = userRepository.findByUsername(advisor.getEmail());
		if (user == null) {
			throw new RuntimeException("User not found for email " + advisor.getEmail());
		}

		userRepository.delete(user); // deletes from user table
		advisorRepository.deleteById(id); //delete from advisor table 
		
		redirectAttributes.addFlashAttribute("message", "Advisor deleted successfully");
		return "redirect:/admin/upload-advisors";
	}

	
	/**
     * Extracts advisor data from an Excel file.
     * Checks for the presence of specific tags and formats expected in the Excel file.
     *
     * @param file the Excel file to be processed
     * @return a list of AdvisorData objects extracted from the file
     * @throws IOException if there is an error processing the file
     */
	private List<AdvisorData> readAdvisorsFromFile(MultipartFile file) throws IOException {
	    List<AdvisorData> advisors = new ArrayList<>();
	    Workbook workbook = WorkbookFactory.create(file.getInputStream());
	    Sheet sheet = workbook.getSheetAt(0);
	    
	    // checks for the correct tag
	    Row tagRow = sheet.getRow(0);
	    if (tagRow != null && tagRow.getCell(0) != null) {
	        String tag = tagRow.getCell(0).getStringCellValue();
	        if (!"advisors".equalsIgnoreCase(tag.trim())) {
	            workbook.close();
	            return null; // File does not contain the correct data
	        }
	    }

	   
	    int headerIndex = 1; 
	    Iterator<Row> rowIterator = sheet.rowIterator();
	    while (rowIterator.hasNext() && rowIterator.next().getRowNum() < headerIndex) {
	        
	    }

	    // process data
	    while (rowIterator.hasNext()) {
	        Row row = rowIterator.next();
	        
	        
	        if (row.getRowNum() > headerIndex) {
	            AdvisorData advisor = new AdvisorData(
	                row.getCell(0).getStringCellValue(), // First Name
	                row.getCell(1).getStringCellValue(), // Last Name
	                row.getCell(2).getStringCellValue(), // Email
	                row.getCell(3).getStringCellValue(), // Phone Number
	                row.getCell(4).getStringCellValue()  // Password
	            );
	            advisors.add(advisor);
	        }
	    }
	    workbook.close();
	    return advisors;
	}

}

