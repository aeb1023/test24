package edu.sru.schedule.scheduleadvisement.controller;

import edu.sru.schedule.scheduleadvisement.domain.user.User;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRepository;
import edu.sru.schedule.scheduleadvisement.secure.TwoFactorAuthentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.mockito.Mockito.*;
import org.springframework.mock.web.MockHttpSession;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for IndexController, responsible for handling web requests related to index and login functionalities.
 * It utilizes Spring MVC's MockMvc to simulate HTTP requests and verify the responses without actually starting a server.
 * This class tests various endpoints, including the display of pages and the handling of two-factor authentication (2FA).
 */
class IndexControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TwoFactorAuthentication twoFactorAuth;

    @InjectMocks
    private IndexController indexController;

    private MockMvc mockMvc;

    /**
     * Sets up the test environment before each test.
     * Initializes mocks and configures the MockMvc instance with a view resolver to handle forwarding to JSP files.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(indexController)
                                 .setViewResolvers(viewResolver)
                                 .build();
    }
    
    /**
     * Tests the rendering of the index page.
     * Verifies that the correct view is returned and that the HTTP status is OK.
     * 
     * @throws Exception if there's an error performing the mock request
     */
    @Test
    void showIndex() throws Exception {
        mockMvc.perform(get("/index"))
               .andExpect(status().isOk())
               .andExpect(view().name("index"));
    }

    /**
     * Tests the rendering of the login page.
     * Verifies that the correct view is returned and that the HTTP status is OK.
     * 
     * @throws Exception if there's an error performing the mock request
     */
    @Test
    void showLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
               .andExpect(status().isOk())
               .andExpect(view().name("login"));
    }

    /**
     * Tests the verification of two-factor authentication (2FA) codes.
     * Simulates a user session, submits a 2FA code, and verifies that the authentication process is handled correctly.
     * Checks for the proper redirection after successful 2FA verification.
     * Verifies that the twoFactorAuth service's verify2FACode method is called exactly once with the correct parameters.
     * 
     * @throws Exception if there's an error performing the mock request
     */
    @Test
    void verify2FA() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("tempUsername", "user@example.com");

        when(userRepository.findByUsername("user@example.com")).thenReturn(new User());
        when(twoFactorAuth.verify2FACode(any(User.class), eq("1234"))).thenReturn(true);

        mockMvc.perform(post("/verify2FA")
                .param("code", "1234")
                .session(session)) 
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/homePage"));

        verify(twoFactorAuth, times(1)).verify2FACode(any(User.class), eq("1234"));
    }


}
