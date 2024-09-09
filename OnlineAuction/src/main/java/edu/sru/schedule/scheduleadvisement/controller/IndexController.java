package edu.sru.schedule.scheduleadvisement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.sru.schedule.scheduleadvisement.domain.user.User;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRepository;
import edu.sru.schedule.scheduleadvisement.secure.TwoFactorAuthentication;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller class responsible for handling various web page requests within the schedule advisement application.
 * It provides mappings for different URIs like home page, login, registration information, and two-factor authentication verification.
 */
@Controller
public class IndexController {
  
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TwoFactorAuthentication twoFactorAuth;

  /**
   * Displays the main index page.
   * This method responds to a GET request to "/index".
   *
   * @param model the model object that can be used to pass data to the view
   * @return the name of the view to be rendered, specifically "index"
   */
  @RequestMapping({"/index"})
  public String showUserList(Model model) {
    return "index";
  }

  @RequestMapping({"/"})
  public String showIndex(Model model) {
    return "index";
  }

  @RequestMapping({"/login"})
  public String showLoginPage() {
    return "login";
  }
  
  /**
   * Displays registration info.
   * This method responds to a GET request to "/missionStatement".
   *
   * @return the name of the view "registrationInfo"
   */
  @RequestMapping({"/missionStatement"})
  public String showMission() {
    return "registrationInfo";
  }

  @RequestMapping({"/FAQ"})
  public String showFAQ() {
    return "FAQ";
  }

  /**
   * Processes the POST request for two-factor authentication verification.
   * Validates the provided 2FA code for the user temporarily stored in the session.
   *
   * @param code the 2FA code submitted by the user
   * @param request the {@link HttpServletRequest} containing the session with the temporary username
   * @return a redirect if verification is successful, redirects to the homepage, otherwise redirects back to the 2FA page with an error
   */
  @PostMapping({"/verify2FA"})
  public String verify2FA(@RequestParam String code, HttpServletRequest request) {
    String username = (String) request.getSession().getAttribute("tempUsername");
    if (username == null) {
        return "redirect:/login";
    }

    User user = userRepository.findByUsername(username);
    if (twoFactorAuth.verify2FACode(user, code)) {
        request.getSession().removeAttribute("tempUsername"); // Clear the temporary attribute
        // Complete the authentication and redirect
        return "redirect:/homePage";
    }

    return "redirect:/verify2FAPage?error";
  }

  /**
   * Displays the page for two-factor authentication verification.
   * Not currently in use due to disabled sign up feature
   * 
   * @return the name of the view "verify2FA"
   */
  @RequestMapping({"/verify2FAPage"})
  public String showVerify2FAPage() {
    return "verify2FA";
  }
}
