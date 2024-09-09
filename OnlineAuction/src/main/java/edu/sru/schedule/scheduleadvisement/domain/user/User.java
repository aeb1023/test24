package edu.sru.schedule.scheduleadvisement.domain.user;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;


/**
 * Represents a user within the system, including their personal and security information,
 * as well as attributes such as roles and authentication details.
 */
@Entity
public class User implements UserDetails {
	final int MAX = 200;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private boolean enabled = true;
	@Size(min=1,max=200, message = "Enter name up to 200 characters")
	private String firstName;
	@Size(min=1,max=200, message = "Enter name up to 200 characters")
	private String lastName;
	@Size(min=1,max=200, message = "Enter valid phone number")
	private String phoneNumber;

	private String countryCode;

	private String secret1;

	private String secret2;

	private String secret3;
	@Size(min=1,max=200, message = "Enter answer up to 200 characters")
	private String userSecret1;
	@Size(min=1,max=200, message = "Enter answer up to 200 characters")
	private String userSecret2;
	@Size(min=1,max=200, message = "Enter answer up to 200 characters")
	private String userSecret3;

	@Transient
	private String captcha;



	@Transient
	private String hiddenCaptcha;

	@Transient
	private String realCaptcha;

	private String emailVerification;

	private String twoFactorAuthenticationCode;

	private boolean isTwoFactorEnabled;
	
	//password reset
	private boolean passwordResetRequired;

	// The name that is visible to other users
	@NonNull
	@Size(max=200, message = "Maximum Character limit is 200")
	private String displayName;

	// The name used to login
	// Each username should be unique, so we use it as the primary key for ease of verifying uniqueness

	//@UniqueLogin(message = "That Username is Already Taken")
	@Size(min=6, max=30, message = "Require Unique Username between 6-30 characters.")
	private String username;

	// Password - temporary
	// TODO: Create a permanent/secure solution
	@NotBlank(message = "Require Password between 6-30 characters.")
	private String password;
	@Size(min=6, max=30, message = "Require Password between 6-30 characters.")
	private String passwordconf;
	@Size(min=6, max=100, message = "Enter a valid email-address between 6-100 characters")
	@Email(message = "Please Provide a Valid Email Address.")
	private String email;

	// A user may not have a description - store it as an empty String
	@Size(max=200, message = "Maximum Character limit is 200")
	private String userDescription;

	private String userImage;

	// Account creation date
	@NonNull
	private String creationDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

	// TODO: The interface says that this should be a collection of Authorities (UserRole)
	// Update this at a later date
	@ManyToOne
	private UserRole userRole;

	// Security fields
	@NonNull
	private boolean accountExpired;
	@NonNull
	private boolean accountLocked;
	@NonNull
	private boolean credentialsExpired;
	@NonNull
	private boolean disabledAccount;
	
	
	//Model for users
	public User(User user) {
		this.displayName = user.displayName;
		this.userRole = user.userRole;
		this.username = user.username;
		this.password = user.password;
		this.passwordconf = user.passwordconf;
		this.email = user.email;
		this.userDescription = user.userDescription;
		this.creationDate = user.creationDate;
		this.userRole = user.userRole;
		this.accountExpired = user.accountExpired;
		this.accountLocked = user.accountLocked;
		this.credentialsExpired = user.credentialsExpired;
		this.disabledAccount = user.disabledAccount;
		this.emailVerification = user.emailVerification;
		this.isTwoFactorEnabled = user.isTwoFactorEnabled;
		this.twoFactorAuthenticationCode = user.twoFactorAuthenticationCode;
	}

	public User() {
		this.isTwoFactorEnabled = false;
	}

	
	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public String getPasswordconf() {
		return passwordconf;
	}

	public void setPasswordconf(String passwordconf) {
		this.passwordconf = passwordconf;
	}

	// Methods for accessing variables
	@Override
	public String getUsername() {
		return username;
	}
	// user roles 
	public UserRole getUserRole() {
		return this.userRole;
	}
	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}


	public void setUsername(String userName) { 
		this.username = userName; 
	}

	public String getEmail() { 
		return email; 
	}

	public void setEmail(String email) {
		this.email = email; 
	}

	public String getUserDescription() { 
		return userDescription;
	}

	public void setUserDescription(String userDescription) { 
		this.userDescription= userDescription; 
	}

	public String getCreationDate() { 
		return creationDate; 
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getEmailVerification() {
		return emailVerification;
	}

	public void setEmailVerification(String emailVerification) {
		this.emailVerification = emailVerification;
	}

	public String getTwoFactorAuthenticationCode() {
		return twoFactorAuthenticationCode;
	}

	public void setTwoFactorAuthenticationCode(String twoFactorAuthenticationCode) {
		this.twoFactorAuthenticationCode = twoFactorAuthenticationCode;
	}

	public boolean isTwoFactorEnabled() {
		return isTwoFactorEnabled;
	}

	public void setTwoFactorEnabled(boolean isTwoFactorEnabled) {
		this.isTwoFactorEnabled = isTwoFactorEnabled;
	}

	public String getUserSecret1() {
		return userSecret1;
	}

	public void setUserSecret1(String userSecret1) {
		this.userSecret1 = userSecret1;
	}

	public String getUserSecret2() {
		return userSecret2;
	}

	public void setUserSecret2(String userSecret2) {
		this.userSecret2 = userSecret2;
	}

	public String getUserSecret3() {
		return userSecret3;
	}

	public void setUserSecret3(String userSecret3) {
		this.userSecret3 = userSecret3;
	}

	public String getSecret1() {
		return secret1;
	}

	public void setSecret1(String secretQuestion1) {
		this.secret1 = secretQuestion1;
	}

	public String getSecret2() {
		return secret2;
	}

	public void setSecret2(String secretQuestion2) {
		this.secret2 = secretQuestion2;
	}

	public String getSecret3() {
		return secret3;
	}

	public void setSecret3(String secretQuestion3) {
		this.secret3 = secretQuestion3;
	}

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	public String getHiddenCaptcha() {
		return hiddenCaptcha;
	}

	public void setHiddenCaptcha(String hiddenCaptcha) {
		this.hiddenCaptcha = hiddenCaptcha;
	}

	public String getRealCaptcha() {
		return realCaptcha;
	}

	public void setRealCaptcha(String realCaptcha) {
		this.realCaptcha = realCaptcha;
	}

	public void setCreationDate(String creationDate) { 
		this.creationDate = creationDate; 
	}

	
	
	//Override methods from UserDetails 

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
	    return Collections.singletonList(new SimpleGrantedAuthority(this.getUserRole().getAuthority()));
	}


	@Override
	public boolean isAccountNonExpired() {
		return !accountExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !accountLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return !credentialsExpired;
	}

	@Override
	public boolean isEnabled() {
		return !disabledAccount;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean getEnabled()
	{
		return enabled;
	}
	//password reset 
	public boolean isPasswordResetRequired() {
        return passwordResetRequired;
    }
	//setter for reset password 
	public void setPasswordResetRequired(boolean passwordResetRequired) {
        this.passwordResetRequired = passwordResetRequired;
    }


	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}
}