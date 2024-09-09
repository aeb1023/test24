package edu.sru.schedule.scheduleadvisement.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import edu.sru.schedule.scheduleadvisement.domain.AdviseeData;
import edu.sru.schedule.scheduleadvisement.domain.user.User;
import edu.sru.schedule.scheduleadvisement.domain.user.UserRole;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRepository;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRoleRepository;
import edu.sru.schedule.scheduleadvisement.repositories.AdviseeRepository; 

import java.util.concurrent.Executor;


/**
 * Configuration class to set up asynchronous task execution and initial data loading for the application.
 * This includes configuring a thread pool for asynchronous tasks and setting up default roles and users.
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AdviseeRepository adviseeRepository;

    /**
     * Defines a bean for task execution with a configured thread pool.
     * This thread pool is customized for application-specific asynchronous operations.
     *
     * @return Executor a configured thread pool executor for asynchronous tasks.
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("poolThread-");
        executor.initialize();
        return executor;
    }

    /**
     * CommandLineRunner bean that sets up default users and roles when the application starts.
     * This ensures that default users their roles are available.
     *
     * @return CommandLineRunner that runs when the application starts
     */
    @Bean
    public CommandLineRunner setupDefaultUsersAndRoles() {
        return args -> {
            createRole("ROLE_ADMIN");
            createRole("ROLE_FACULTY");
            createRole("ROLE_USER");

            User adminUser = createUser("admin", "adminpassword", "ROLE_ADMIN", "Default", "Admin", "adminemail@sru.edu");
            User facultyUser = createUser("faculty", "facultypassword", "ROLE_FACULTY", "Default", "Advisor", "facultyemail@sru.edu");
            User studentUser = createUser("student", "studentpassword", "ROLE_USER", "Default", "Student", "studentemail@sru.edu");

            createOrUpdateAdvisee(studentUser, facultyUser.getId());
        };
    }
 
    /**
     * Creates a new user role if it does not exist in the repository.
     *
     * @param authorityName the authority name of the role to create
     */
    private void createRole(String authorityName) {
        UserRole userRole = userRoleRepository.findByAuthorityName(authorityName);
        if (userRole == null) {
            userRole = new UserRole();
            userRole.setAuthorityName(authorityName);
            userRoleRepository.save(userRole);
        }
    }
  
    /**
     * Creates a user if they do not already exist and assigns a specified role.
     *
     * @param username the username of the new user
     * @param password the password for the new user
     * @param roleName the authority name of the role to assign to the new user
     * @param firstName the first name of the new user
     * @param lastName the last name of the new user
     * @param email the email address of the new user
     * @return User the newly created or found user
     */
    private User createUser(String username, String password, String roleName, String firstName, String lastName, String email) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            UserRole role = userRoleRepository.findByAuthorityName(roleName);
            user = new User();
            user.setUsername(username);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPassword(bCryptPasswordEncoder.encode(password));
            user.setEnabled(true);
            user.setUserRole(role);
            userRepository.save(user);
        }
        return user;
    }

    /**
     * Updates or creates an advisee record for a student with details about their advisor.
     *
     * @param student the student user to update or create as an advisee
     * @param advisorId the advisor's user ID
     */
    private void createOrUpdateAdvisee(User student, Long advisorId) {
        AdviseeData advisee = adviseeRepository.findByEmail(student.getEmail()).orElse(new AdviseeData());
        advisee.setFirstName(student.getFirstName());
        advisee.setLastName(student.getLastName());
        advisee.setEmail(student.getEmail());
        advisee.setAdvisorId(advisorId);
        advisee.setPassword("studentpassword");
        advisee.setPhoneNumber("555-555-5555");

        adviseeRepository.save(advisee);
    }

}


