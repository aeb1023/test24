package edu.sru.schedule.scheduleadvisement.repository.user;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import edu.sru.schedule.scheduleadvisement.domain.user.User;

/**
 * Repository for accessing user data.
 */
public interface UserRepository extends CrudRepository<User, Long> {
	
	User findByUsername(String username);
	User findByEmail(String email);
	
	List<User> findByUsernameStartingWith(String query);
	List<User> findByEmailContaining(String query);
	List<User> findByUserRole_AuthorityName(String authorityName);

	
}
	