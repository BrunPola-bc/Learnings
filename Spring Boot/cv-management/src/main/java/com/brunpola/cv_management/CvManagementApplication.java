package com.brunpola.cv_management;

import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log
public class CvManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(CvManagementApplication.class, args);

    log.info("CV Management Application started successfully.");
  }
}
