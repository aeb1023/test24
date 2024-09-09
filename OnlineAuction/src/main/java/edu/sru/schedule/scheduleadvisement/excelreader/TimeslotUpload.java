package edu.sru.schedule.scheduleadvisement.excelreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.sru.schedule.scheduleadvisement.domain.AdviseeData;
import edu.sru.schedule.scheduleadvisement.domain.Timeslot;
import edu.sru.schedule.scheduleadvisement.domain.user.User;
import edu.sru.schedule.scheduleadvisement.repositories.AdvisorRepository;
import edu.sru.schedule.scheduleadvisement.repositories.TimeslotRepository;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRepository;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRoleRepository;

/**
 * Controller for managing timeslot uploads and display within the application.
 */
@Controller
public class TimeslotUpload {

	@Autowired
	private TimeService timeService;
	private User user;
	private final List<String> uploadMessages = new ArrayList<String>();
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd");

	private final TimeslotRepository timeslotRepository;
	private final UserRoleRepository userRoleRepository;
	private final AdvisorRepository advisorRepository;

	@Autowired
	private UserRepository userRepository;

	public TimeslotUpload(TimeslotRepository timeslotRepository, UserRoleRepository userRoleRepository,
			AdvisorRepository advisorRepository) {
		this.timeslotRepository = timeslotRepository;
		this.userRoleRepository = userRoleRepository;
		this.advisorRepository = advisorRepository;
	}

	
	@RequestMapping({ "/timeslotUpload" })
	public String getUpload(Model model) {
		System.out.println("DEBUG1");
		model.addAttribute("uploadMessages", uploadMessages);
		return "timeslotUpload";
	}

	
	/**
     * Display the timeslot upload page.
     * 
     * @param model Spring MVC Model to pass data to the view
     * @return Name of the view "timeslotUpload"
     */
	@GetMapping("/timeslotUpload")
	public String getTimeslots(Model model, Principal principal) {
		String username = principal.getName();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String advisorEmail = authentication.getName();
		User advisor = userRepository.findByUsername(advisorEmail);
		Long advisorId = advisor.getId();

		// only the timeslots associated with the logged in advisor
		List<Timeslot> timeslots = timeslotRepository.findByAdvisorId(advisorId);
		model.addAttribute("timeslots", timeslots);

		return "timeslotUpload";
	}

	/**
     * Processes the uploaded Excel file containing timeslot data, parses it, and saves the data into the database.
     * 
     * @param file The uploaded Excel file containing timeslot data
     * @param redirectAttributes Used to add flash attributes for redirect 
     * @return a redirect string to the timeslot upload page
     */
	@PostMapping({ "/timeslotUpload" })
	public String uploadExcel(@RequestParam("excelFile") MultipartFile file, Model model,
			RedirectAttributes redirectAttributes) {
		// Format a date for the current upload messages
		String messageDate = String.format("[%s]", sdf.format(new Date()));
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		List<String> successMessages = new ArrayList<>();
//        List<String> errorMessages = new ArrayList<>();

		System.out.println("DEBUG: timeslot upload");
		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", messageDate + " Failed to upload file, none selected.");
			return "redirect:/timeslotUpload";
		}
		
