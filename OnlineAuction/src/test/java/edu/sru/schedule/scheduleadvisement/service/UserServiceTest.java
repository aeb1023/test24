package edu.sru.schedule.scheduleadvisement.service;

import edu.sru.schedule.scheduleadvisement.domain.user.User;
import edu.sru.schedule.scheduleadvisement.domain.user.UserRole;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateUserProfileTest() {
        User userToUpdate = new User();
        userToUpdate.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userToUpdate));
        when(userRepository.save(any(User.class))).thenReturn(userToUpdate);

        User updatedUser = userService.updateUserProfile(1L, userToUpdate);
        assertNotNull(updatedUser, "Updated user should not be null");
    }

    @Test
    void addUserTest() {
        User newUser = new User();
        newUser.setUsername("newUser");
        when(userRepository.save(newUser)).thenReturn(newUser);

        User resultUser = userService.addUser(newUser);
        assertEquals(newUser.getUsername(), resultUser.getUsername(), "Added user should match the new user");
    }

    @Test
    void deleteUserTest() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        userService.deleteUser(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void getUserByIdTest() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User resultUser = userService.getUserById(1L);
        assertEquals(user.getId(), resultUser.getId(), "Fetched user should match the expected user");
    }

    @Test
    void getUserByUsernameTest() {
        User user = new User();
        user.setUsername("testUser");

        UserRole role = new UserRole();
        role.setAuthorityName("ROLE_USER"); 
        user.setUserRole(role);

        when(userRepository.findByUsername("testUser")).thenReturn(user);

        User resultUser = userService.getUserByUsername("testUser");
        assertEquals("testUser", resultUser.getUsername(), "Fetched user should match the expected username");
        assertEquals("ROLE_USER", resultUser.getUserRole().getAuthority(), "The user role should match ROLE_USER");
    }

    @Test
    void getAllUsersTest() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        List<User> users = userService.getAllUsers();
        assertFalse(users.isEmpty(), "User list should not be empty");
        assertEquals(1, users.size(), "User list size should match the expected size");
    }

    @Test
    void searchUsersTest() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setEmail("testUser@test.com");

        UserRole role = new UserRole();
        role.setId(1L);
        role.setAuthorityName("ROLE_USER");
        user.setUserRole(role);

        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        List<User> matchedUsers = userService.searchUsers("test", "name");
        assertFalse(matchedUsers.isEmpty(), "Matched user list should not be empty for 'name' filter");
        assertEquals(1, matchedUsers.size(), "Matched user list size for 'name' filter should match the expected size");

        matchedUsers = userService.searchUsers("testUser@test.com", "email");
        assertFalse(matchedUsers.isEmpty(), "Matched user list should not be empty for 'email' filter");
        assertEquals(1, matchedUsers.size(), "Matched user list size for 'email' filter should match the expected size");
    }



}
