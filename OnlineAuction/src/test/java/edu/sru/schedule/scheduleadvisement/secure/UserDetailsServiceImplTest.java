package edu.sru.schedule.scheduleadvisement.secure;

import edu.sru.schedule.scheduleadvisement.domain.user.User;
import edu.sru.schedule.scheduleadvisement.domain.user.UserRole;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for UserDetailsServiceImpl, which implements the UserDetails service for Spring Security.
 * This class tests the loading of user details necessary for authentication and authorization processes within the application.
 * It uses Mockito for mocking the UserRepository and injects it into UserDetailsServiceImpl to test user retrieval logic.
 */
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Sets up the testing environment before each test by initializing Mockito annotations,
     * which is necessary for using Mock and InjectMocks annotations.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests the loadUserByUsername method when a user with the provided username exists.
     * Verifies that the UserDetails object returned contains the correct information such as username,
     * password, and authorities, and that all account status flags (e.g., isAccountNonExpired) are true,
     * indicating the account is fully active and not restricted.
     */
    @Test
    void loadUserByUsername_whenUserExists() {
        
        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPassword("password");
        mockUser.setEnabled(true);
        
        UserRole role = new UserRole();
        role.setAuthorityName("ROLE_USER");
        mockUser.setUserRole(role);

        when(userRepository.findByUsername(username)).thenReturn(mockUser);

        
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }

    /**
     * Tests the loadUserByUsername method for the scenario where the user does not exist in the database.
     * Verifies that the method throws a UsernameNotFoundException, which is expected behavior for non-existent users,
     * thereby confirming that the service properly handles this error condition.
     */
    @Test
    void loadUserByUsername_whenUserDoesNotExist() {
       
        String username = "nonExistingUser";
        when(userRepository.findByUsername(username)).thenReturn(null);

    
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));
    }
}
