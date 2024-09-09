package edu.sru.schedule.scheduleadvisement.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import edu.sru.schedule.scheduleadvisement.domain.AdvisorData;
import edu.sru.schedule.scheduleadvisement.domain.user.User;
import edu.sru.schedule.scheduleadvisement.domain.user.UserRole;
import edu.sru.schedule.scheduleadvisement.repositories.AdvisorRepository;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRepository;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRoleRepository;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for AdminController using Spring MVC testing framework.
 * It sets up a mock MVC environment for testing the functionality provided by AdminController.
 * The class utilizes mock user authentication for an admin role to test access control and functionality.
 */
@WebMvcTest(AdminController.class)
@WithMockUser(username="admin", roles={"ADMIN"})
public class AdminControllerTest {

	@Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AdvisorRepository advisorRepository; 

    @MockBean
    private UserRoleRepository userRoleRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private AdminController adminController;

    /**
     * Sets up the testing environment before each test.
     * Initializes Mockito annotations and configures the MockMvc object with necessary view resolvers.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                                 .setViewResolvers(viewResolver)
                                 .build();
    }

    /**
     * Tests the view users functionality by making a GET request to the appropriate URL.
     * It verifies that the response status is OK, the correct view is returned, and the model contains an attribute for users.
     * @throws Exception if there's an error performing the mock request
     */
    @Test
    void testViewUsers() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(new User());
        when(userRepository.findAll()).thenReturn(users);

        mockMvc.perform(get("/admin/view-users"))
               .andExpect(status().isOk())
               .andExpect(view().name("viewUsers"))
               .andExpect(model().attributeExists("users"));
    }

    /**
     * Tests the display of system settings page by making a GET request to the system settings URL.
     * It checks that the status is OK and that the correct view name is returned.
     * @throws Exception if there's an error performing the mock request
     */
    @Test
    void testShowSystemSettings() throws Exception {
        mockMvc.perform(get("/systemSettings"))
               .andExpect(status().isOk())
               .andExpect(view().name("systemSettings"));
    }
    
    /**
     * Tests the upload advisors functionality with an empty file.
     * It attempts to upload an empty Excel file and checks for a redirection and the presence of an error message in the flash attributes.
     * @throws Exception if there's an error performing the mock request
     */
    @Test
    void testUploadAdvisorsEmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("excelFile", "", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]);

        mockMvc.perform(multipart("/admin/upload-advisors").file(file))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/admin/upload-advisors"))
               .andExpect(flash().attributeExists("errorMessage"));
    }
    
   

    /**
     * Tests the addition of a new advisor.
     * @throws Exception if there's an error performing the mock request
     * 
     * FAILS
     */
    @Test
    void testAddAdvisor() throws Exception {
        AdvisorData advisor = new AdvisorData("Jane", "Smith", "jane.smith@example.com", "0987654321", "securepassword");
        mockMvc.perform(post("/admin/add-advisor")
                .param("firstName", advisor.getFirstName())
                .param("lastName", advisor.getLastName())
                .param("email", advisor.getEmail())
                .param("phoneNumber", advisor.getPhoneNumber())
                .param("password", advisor.getPassword()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/upload-advisors"))
                .andExpect(flash().attributeExists("message"));
    }

    /**
     * Tests editing an existing advisor's details.
     * @throws Exception if there's an error performing the mock request
     * 
     * FAILS
     */
    @Test
    void testEditAdvisor() throws Exception {
        Long advisorId = 1L;
        AdvisorData updatedAdvisor = new AdvisorData("Updated", "Advisor", "update@example.com", "0000000000", "newpassword");

        when(advisorRepository.findById(eq(advisorId))).thenReturn(Optional.of(new AdvisorData()));
        when(advisorRepository.save(any(AdvisorData.class))).thenReturn(updatedAdvisor);

        mockMvc.perform(post("/admin/update-advisor/" + advisorId)
                .param("firstName", updatedAdvisor.getFirstName())
                .param("lastName", updatedAdvisor.getLastName())
                .param("email", updatedAdvisor.getEmail())
                .param("phoneNumber", updatedAdvisor.getPhoneNumber())
                .param("password", updatedAdvisor.getPassword()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/upload-advisors"))
                .andExpect(flash().attributeExists("message"));
    }

    /**
     * Tests the deletion of an advisor.
     * @throws Exception if there's an error performing the mock request
     * 
     * FAILS
     */
    @Test
    void testDeleteAdvisor() throws Exception {
        Long advisorId = 1L;
        when(advisorRepository.existsById(eq(advisorId))).thenReturn(true);

        mockMvc.perform(get("/admin/delete-advisor/" + advisorId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/upload-advisors"))
                .andExpect(flash().attributeExists("message"));
    }

}

