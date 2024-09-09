package edu.sru.schedule.scheduleadvisement.controller;

import edu.sru.schedule.scheduleadvisement.domain.AdviseeData;
import edu.sru.schedule.scheduleadvisement.domain.user.User;
import edu.sru.schedule.scheduleadvisement.repositories.AdviseeRepository;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for FacultyController, focused on testing the functionality related to faculty interactions with advisees.
 * It uses Mockito for mocking dependencies and injects them into the FacultyController to simulate application behaviors.
 */
class FacultyControllerTest {

    @Mock
    private EmailController emailController;

    @Mock
    private AdviseeRepository adviseeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    @InjectMocks
    private FacultyController facultyController;

    /**
     * Initializes Mockito annotations before each test, setting up the mocks and injectMocks for use in each test method.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests the sendEmailToAdvisees method in the FacultyController.
     * Ensures that emails are correctly sent to a list of advisees retrieved based on the authenticated user's advisor ID.
     * Checks that the email is sent exactly once with the correct subject and body, and that the correct view is returned with a success message.
     */
    @Test
    void testSendEmailToAdvisees() {
        String advisorEmail = "advisor@example.com";
        Long advisorId = 1L;
        User advisor = new User();
        advisor.setId(advisorId);
        advisor.setEmail(advisorEmail);

        AdviseeData advisee = new AdviseeData();
        advisee.setEmail("advisee@example.com");
        List<AdviseeData> advisees = Arrays.asList(advisee);

        when(authentication.getName()).thenReturn(advisorEmail);
        when(userRepository.findByUsername(advisorEmail)).thenReturn(advisor);
        when(adviseeRepository.findByAdvisorId(advisorId)).thenReturn(advisees);

        String subject = "Test Subject";
        String body = "Test Body";

        String viewName = facultyController.sendEmailToAdvisees(subject, body, authentication, model);

        verify(emailController, times(1)).sendEmailToList(anyList(), eq(subject), eq(body));
        assertEquals("emailAdvisees", viewName);
        verify(model, times(1)).addAttribute(eq("successMessage"), anyString());
    }
}
