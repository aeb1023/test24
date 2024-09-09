package edu.sru.schedule.scheduleadvisement.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import cn.apiclub.captcha.Captcha;
import edu.sru.schedule.scheduleadvisement.domain.user.User;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRepository;
import edu.sru.schedule.scheduleadvisement.secure.CaptchaUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


/**
 * Controller class for managing user-related operations.
 */
@Controller
public class UserController {
	//private User Currently_Logged_In;
	private UserRepository userRepository;
	private UtilityController util;
	@PersistenceContext
	private EntityManager entityManager;
		
	UserController(UserRepository userRepository, UtilityController util) {
		this.userRepository = userRepository;
		this.util = util;
	}
	
	// Basic CRUD functions
	@RequestMapping("/get-user") 
	public User getUser(@PathVariable("id") long id, Model model) {
		User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ID passed to find a user"));
		return user;
	}
	
	@RequestMapping("/get-username") 
	public User getUserByUsername(@PathVariable("username") String name) {
		User user = userRepository.findByUsername(name);
		return user;
	}
	
	@RequestMapping("/get-email") 
	public User getUserByEmail(@PathVariable("email") String email) {
		User user = userRepository.findByEmail(email);
		return user;
	}

	@RequestMapping("/get-all-users") 
	public Iterable<User> getAllUsers() {
		Iterable<User> users = userRepository.findAll();
		return users;
	}
	

	@Autowired
	private PasswordEncoder passwordEncoder;
	@PostMapping("/add-user")
	public User addUser(@Valid User user, BindingResult result) {
		// Find specific errors
		// Verifies that the giver user.creationDate is in a valid format
		//LocalDate.parse(user.getCreationDate());
		// Find other errors determined by the result
		if (result.hasErrors()) {
			// Temporary - just returns to the website index
			throw new IllegalArgumentException();
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setPasswordconf(util.randomStringGenerator());
		return userRepository.save(user);
	}
	@PostMapping("/edit-user")
	public User editUser(@Valid User user, String oldPass) {
		// Find specific errors
		// Verifies that the giver user.creationDate is in a valid format
		//LocalDate.parse(user.getCreationDate());
		// Find other errors determined by the result
		
		// only change the password if the new one if different from the old
		if (user.getPassword() != oldPass) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		
		user.setPasswordconf(util.randomStringGenerator());
		return userRepository.save(user);
	}
	
	@PostMapping("/edit-user/{id}/{returnPath}")
	public void updateUser(@PathVariable("id") long id, @PathVariable("returnPath") String returnPath,
							 @Validated User user, BindingResult result, Model model) {
		if (result.hasErrors()) {
			// Temporary - just returns to the website index
			throw new IllegalArgumentException();
		}
		
		userRepository.save(user);
	}
	
	@PostMapping("/delete-user/{id}")
	public void deleteUser(@PathVariable("id") long id, Model model) {
		User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ID passed to delete a User"));
		userRepository.delete(user);
	}
	// End of basic CRUD functions
	
	void getCaptcha(User user) {
		Captcha captcha = CaptchaUtil.createCaptcha(240, 70);
		user.setHiddenCaptcha(captcha.getAnswer());
		user.setCaptcha(""); // value entered by the User
		user.setRealCaptcha(CaptchaUtil.encodeCaptcha(captcha));
		
	}
	

}
