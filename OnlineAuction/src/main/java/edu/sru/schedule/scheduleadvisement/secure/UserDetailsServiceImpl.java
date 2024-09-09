package edu.sru.schedule.scheduleadvisement.secure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

import edu.sru.schedule.scheduleadvisement.repository.user.UserRepository;

/**
 * This service is responsible for retrieving user details from the database
 * for user authentication purposes. Implements Spring Security's UserDetailsService interface.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	/**
     * Loads the user's data given the username.
     *
     * @param username the username identifying the user whose data is required.
     * @return UserDetails containing necessary information like username, password, and authorities.
     * @throws UsernameNotFoundException if no user is found with the given username or if the user is not enabled.
     */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		edu.sru.schedule.scheduleadvisement.domain.user.User user1 = userRepository.findByUsername(username);
		if (user1 == null || !user1.getEnabled()) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}

		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user1.getUserRole().getAuthority());

		UserDetails user = User.withUsername(user1.getUsername()).password(user1.getPassword())
				.authorities(Collections.singletonList(authority)) // checks authority
				.accountExpired(!user1.isAccountNonExpired()).accountLocked(!user1.isAccountNonLocked())
				.credentialsExpired(!user1.isCredentialsNonExpired()).disabled(!user1.isEnabled()).build();
		return user;
	}
}
