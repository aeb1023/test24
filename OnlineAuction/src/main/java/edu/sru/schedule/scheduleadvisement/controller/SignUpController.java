package edu.sru.schedule.scheduleadvisement.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.web.exchanges.HttpExchange.Principal;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import edu.sru.schedule.scheduleadvisement.domain.user.User;
import edu.sru.schedule.scheduleadvisement.domain.user.UserRole;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRepository;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRoleRepository;
import edu.sru.schedule.scheduleadvisement.util.PreLoad;

/**
 * SignUpController responsible for handling new users within the application.
 * currently not in use, to re-enable this feature take comments out from index.html
 * to display the signup button.
 */

@Controller
// @RequestMapping(value = "/user")
public class SignUpController {
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserRoleRepository userRoleRepository;

	private UserController userController;
	private UserRepository userRepository;
	private EmailController email;
	private UtilityController util;

	private static final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
	private boolean showedSuccess = false;
	User findUser = new User();
	private String answer;
	private String question;
	private int theId;
	private String page;

	public SignUpController(UserController userController, EmailController email, UtilityController util,
			UserRepository userRepository) {
		this.userController = userController;
		this.email = email;
		this.util = util;
		this.userRepository = userRepository;
	}

	Map<String, String> countryCodesMap;

	@ModelAttribute
	public void preLoad(Model model) {

		countryCodesMap = new LinkedHashMap<>();
		Set<String> regions = phoneNumberUtil.getSupportedRegions();
		countryCodesMap = regions.stream()
				.sorted(Comparator.comparing(region -> new Locale("", region).getDisplayCountry()))
				.collect(Collectors.toMap(region -> region, region -> String.format("+%s %s",
						phoneNumberUtil.getCountryCodeForRegion(region), new Locale("", region).getDisplayCountry()),
						(x, y) -> y, LinkedHashMap::new));
	}

	List<String> secretQuestion1;

	@ModelAttribute
	public void preLoad2(Model model) {
		secretQuestion1 = new ArrayList<>();
		secretQuestion1.add("What is your mother's maiden name?");
		secretQuestion1.add("What is the name of your first pet?");
		secretQuestion1.add("What is the name of the street you grew up on?");
		secretQuestion1.add("What is the make of the first car you owned?");
		secretQuestion1.add("What city were you born in?");
	}

	List<String> secretQuestion2;

	@ModelAttribute
	public void preLoad3(Model model) {
		secretQuestion2 = new ArrayList<>();
		secretQuestion2.add("What is your favorite band?");
		secretQuestion2.add("What is your father's middle name?");
		secretQuestion2.add("What is your favorite school subject?");
		secretQuestion2.add("What was the name of your first boyfriend/girlfriend?");
		secretQuestion2.add("Where is somewhere you've always wanted to travel to?");
	}

	List<String> secretQuestion3;

	@ModelAttribute
	public void preLoad4(Model model) {
		secretQuestion3 = new ArrayList<>();
		secretQuestion3.add("What is your favorite beverage?");
		secretQuestion3.add("What is your favorite video game?");
		secretQuestion3.add("What is your favorite tv show?");
		secretQuestion3.add("What is your favorite outdoor activity?");
		secretQuestion3.add("What was your highschool mascot?");
	}

	/**
     * Displays the initial form for user sign-up including input fields for all
     * user data and security questions.
     *
     * @param model Model to pass attributes to the view
     * @return the sign-up view name "newUser"
     */
	@RequestMapping({ "/newUser" })
	public String newUser(Model model) {
		if (getPage() == null) {
			setPage("signup");
			setShowedSuccess(false);
		}
		if (getPage() != null && !(getPage().contains("signup")) && !(getPage().contains("success"))) {
			setPage("signup");
			setShowedSuccess(false);
		}
		if (isShowedSuccess()) {
			setPage("signup");
			setShowedSuccess(false);
		}
		if (getPage().contains("success")) {
			setShowedSuccess(true);
		}

		String defaultCountryCode = PreLoad.getDefaultCountryCode();

		User user = new User();
		userController.getCaptcha(user);
		model.addAttribute("page", getPage());
		model.addAttribute("user", user);
		model.addAttribute("secretQuestion1", secretQuestion1);
		model.addAttribute("secretQuestion2", secretQuestion2);
		model.addAttribute("secretQuestion3", secretQuestion3);
		model.addAttribute("countryCodes", countryCodesMap);
		model.addAttribute("users", userController.getAllUsers());
		model.addAttribute("defaultCountryCode", defaultCountryCode);
		return "newUser";
	}

	/**
	 * creates a new user when someone signs up called by newUser.html
	 * 
	 * @param user
	 * @param result
	 * @param file
	 * @param attributes
	 * @param model
	 * @return
	 */

