package edu.sru.schedule.scheduleadvisement.excelreader;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

import edu.sru.schedule.scheduleadvisement.domain.AdviseeData;
import edu.sru.schedule.scheduleadvisement.domain.user.User;
import edu.sru.schedule.scheduleadvisement.domain.user.UserRole;
import edu.sru.schedule.scheduleadvisement.repositories.AdviseeRepository;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRepository;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRoleRepository;

/**
 * Controller for managing the upload of advisee data via Excel files.
 * This includes adding, editing, and deleting advisee records.
 */
@Controller
public class AdviseeUpload {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	private final AdviseeRepository adviseeRepository;
	private final UserRoleRepository userRoleRepository;
	private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
	private final List<String> uploadMessages = new ArrayList<>();

	public AdviseeUpload(AdviseeRepository adviseeRepository, UserRoleRepository userRoleRepository) {
		this.adviseeRepository = adviseeRepository;
		this.userRoleRepository = userRoleRepository;
	}

	/**
     * Displays the upload form for advisees, allowing advisors to upload an Excel file
     * containing advisee data.
     *
     * @param model Model object to pass data to the view
     * @return Name of the view "adviseeUpload"
     */
	@GetMapping("/adviseeUpload")
	public String showUploadForm(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String advisorEmail = authentication.getName(); // get advisor's email (username)

		// Fetch the advisor user by email to get their ID
		User advisor = userRepository.findByUsername(advisorEmail);
		if (advisor == null) {

			model.addAttribute("errorMessage", "Advisor not found. Please ensure the advisor is correctly logged in.");
			return "adviseeUpload";
		}
		Long advisorId = advisor.getId(); // get advisor's ID

		// only the advisees associated with the logged in advisor
		List<AdviseeData> adviseesOfCurrentAdvisor = adviseeRepository.findByAdvisorId(advisorId);
		model.addAttribute("allAdvisees", adviseesOfCurrentAdvisor);
		return "adviseeUpload";
	}

	/**
     * processes request to upload advisee data from an Excel file.
     *
     * @param file Excel file containing advisee data
     * @param redirectAttributes RedirectAttributes to add flash messages for the user
     * @return Redirect view name "adviseeUpload"
     */
	@PostMapping("/adviseeUpload")
	public String uploadAdvisees(@RequestParam("excelFile") MultipartFile file, RedirectAttributes redirectAttributes) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String advisorEmail = authentication.getName();
		User advisor = userRepository.findByUsername(advisorEmail);

