package edu.sru.schedule.scheduleadvisement.excelreader;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import edu.sru.schedule.scheduleadvisement.domain.AdviseeData;

/**
 * Test class for AdviseeService, which manages operations related to the storage and retrieval of advisee data.
 * This class utilizes Spring's @ SpringBootTest annotation to leverage the full Spring application context for integration testing.
 */
@SpringBootTest
public class AdviseeServiceTest {

    private AdviseeService adviseeService;
    
    /**
     * Sets up the AdviseeService instance before each test. This setup method initializes the service so it's ready to accept data for tests.
     */
    @BeforeEach
    public void setUp() {
        adviseeService = new AdviseeService();
    }
    
    /**
     * Tests the getAdviseeArray method to ensure it correctly manages the storage and retrieval of AdviseeData objects.
     * This test adds two specific AdviseeData instances to the service's data storage and then verifies that these instances
     * can be retrieved accurately, ensuring both the correct order and the integrity of the data.
     */
    @Test
    public void testGetAdviseeArray() {
        AdviseeData adviseeData1 = new AdviseeData("Rocky", "Rock", "rtr1889@sru.edu", "8007789111", "thepassword");
        AdviseeData adviseeData2 = new AdviseeData("John", "Doe", "jdd1234@sru.edu", "1234567890", "thepassword");
        adviseeService.getAdviseeArray().add(adviseeData1);
        adviseeService.getAdviseeArray().add(adviseeData2);
        
        List<AdviseeData> adviseeArray = adviseeService.getAdviseeArray();
        
  // Verify that the advisee array contains the added advisee data
        assertEquals(2, adviseeArray.size());
        assertEquals(adviseeData1, adviseeArray.get(0));
        assertEquals(adviseeData2, adviseeArray.get(1));
    }
}