	@PostMapping("/add-user-signup")
	public String addUser(@Valid User user, BindingResult result, @RequestParam("imageName") MultipartFile file,
			RedirectAttributes attributes, Model model) {
		User usertemp = user;
		String countryCode = user.getCountryCode();
		String phoneNumberString = user.getPhoneNumber();
		PhoneNumber phoneNumber = new PhoneNumber();
		try {
			phoneNumber.setCountryCode(phoneNumberUtil.getCountryCodeForRegion(countryCode))
					.setNationalNumber(Long.parseLong(phoneNumberString));
			if (!phoneNumberUtil.isValidNumber(phoneNumber)) {
				result.addError(new FieldError("phoneNumber", "phoneNumber",
						String.format("Please enter valid phone number. Example format for the selected country - %s",
								phoneNumberUtil.getExampleNumberForType(countryCode, PhoneNumberType.MOBILE)
										.getNationalNumber())));
			}
		} catch (NumberFormatException e) {
			result.addError(new FieldError("phoneNumber", "phoneNumber", String.format(
					"Please enter valid phone number. Example format for the selected country - %s",
					phoneNumberUtil.getExampleNumberForType(countryCode, PhoneNumberType.MOBILE).getNationalNumber())));
		}
		if (!EmailValidator.getInstance().isValid(user.getEmail())) {
			result.addError(new FieldError("email", "email", "Invalid email format. Example - username@domain.com"));
		}
		if (userController.getUserByUsername(usertemp.getUsername()) != null) {
			result.addError(new FieldError("username", "username", "Username is already taken."));
		}
		if (userController.getUserByEmail(usertemp.getEmail()) != null) {
			result.addError(new FieldError("email", "email", "An account is already associated with this email."));
		}

		if (!(usertemp.getPasswordconf().equals(usertemp.getPassword()))) {
			result.addError(new FieldError("passwordconf", "passwordconf", "Passwords don't match."));
		}

		String defaultCountryCode = PreLoad.getDefaultCountryCode();

		if (result.hasErrors()) {
			model.addAttribute("user", user);
			model.addAttribute("countryCodes", countryCodesMap);
			model.addAttribute("defaultCountryCode", defaultCountryCode);
			model.addAttribute("secretQuestion1", secretQuestion1);
			model.addAttribute("secretQuestion2", secretQuestion2);
			model.addAttribute("secretQuestion3", secretQuestion3);
			userController.getCaptcha(user);
			model.addAttribute("page", getPage());
			return "newUser";
		}
		/* captcha */
		if (user.getCaptcha().equals(user.getHiddenCaptcha())) { // from original login code
			// Removed condition where if a user is registered with the username "admin" they are given admin role.
			UserRole userRole = userRoleRepository.findByAuthorityName("ROLE_USER");
	        if (userRole != null) {
	            user.setUserRole(userRole);
	        } else {
	           
	            System.out.println("ROLE_USER role not found.");
	            return "errorPage";
	        }
			user.setEnabled(true);

			model.addAttribute("message", "User Registered successfully!");
			setPage("success");
			// userController.setCurrently_Logged_In(user);
			model.addAttribute("page", getPage());
			if (!"ROLE_ADMIN".equals(user.getUserRole().getAuthority())) {
			    email.verificationEmail(user, util.randomStringGenerator());
			}

			userController.addUser(user, result);

			user = userController.getUserByEmail(user.getEmail());

			System.out.println(user.getId() + " user Id");

			/* profile picture */
			System.out.println(file.isEmpty() + " is the file empty");
			if (!file.isEmpty()) {
				String tempImageName;
				tempImageName = user.getId() + StringUtils.cleanPath(file.getOriginalFilename());
				System.out.println(file.getOriginalFilename());
				System.out.println(tempImageName);
				user.setUserImage(tempImageName);
				try {
					String fileLocationTemp = new ClassPathResource("static/images/userImages").getFile()
							.getAbsolutePath() + "/" + tempImageName;

					try (FileOutputStream output = new FileOutputStream(fileLocationTemp)) {
						output.write(file.getBytes());
					}

					System.out.println("Upload successful, file saved at: " + fileLocationTemp);
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Upload failed");
				}
				model.addAttribute("userImage", tempImageName);
			}

			userRepository.save(user);
			model.addAttribute("user", user);

			System.out.println("line 341 " + user);
			User userLogin = userController.getUserByUsername(user.getUsername());
			System.out.println("line 343 " + userLogin);
			// userController.setCurrently_Logged_In(userLogin);
			// System.out.println(userController.getCurrently_Logged_In());

			return "redirect:/emailVerification";
		} else {
			result.addError(new FieldError("captcha", "captcha", "Incorrect Captcha."));
			userController.getCaptcha(user);

			model.addAttribute("secretQuestion1", secretQuestion1);
			model.addAttribute("secretQuestion2", secretQuestion2);
			model.addAttribute("secretQuestion3", secretQuestion3);
			model.addAttribute("user", user);
			model.addAttribute("page", getPage());
			model.addAttribute("countryCodes", countryCodesMap);
			model.addAttribute("defaultCountryCode", defaultCountryCode);

			return "newUser";
		}
	}

