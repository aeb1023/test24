package edu.sru.schedule.scheduleadvisement.controller;

import java.util.Random;

import org.springframework.web.bind.annotation.RestController;

/**
 * A utility controller that provides various helper methods for common functionalities
 * required throughout the application.
 */
@RestController
public class UtilityController {

public String randomStringGenerator()
{
	 int leftLimit = 97; // letter 'a'
	    int rightLimit = 122; // letter 'z'
	    int targetStringLength = 10;
	    Random random = new Random();
	    StringBuilder buffer = new StringBuilder(targetStringLength);
	    for (int i = 0; i < targetStringLength; i++) {
	        int randomLimitedInt = leftLimit + (int) 
	          (random.nextFloat() * (rightLimit - leftLimit + 1));
	        buffer.append((char) randomLimitedInt);
	    }
	    String generatedString = buffer.toString();
	    return generatedString;
	}
}

