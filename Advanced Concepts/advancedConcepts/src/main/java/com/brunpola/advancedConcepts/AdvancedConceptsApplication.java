package com.brunpola.advancedConcepts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication /* (
                       exclude = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class}) */
public class AdvancedConceptsApplication {

  public static void main(String[] args) {
    SpringApplication.run(AdvancedConceptsApplication.class, args);
  }
}
