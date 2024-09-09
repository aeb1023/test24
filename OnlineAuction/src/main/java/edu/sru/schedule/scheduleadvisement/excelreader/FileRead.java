package edu.sru.schedule.scheduleadvisement.excelreader;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Controller;

/**
 * Controller class designed for reading files or starting services related to file handling.
 */
@Controller
public class FileRead {

	public void start(String[] args) throws IOException, URISyntaxException {
		SpringApplication.run(FileRead.class, args);
	}
}