	@GetMapping({ "/forgotUser/{id}" })
	public String forgotUser(@PathVariable("id") int id, Model model) {
		setPage("email");
		model.addAttribute("page", page);
		if (id == 0) {
			setTheId(0);
			setPage("user");
		} else {
			setTheId(1);
			setPage("pass");
		}
		return "forgotUser";
	}

	@PostMapping({ "/findUser" })
	public String findUser(Model model, @RequestParam("email") String email2) {
		if (userController.getUserByEmail(email2) == null) {
			setPage("findUserFail");
			model.addAttribute("page", page);
			return "forgotUser";
		}
		findUser = userController.getUserByEmail(email2);

		// Mask the email and add it to the model
		String maskedEmail = maskEmail(email2);
		model.addAttribute("maskedEmail", maskedEmail);

		if (getTheId() == 0) {
			email.usernameRecovery(findUser);
		}
		Random rand = new Random();
		int rand_int1 = rand.nextInt(3);

		if (rand_int1 == 0) {
			setQuestion(findUser.getSecret1());
			setAnswer(findUser.getUserSecret1());
		}
		if (rand_int1 == 1) {
			setQuestion(findUser.getSecret2());
			setAnswer(findUser.getUserSecret2());
		} else {
			setQuestion(findUser.getSecret3());
			setAnswer(findUser.getUserSecret3());
		}
		model.addAttribute("question", question);
		model.addAttribute("page", page);
		return "forgotUser";
	}

	private String maskEmail(String email) {
		int index = email.indexOf('@');
		if (index <= 3)
			return "****"; // if the email has 3 or fewer characters before '@', return "****"

		String firstPart = email.substring(0, 1);
		String middlePart = email.substring(1, index - 2);
		String lastPart = email.substring(index - 2);

		StringBuilder maskedMiddle = new StringBuilder();
		for (int i = 0; i < middlePart.length(); i++) {
			maskedMiddle.append('*');
		}

		return firstPart + maskedMiddle.toString() + lastPart;
	}

	@PostMapping({ "/answerQuestion" })
	public String answerQuestion(@RequestParam("answer") String answer2, Model model) {
		if (answer2.equals(getAnswer())) {
			setPage("reset");
			model.addAttribute("page", page);
		} else {
			setPage("wrong");
			model.addAttribute("question", getQuestion());
			model.addAttribute("page", page);
		}

		return "forgotUser";
	}
	
	// reset password from original login code
	@PostMapping("/resetPassword")
	public String resetPassword(@RequestParam("pass") String pass, @RequestParam("pass2") String pass2, Model model) {
		if (pass.length() < 6 || !(pass.equals(pass2))) {
			setPage("resetfail");
			model.addAttribute("page", page);
			return "forgotUser";
		} else {

			findUser.setPassword(passwordEncoder.encode(pass));
			userRepository.save(findUser);
			setPage("resetSuccess");
			model.addAttribute("page", page);
			return "forgotUser";
		}
	}

	@RequestMapping({ "/emailverification" })
	public String showverify(Model model) {

		setPage("startverify");

		model.addAttribute("page", getPage());
		model.addAttribute("users", userRepository.findAll());
		return "emailverification";
	}

	@RequestMapping("/verify")
	public String verifyUser(@RequestParam("username") String name, @RequestParam("verification") String verification,
			Model model) {
		if (userController.getUserByUsername(name) == null) {
			setPage("wronguser");
			model.addAttribute("username", name);
			model.addAttribute("verification", verification);
			model.addAttribute("page", getPage());
			model.addAttribute("message", "Wrong Username!");
			return "emailverification";
		}

		User user = userController.getUserByUsername(name);
		if (user.getEnabled()) {
			setPage("alreadyverified");
			model.addAttribute("page", getPage());
			model.addAttribute("message", "User already Verified!");
			return "emailverification";
		}
		if (verification.equals(user.getEmailVerification())) {
			setPage("verifysuccess");
			model.addAttribute("page", getPage());
			model.addAttribute("message", "User Verified successfully!");
			user.setEnabled(true);
			userRepository.save(user);
			return "emailverification";
		} else {
			setPage("wrongcode");
			model.addAttribute("username", name);
			model.addAttribute("verification", verification);
			model.addAttribute("page", getPage());
			model.addAttribute("message", "Wrong verification code!");
			return "emailverification";
		}
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public int getTheId() {
		return theId;
	}

	public void setTheId(int theId) {
		this.theId = theId;
	}

	public boolean isShowedSuccess() {
		return showedSuccess;
	}

	public void setShowedSuccess(boolean showedSuccess) {
		this.showedSuccess = showedSuccess;
	}

}
