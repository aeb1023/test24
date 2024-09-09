package edu.sru.schedule.scheduleadvisement.domain.user;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Provides unit tests for the UserRole class to ensure its getter, setter,
 * and getAuthority methods function as expected.
 */
public class UserRoleTest {

	 /**
     * Tests the getter and setter methods of the UserRole class.
     * Verifies that the set values for ID and authority name are correctly retrieved.
     */
    @Test
    public void testGetterAndSetterMethods() {
    	
    // Create a UserRole object
        UserRole userRole = new UserRole();

        userRole.setId(1);
        userRole.setAuthorityName("ROLE_USER");

    // Test getter methods
        assertEquals(1, userRole.getId());
        assertEquals("ROLE_USER", userRole.getAuthorityName());
    }

    /**
     * Tests the getAuthority method of the UserRole class.
     * Verifies that the authority name is returned correctly.
     */
    @Test
    public void testGetAuthority() {
    	
    // Create a UserRole object
        UserRole userRole = new UserRole();
        userRole.setAuthorityName("ROLE_ADMIN");

    // Test getAuthority method
        assertEquals("ROLE_ADMIN", userRole.getAuthority());
    }
}
