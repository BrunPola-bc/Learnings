package com.brunpola.people_service;

import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PeopleServiceApplicationTests {

  @Test
  void contextLoads() {}

  @Test
  void mainMethodRuns() {
    try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
      PeopleServiceApplication.main(new String[] {});
      mocked.verify(() -> SpringApplication.run(PeopleServiceApplication.class, new String[] {}));
    }
  }
}
