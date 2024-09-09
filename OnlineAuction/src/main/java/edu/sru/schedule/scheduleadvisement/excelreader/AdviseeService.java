package edu.sru.schedule.scheduleadvisement.excelreader;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import edu.sru.schedule.scheduleadvisement.domain.AdviseeData;

/**
 * Service class for managing a list of AdviseeData objects.
 * This class provides a centralized location to store and retrieve advisee information.
 */
@Service
public class AdviseeService {
	private final List<AdviseeData> advisee = new ArrayList<AdviseeData>();
	
	public List<AdviseeData> getAdviseeArray() {
		return advisee;
	}
	
}