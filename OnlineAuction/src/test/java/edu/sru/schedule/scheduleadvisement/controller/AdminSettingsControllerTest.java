package edu.sru.schedule.scheduleadvisement.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * This class tests the AdminSettingsController using Spring's testing framework to manage the web context.
 * The class is annotated with @ SpringBootTest to indicate that it uses a full application context.
 * @ AutoConfigureMockMvc is used to inject a  MockMvc instance for testing MVC controllers without starting a full HTTP server.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AdminSettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

   
    /**
     * Tests the ability of the admin user to view settings through a GET request.
     * This test ensures that the correct view is returned and that the necessary model attributes are present.
     * It uses @ WithMockUser to simulate an authenticated user with admin privileges.
     * 
     * @throws Exception if the mock MVC request building or execution fails
     */
    @Test
    @WithMockUser(username="admin", roles={"ADMIN"})
    public void showSettings() throws Exception {
        mockMvc.perform(get("/admin/settings"))
               .andExpect(status().isOk())
               .andExpect(model().attributeExists("settings"))
               .andExpect(view().name("adminSettings"));
    }

    /**
     * Tests the admin's ability to update settings through a POST request.
     * This method sends form data representing settings updates and checks for a redirect response,
     * indicating that the data was processed successfully.
     * The CSRF token is included to mimic the security context of actual user interactions.
     * It uses @ WithMockUser to simulate an authenticated user with admin privileges.
     * 
     * @throws Exception if the mock MVC request building or execution fails
     */
    @Test
    @WithMockUser(username="admin", roles={"ADMIN"})
    public void updateSettings() throws Exception {
        mockMvc.perform(post("/admin/settings")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) 
                        .param("academicYearStart", "2022")
                        .param("academicYearEnd", "2023")
                        .param("semester", "Fall")
                        .param("registrationStartDate", "2022-08-01")
                        .param("registrationEndDate", "2022-12-15")
                        .param("supportContactEmail", "support@example.com"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/admin/settings"));
    }
}

