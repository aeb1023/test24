package edu.sru.schedule.scheduleadvisement.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.sru.schedule.scheduleadvisement.domain.user.User;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRepository;

/**
 * Service class for managing user data.
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User updateUserProfile(Long userId, User user) {
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException(
                        "User with id " + userId + " does not exist"
                ));
        return userRepository.save(userToUpdate);
    }

    //Add user
    public User addUser(User user) {
        return userRepository.save(user);
    }

    //Delete user
    public void deleteUser(Long userId) {
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            throw new IllegalStateException("User with id " + userId + " does not exist");
        }
        userRepository.deleteById(userId);
    }

    //Get user by id
    public User getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException(
                        "User with id " + userId + " does not exist"
                ));
        return user;
    }

    //Get user by username
    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalStateException("User with username " + username + " does not exist");
        }
        return user;
    }
    
    public List<User> getAllUsers() {
        Iterable<User> usersIterable = userRepository.findAll();
        List<User> userList = new ArrayList<>();
        for (User user : usersIterable) {
            userList.add(user);
        }
        return userList;
    }
    
    public List<User> searchUsers(String query, String filterType) {
        List<User> allUsers = getAllUsers();
        List<User> matchedUsers = new ArrayList<>();

        for (User user : allUsers) {
            if (!"ROLE_USER".equals(user.getUserRole().getAuthority())) {
                continue; // Skip users who do not have ROLE_USER as their role
            }

            if ("name".equals(filterType) && user.getUsername().contains(query)) {
                matchedUsers.add(user);
            } else if ("email".equals(filterType) && user.getEmail().contains(query)) {
                matchedUsers.add(user);
            }
        }


        return matchedUsers;
    }
    
//    public void refreshUser(User user) {
//    	userController.setCurrently_Logged_In(user);
//    	userRepository.save(user);
//    	
//    	//return user;
//    }
}
