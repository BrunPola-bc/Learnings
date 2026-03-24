package com.brunpola.cv_management;

import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the CV Management Spring Boot application.
 *
 * <p>This class bootstraps the Spring context and starts the embedded web server. It also logs an
 * informational message when the application starts.
 */
@SpringBootApplication
@Log
public class CvManagementApplication {

  /**
   * Main method used to run the Spring Boot application.
   *
   * @param args command-line arguments passed to the application
   */
  public static void main(String[] args) {
    SpringApplication.run(CvManagementApplication.class, args);

    log.info("CV Management Application started successfully.");
  }
}