		try {
			InputStream inputStream = file.getInputStream();
			Workbook workbook = new XSSFWorkbook(inputStream);
			Sheet sheet = workbook.getSheetAt(0);

			String tag = "";
			Cell cell = sheet.getRow(0).getCell(0);
			if (cell != null) {
				tag = cell.getStringCellValue();
				if (tag.equals("times")) {
					redirectAttributes.addFlashAttribute("uploadMessages", messageDate + " Successfully found times tag in " + file.getOriginalFilename());
				} else {
					redirectAttributes.addFlashAttribute("errorMessage", messageDate + " No times tag found in " + file.getOriginalFilename());
				}
			}
			File newFile = new File(
					System.getProperty("user.dir") + "/Program Documents/" + file.getOriginalFilename());
			file.transferTo(newFile);

			LinkedList<Timeslot> timeslotList = timeService.getTimeslotList();

			String advisorEmail = authentication.getName();
			User advisor = userRepository.findByUsername(advisorEmail);
			Long advisorId = advisor.getId();

			System.out.println("DEBUG: UPLOADED FILE " + file.getOriginalFilename());
			System.out.println("DEBUG: ADVISOR ID " + advisorId);
			
//			if(!timeService.doesTimeExist(advisorId)) {
				if(timeService.getById(advisorId) == null && timeService.getTimeslotList().isEmpty()) {
					for (Object o : readFile(newFile)) {
						Timeslot timeslot = (Timeslot) o;
						timeslot.setAdvisorId(advisorId);
						uploadMessages.add(timeslot.toString());
						timeService.addTimeslot(timeslot);
						timeslotRepository.save(timeslot);
						//success
						redirectAttributes.addFlashAttribute("successMessage", messageDate + " Timeslots uploaded successfully from " + file.getOriginalFilename());
					}
//				}
			}else {
				// error if timeslot already exist
				redirectAttributes.addFlashAttribute("errorMessage", "Timeslot already exists for " + advisorEmail);
				
			}
//			if (!errorMessages.isEmpty()) {
//                redirectAttributes.addFlashAttribute("uploadMessages", errorMessages);
//            }
//            if (!successMessages.isEmpty()) {
//                redirectAttributes.addFlashAttribute("successMessage", messageDate + " Timeslots uploaded successfully from " + file.getOriginalFilename());
//            }
			
			return "redirect:/timeslotUpload";
		} catch (

		IOException e) {
			System.out.println("DEBUG FINAL");
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessages", messageDate + " Failed to upload file: " + e.getCause().toString());
			return "redirect:/timeslotUpload";
		}
	}

	public List<?> readFile(String path) throws IOException {
		System.out.println("Reading file with Path: " + path);
		boolean timeSlot = path.contains("timeslots") ? true : false;
		List<?> dataList = timeSlot ? new ArrayList<Timeslot>() : new ArrayList<AdviseeData>();

		// Load the file
		FileInputStream thisxls;
		XSSFWorkbook wb;
		XSSFSheet sheet;
		XSSFRow curRow;
		thisxls = new FileInputStream(ResourceUtils.getFile(path));
		wb = new XSSFWorkbook(thisxls);
		sheet = wb.getSheetAt(0); //
		curRow = sheet.getRow(2);

		// Iterate through the excel entries & store
		while (curRow.getRowNum() <= sheet.getLastRowNum()) {
			if (timeSlot) {
				Date date = checkDateType(curRow.getCell(0)); 			// date
				String startTime = checkStringType(curRow.getCell(1));  // start time
				String endTime = checkStringType(curRow.getCell(2));	// end time
				int length = checkIntType(curRow.getCell(3));			// length
				Timeslot timeslot = new Timeslot(date, startTime, endTime, length);
				((List<Timeslot>) dataList).add(timeslot);
			}
			curRow = sheet.getRow(curRow.getRowNum() + 1);
			if (curRow == null) {
				break;
			}
		} //

		// Print our stored data
		for (Object field : dataList) {
			if (field instanceof Timeslot) {
				Timeslot timeslot = (Timeslot) field;
				System.out.println("Date: " + timeslot.getDate());
				System.out.println("Start Time: " + timeslot.getStartTime());
				System.out.println("End Time: " + timeslot.getEndTime());
				System.out.println("Timeslot Length: " + timeslot.getLength() + " minutes");
			}
		}

		// Close to prevent memory leaks
		thisxls.close();
		wb.close();
		return dataList;
	}

	/**
	 * Adds a new timeslot to the database after validating it does not already exist.
	 * 
	 * @param date the date of the new timeslot.
	 * @param startTime the start time of the new timeslot.
	 * @param endTime the end time of the new timeslot.
	 * @param length the duration of the timeslot.
	 * @param model the model to hold data for the view.
	 * @param redirectAttributes attributes for redirect
	 * @return redirect path to refresh the timeslot upload page.
	 */
	@PostMapping("/addTimeslot")
	public String addTimeslot(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date,
			@RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime,
			@RequestParam("length") int length, Model model,
			RedirectAttributes redirectAttributes) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		/*//start to accept type=time instead of type=text in html
		SimpleDateFormat timeIn = new SimpleDateFormat("HH:mm");
		SimpleDateFormat timeOut = new SimpleDateFormat("h:mma");
	
		try {
			Date timeInStart = timeIn.parse(startTime);
			Date timeInEnd = timeIn.parse(endTime);
			String formatStart = timeOut.format(timeInStart);
			String formatEnd = timeOut.format(timeInEnd);
		}catch (ParseException e){
			e.printStackTrace();
		}
		*/
		
		System.out.println("DEBUG - Date before parse: " + date);
		String dateString = sdf.format(date);
		Date parsedDate = date;
		try {
			String advisorEmail = authentication.getName();
			User advisor = userRepository.findByUsername(advisorEmail);
			Long advisorId = advisor.getId();

			boolean timeslotExists = timeslotRepository.existsByDateAndStartTimeAndEndTimeAndLengthAndAdvisorId(
					parsedDate, startTime, endTime, length, advisorId);
			if (!timeslotExists) {
				Timeslot newTimeslot = new Timeslot(parsedDate, startTime, endTime, length);
				newTimeslot.setAdvisorId(advisorId); // links it to the advisor

				timeService.addTimeslot(newTimeslot);
				timeslotRepository.save(newTimeslot);
				
				redirectAttributes.addFlashAttribute("message", "Added new Timeslot: " + newTimeslot);
				System.out.println("DEBUG: timeslot added successfully");
			} else {
				redirectAttributes.addFlashAttribute("message", "Timeslot already exists.");
				System.out.println("DEBUG: timeslot already exists");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/timeslotUpload";
	}

	
	/**
	 * Displays the form for editing an existing timeslot.
	 * 
	 * @param id the ID of the timeslot to edit.
	 * @param model the model to hold data for the view.
	 * @return the edit form view for timeslots.
	 */
	@GetMapping({ "/editTimeslots/{id}" })
	public String showEditForm(@PathVariable Long id, Model model) {
		try {
			Timeslot timeslot = timeslotRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("timeslot not found for id " + id));
			model.addAttribute("timeslot", timeslot);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "editTimeslots";
	}

	
	/**
	 * Updates the specified timeslot with new data provided by the user.
	 * 
	 * @param id the ID of the timeslot to update.
	 * @param updateTimeslot the updated timeslot data.
	 * @param redirectAttributes attributes for redirect scenarios.
	 * @return redirect path to refresh the timeslot upload page.
	 */
	@PostMapping("/updateTimeslot/{id}")
	public String updateTimeslot(@PathVariable Long id,
			//@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date, 
			Timeslot updateTimeslot,
			RedirectAttributes redirectAttributes) {

		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//String dateString = sdf.format(date);
//		try {
//			Date parsedDate = sdf.parse(dateString.substring(0, 10));
//			System.out.println("DEBUG: date" + parsedDate);
//		} catch (ParseException e) {
//			e.printStackTrace();}
		

		Timeslot timeslot = timeslotRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("timeslot not found for id " + id));
		try {
			System.out.println("DEBUG: trying edit of " + timeslot);
			//timeslot.setDate(updateTimeslot.getDate());
			timeslot.setStartTime(updateTimeslot.getStartTime());
			timeslot.setEndTime(updateTimeslot.getEndTime());
			timeslot.setLength(updateTimeslot.getLength());
			timeslotRepository.save(timeslot);
			System.out.println("DEBUG: update complete " + timeslot);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("DEBUG: could not update " + timeslot);
		}

		return "redirect:/timeslotUpload";
	}
	/*
	 * //older version of editTimeslot - being used to compare to new version
	 * 
	 * @PostMapping("/editTimeslot")
	 * 
	 * public String editTimeslot(@RequestParam("timeslotId") Long timeslotId,
	 * 
	 * @RequestParam("date") Date date,
	 * 
	 * @RequestParam("startTime") String startTime,
	 * 
	 * @RequestParam("endTime") String endTime,
	 * 
	 * @RequestParam("length") int length, Model model) { System.out.println("1");
	 * SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); String dateString
	 * = sdf.format(date); System.out.println("2");
	 * System.out.println("DEBUG EDIT OPTION");
	 * 
	 * System.out.println("Received timeslotId: " + timeslotId);
	 * System.out.println("Received date: " + date);
	 * System.out.println("Received startTime: " + startTime);
	 * System.out.println("Received endTime: " + endTime);
	 * System.out.println("Received length: " + length);
	 * 
	 * Timeslot timeslot = timeService.getById(timeslotId);
	 * 
	 * try { System.out.println("DEBUG TRY EDIT"); // System.out.println("3"); //
	 * Date parsedDate = sdf.parse(dateString.substring(0, 10)); //
	 * timeslot.setDate(parsedDate); // System.out.println("4"); //
	 * timeslot.setStartTime(startTime); // timeslot.setEndTime(endTime); //
	 * timeslot.setLength(length); timeService.updateTimeslot(timeslotId, date,
	 * startTime, endTime, length); timeslotRepository.save(timeslot); } catch
	 * (/*ParseException/ Exception e) { e.printStackTrace(); }
	 * 
	 * System.out.println("DEBUG EDIT"); return "redirect:/timeslotUpload"; }
	 */

	
	/**
	 * Updates the specified timeslot with new data provided by the user.
	 * 
	 * @param id the ID of the timeslot to update.
	 * @param updateTimeslot the updated timeslot data.
	 * @param redirectAttributes attributes for redirect
	 * @return redirect path to refresh the timeslot upload page.
	 */
	@PostMapping("/deleteTimeslot")
	public String deleteTimeslot(@RequestParam("timeslotId") Long timeslotId, Model model) {
		timeService.deleteTimeslotById(timeslotId);
		timeslotRepository.deleteById(timeslotId);
		return "redirect:/timeslotUpload";
	}

	public Date checkDateType(XSSFCell testCell) {
		return testCell.getDateCellValue();
	}

	public String checkStringType(XSSFCell testCell) {
		if (testCell.getCellType().equals(CellType.NUMERIC)) {
			return Integer.toString((int) testCell.getNumericCellValue());
		}
		return testCell.getStringCellValue();
	}

	public int checkIntType(XSSFCell testCell) {
		if (testCell.getCellType().equals(CellType.STRING)) {
			return Integer.parseInt(testCell.getStringCellValue());
		}
		return (int) testCell.getNumericCellValue();
	}

	public List<?> readFile(File file) throws IOException {
		return readFile(file.getCanonicalPath());
	}
}