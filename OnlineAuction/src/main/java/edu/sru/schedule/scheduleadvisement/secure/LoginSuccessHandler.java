package edu.sru.schedule.scheduleadvisement.secure;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;

import edu.sru.schedule.scheduleadvisement.domain.user.User;
import edu.sru.schedule.scheduleadvisement.repository.user.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom authentication success handler to manage user redirection after login
 * based on their roles and two-factor authentication status.
 */
@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TwoFactorAuthentication twoFactorService;

	/**
     * Handles the logic to be executed when a user logs in successfully.
     *
     * @param request the request during which the login attempt occurred.
     * @param response the response associated with the request.
     * @param authentication the authentication token for the current user.
     * @throws IOException if an input or output exception occurs.
     * @throws ServletException if a servlet-specific error occurs.
     */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		String username = ((UserDetails) authentication.getPrincipal()).getUsername();
		User user = userRepository.findByUsername(username);

	
		if (user.isTwoFactorEnabled()) {
			twoFactorService.send2FACode(user);
			request.getSession().setAttribute("tempUsername", username);
			getRedirectStrategy().sendRedirect(request, response, "/verify2FAPage");
		} else {
			// Check if the user has the ROLE_ADMIN authority and directs them to the admin landing page
			if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
				getRedirectStrategy().sendRedirect(request, response, "/admin");
			} else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
				// users with ROLE_USER to the student.html page
				getRedirectStrategy().sendRedirect(request, response, "student");
			} else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_FACULTY"))) {
				// users with ROLE_FACULTY to the faculty.html page
				getRedirectStrategy().sendRedirect(request, response, "/faculty"); 
			
			}else {
				//  redirect to homepage 
				getRedirectStrategy().sendRedirect(request, response, "/homePage");
			}
		}
	}
}
