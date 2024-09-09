package edu.sru.schedule.scheduleadvisement.controller;

import edu.sru.schedule.scheduleadvisement.domain.user.User;
import edu.sru.schedule.scheduleadvisement.domain.user.UserRole;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRepository;
import edu.sru.schedule.scheduleadvisement.service.AdminSettingsService;
import edu.sru.schedule.scheduleadvisement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.security.Principal;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for LandingPageController, which handles operations related to the main user interface
 * such as displaying the home page, admin page, and handling changes to user details like passwords, emails, and phone numbers.
 * This class tests the methods in LandingPageController with a focus on their interactions with the UserService,
 * UserRepository, and PasswordEncoder to ensure correct application behavior.
 */
class LandingPageControllerTest {

    @Mock
    private UserService userService;
    
    @Mock
    private UserRepository userRepository;


    @Mock
    private AdminSettingsService adminSettingsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private Principal principal;

    @InjectMocks
    private LandingPageController landingPageController;

    /**
     * Sets up the test environment before each test, initializing Mockito annotations and preparing a mocked user setup.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup a mock user with a non-null UserRole
        UserRole mockRole = new UserRole();
        mockRole.setAuthorityName("ROLE_USER");
        
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");
        mockUser.setUserRole(mockRole);

        when(principal.getName()).thenReturn("testUser");
        when(userService.getUserByUsername("testUser")).thenReturn(mockUser);
    }

    /**
     * Tests the showHomePage method to ensure it correctly renders the home page view and populates the model with user data.
     */
    @Test
    void showHomePageTest() {   //still need to test admin settings 
        String viewName = landingPageController.showHomePage(model, principal);

        assertNotNull(viewName);
        assertEquals("homePage", viewName);
        verify(model).addAttribute(eq("user"), any(User.class));  
    }
    
    /**
     * Tests the showAdminPage method to verify if the admin page is rendered correctly with the necessary model attributes.
     */
    @Test
    void showAdminPageTest() {
        String viewName = landingPageController.showAdminPage(model, principal);
        assertEquals("admin", viewName);
        verify(model, times(1)).addAttribute(eq("user"), any(User.class));
    }

    /**
     * Tests the changeStudentPassword method to confirm that it successfully changes the user's password when provided valid inputs.
     * This test checks for the correct redirection and password update logic.
     */
    @Test
    void changeStudentPassword_ShouldChangePassword_WhenValid() {
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        User user = new User();
        user.setUsername("testUser");
        user.setPassword(oldPassword);

        when(principal.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);

        String result = landingPageController.changeStudentPassword(oldPassword, newPassword, newPassword, principal, redirectAttributes);
        assertEquals("redirect:/userSettings", result);
    }

    /**
     * Tests the changeStudentEmail method to ensure it updates the user's email address correctly.
     * This test also checks for the proper redirect and success message.
     */
    @Test
    void changeEmailTest() {
        String oldEmail = "old@test.com";
        String newEmail = "new@test.com";
        String confirmEmail = "new@test.com";
        String username = "testUser";
        User mockUser = new User();
        mockUser.setEmail(oldEmail);
        mockUser.setUsername(username);

        when(principal.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(mockUser);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        
        String viewName = landingPageController.changeStudentEmail(oldEmail, newEmail, confirmEmail, principal, redirectAttributes);

        verify(userRepository).save(mockUser);
        assertEquals("redirect:/changeEmail", viewName);
        assertEquals("Your email has been updated successfully.", redirectAttributes.getFlashAttributes().get("success"));
    }

    /**
     * Tests the changePhoneNumber method to verify if the user's phone number is updated correctly.
     * It assesses the function's ability to handle correct inputs and manage successful updates with proper redirect and confirmation message.
     */
    @Test
    void changePhoneNumberTest() {
        String oldPhoneNumber = "123456";
        String newPhoneNumber = "654321";
        String confirmPhoneNumber = "654321";
        String username = "testUser";
        User mockUser = new User();
        mockUser.setPhoneNumber(oldPhoneNumber);
        mockUser.setUsername(username);

        when(principal.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(mockUser);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        
        String viewName = landingPageController.changePhoneNumber(oldPhoneNumber, newPhoneNumber, confirmPhoneNumber, principal, redirectAttributes);

        verify(userRepository).save(mockUser);
        assertEquals("redirect:/changePhoneNumber", viewName);
        assertEquals("Your phone number has been updated successfully.", redirectAttributes.getFlashAttributes().get("success"));
    }

   
}