		if (advisor == null) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"Advisor not found. Please check the advisor's email and try again.");
			return "redirect:/adviseeUpload";
		}
		Long advisorId = advisor.getId();

		String messageDate = String.format("[%s]", sdf.format(new Date()));
		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", messageDate + " Please select a file to upload.");
			return "redirect:/adviseeUpload";
		} else {
			try {
				List<AdviseeData> advisees = readAdviseesFromFile(file, advisorId);
				if (advisees == null) {
					redirectAttributes.addFlashAttribute("errorMessage", "The file does not contain advisee logins.");
					return "redirect:/adviseeUpload";
				}

				UserRole userRole = userRoleRepository.findByAuthorityName("ROLE_USER");
				if (userRole == null) {
					redirectAttributes.addFlashAttribute("errorMessage",
							"User role ROLE_USER not found in the database.");
					return "redirect:/adviseeUpload";
				}

				List<String> errorMessages = new ArrayList<>();

				for (AdviseeData advisee : advisees) {
					User existingUser = userRepository.findByUsername(advisee.getEmail());
					if (existingUser == null) {
						User newUser = new User();
						newUser.setFirstName(advisee.getFirstName());
						newUser.setLastName(advisee.getLastName());
						newUser.setUsername(advisee.getEmail());
						newUser.setEmail(advisee.getEmail());
						newUser.setPhoneNumber(advisee.getPhoneNumber());
						newUser.setPassword(passwordEncoder.encode(advisee.getPassword()));
						newUser.setUserRole(userRole);
						userRepository.save(newUser);
						advisee.setAdvisorId(advisorId);
						adviseeRepository.save(advisee);
					} else {
						errorMessages.add("Advisee with email " + advisee.getEmail() + " already exists.");
					}
				}

				if (!errorMessages.isEmpty()) {
					redirectAttributes.addFlashAttribute("uploadMessages", errorMessages);
				} else {
					redirectAttributes.addFlashAttribute("successMessage",
							messageDate + " Advisees uploaded successfully.");
				}
			} catch (IOException e) {
				redirectAttributes.addFlashAttribute("errorMessage",
						messageDate + " Failed to upload file: " + e.getMessage());
			}
		}
		return "redirect:/adviseeUpload";
	}

	/**
     * Displays the form to manually add a single advisee.
     *
     * @param model Model object to pass data to the view
     * @return Name of the view "addAdvisee"
     */
	@GetMapping("/addAdvisee")
	public String showAddAdviseeForm(Model model) {
		model.addAttribute("advisee", new AdviseeData());
		return "addAdvisee";
	}

	/**
     * Processes the form submission for adding a new advisee.
     *
     * @param adviseeData Advisee data from the form
     * @param redirectAttributes RedirectAttributes to add flash messages for the user
     * @return Redirect view name
     */
	@PostMapping("/addAdvisee")
	public String addAdvisee(@ModelAttribute AdviseeData adviseeData, RedirectAttributes redirectAttributes) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String advisorEmail = authentication.getName();

		User advisor = userRepository.findByUsername(advisorEmail);
		if (advisor == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "Advisor not found. Please ensure you are logged in.");
			return "redirect:/addAdvisee";
		}
		Long advisorId = advisor.getId();
		adviseeData.setAdvisorId(advisorId);

		// check if an advisee with the same email and advisor ID already exists
		AdviseeData existingAdvisee = adviseeRepository.findByEmailAndAdvisorId(adviseeData.getEmail(), advisorId);
		if (existingAdvisee == null) {
			adviseeRepository.save(adviseeData);

			// create a user for the advisee if they do not already exist
			User user = userRepository.findByUsername(adviseeData.getEmail());
			if (user == null) {
				user = new User();
				user.setFirstName(adviseeData.getFirstName());
				user.setLastName(adviseeData.getLastName());
				user.setUsername(adviseeData.getEmail());
				user.setEmail(adviseeData.getEmail());
				user.setPhoneNumber(adviseeData.getPhoneNumber());
				user.setPassword(passwordEncoder.encode(adviseeData.getPassword()));
				UserRole userRole = userRoleRepository.findByAuthorityName("ROLE_USER");
				user.setUserRole(userRole);
				userRepository.save(user);
			}
		} else {
			redirectAttributes.addFlashAttribute("errorMessage",
					"Advisee with this email already assigned to the advisor.");
		}

		return "redirect:/adviseeUpload";
	}

	/**
     * Displays the form for editing an existing advisee's details.
     *
     * @param id The ID of the advisee to edit
     * @param model Model object to pass data to the view
     * @return Name of the view "editAdvisees"
     */
	@GetMapping("/editAdvisee/{id}")
	public String showEditForm(@PathVariable Long id, Model model) {
		AdviseeData advisee = adviseeRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Advisee not found for id " + id));
		model.addAttribute("advisee", advisee);
		return "editAdvisees";
	}

	 /**
     * Processes the form submission for updating an advisee's details.
     *
     * @param id The ID of the advisee to update
     * @param updatedAdvisee Updated data for the advisee
     * @param redirectAttributes RedirectAttributes to add flash messages for the user
     * @return Redirect view name
     */
	@PostMapping("/updateAdvisee/{id}")
	public String updateAdvisee(@PathVariable Long id, AdviseeData updatedAdvisee,
			RedirectAttributes redirectAttributes) {
		AdviseeData advisee = adviseeRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Advisee not found for id " + id));
		advisee.setFirstName(updatedAdvisee.getFirstName());
		advisee.setLastName(updatedAdvisee.getLastName());
		advisee.setEmail(updatedAdvisee.getEmail());
		advisee.setPhoneNumber(updatedAdvisee.getPhoneNumber());
		advisee.setPassword(updatedAdvisee.getPassword());
		adviseeRepository.save(advisee);

		redirectAttributes.addFlashAttribute("message", "Advisee updated successfully");
		return "redirect:/adviseeUpload";
	}

	/**
     * Handles the deletion of an advisee.
     *
     * @param id The ID of the advisee to delete
     * @param redirectAttributes RedirectAttributes to add flash messages for the user
     * @return Redirect view name
     */
	@GetMapping("/deleteAdvisee/{id}")
	public String deleteAdvisee(@PathVariable Long id, RedirectAttributes redirectAttributes, Model model) {
		AdviseeData advisee = adviseeRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Advisor not found for id " + id));

		adviseeRepository.deleteById(id);

		redirectAttributes.addFlashAttribute("message", "Advisee deleted successfully");
		return "redirect:/adviseeUpload";
	}

    
	/**
     * Reads advisee data from an Excel file.
     *
     * @param file Excel file containing advisee data
     * @param advisorId Advisor's ID to link advisees
     * @return List of AdviseeData objects extracted from the Excel file
     * @throws IOException If there is an error reading the file
     */
	private List<AdviseeData> readAdviseesFromFile(MultipartFile file, Long advisorId) throws IOException {
		List<AdviseeData> advisees = new ArrayList<>();
		Workbook workbook = WorkbookFactory.create(file.getInputStream());
		Sheet sheet = workbook.getSheetAt(0);
       //checks for the correct tag 
		Row firstRow = sheet.getRow(0);
		if (firstRow != null && firstRow.getCell(0) != null) {
			String tag = firstRow.getCell(0).getStringCellValue();
			if (!"advisees".equalsIgnoreCase(tag.trim())) {
				workbook.close();
				return null;
			}
		} else {
			workbook.close();
			return null; // indicate a formatting error
		}

		Iterator<Row> rowIterator = sheet.rowIterator();

		// Skip the header row
		if (rowIterator.hasNext()) {
			rowIterator.next(); // Skip the title row
		}

		if (rowIterator.hasNext()) {
			rowIterator.next(); // Skip the header row
		}

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();

			AdviseeData advisee = new AdviseeData(row.getCell(0).getStringCellValue(),
					row.getCell(1).getStringCellValue(), row.getCell(2).getStringCellValue(),
					row.getCell(3).getStringCellValue(), row.getCell(4).getStringCellValue());
			advisees.add(advisee);
			advisee.setAdvisorId(advisorId); // set advisor ID to each advisee.
		}
		workbook.close();
		return advisees;
	}

}
