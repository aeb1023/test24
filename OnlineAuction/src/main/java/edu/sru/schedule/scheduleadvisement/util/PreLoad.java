package edu.sru.schedule.scheduleadvisement.util;

import java.io.IOException;
import java.io.InputStream;

import java.util.Scanner;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class PreLoad {

  public static String getDefaultCountryCode() {
    Resource resource = new ClassPathResource("static/preload/default_country.txt");

    try (InputStream inputStream = resource.getInputStream();
        Scanner sc = new Scanner(inputStream)) {
        return sc.nextLine().trim();
    } catch (IOException e) {
        throw new RuntimeException("Failed to load default country code", e);
    }
  }

}
