package edu.sru.schedule.scheduleadvisement.controller;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import edu.sru.schedule.scheduleadvisement.domain.user.User;
import jakarta.servlet.ServletContext;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

/**
 * Test class for EmailController, aimed at validating the email sending functionalities provided by the application.
 * It uses Mockito to mock the JavaMailSender and ServletContext to ensure email functionalities can be tested.
 */
class EmailControllerTest {

    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private ServletContext servletContext;
    
    @InjectMocks
    private EmailController emailController;

    /**
     * Initializes Mockito annotations before each test case, which is necessary for mocks and injectMocks to work.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests the sendEmailToList method to ensure it correctly sends emails to all recipients in the list.
     * Verifies that the JavaMailSender.send method is called once for each recipient in the list.
     */
    @Test
    void testSendEmailToList() {
        String subject = "Test Subject";
        String body = "Test Body";
        List<String> recipients = Arrays.asList("user1@example.com", "user2@example.com");

        emailController.sendEmailToList(recipients, subject, body);

        verify(javaMailSender, times(recipients.size())).send(any(SimpleMailMessage.class));
    }

    /**
     * Tests sending of a two-factor authentication email to ensure it sends exactly one email using the provided user details.
     * Verifies that the JavaMailSender.send method is called exactly once.
     */
    @Test
    void testSendTwoFactorAuthEmail() {
        User user = new User();
        user.setEmail("user@example.com");
        String code = "123456";

        emailController.sendTwoFactorAuthEmail(user, code);

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    /**
     * Tests the sending of a verification email which includes constructing a verification link.
     * Mocks network and context path environments to predict the link construction behavior.
     * Verifies that the JavaMailSender.send method is called exactly once.
     * 
     * @throws Exception if there is an error setting up the network environment mocks
     */
    @Test
    void testVerificationEmail() throws Exception {
        User user = new User();
        user.setUsername("user");
        user.setEmail("user@example.com");
        String code = "verification-code";

        when(servletContext.getContextPath()).thenReturn("/app");
        try (MockedStatic<InetAddress> mockedInetAddress = mockStatic(InetAddress.class)) {
            InetAddress mockInetAddress = mock(InetAddress.class);
            mockedInetAddress.when(InetAddress::getLocalHost).thenReturn(mockInetAddress);
            when(mockInetAddress.getHostAddress()).thenReturn("127.0.0.1");
        when(InetAddress.getLocalHost().getHostAddress()).thenReturn("127.0.0.1");

        emailController.verificationEmail(user, code);

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }
    }

    /**
     * Tests the functionality to send a username recovery email to a user.
     * Verifies that exactly one email is sent using the user's registered email address.
     */
    @Test
    void testUsernameRecovery() {
        User user = new User();
        user.setUsername("user");
        user.setEmail("user@example.com");

        emailController.usernameRecovery(user);

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
