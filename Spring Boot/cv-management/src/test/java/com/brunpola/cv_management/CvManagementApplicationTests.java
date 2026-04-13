package com.brunpola.cv_management;

import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CvManagementApplicationTests {

  @Test
  void contextLoads() {}

  @Test
  void mainMethodRuns() {
    try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
      CvManagementApplication.main(new String[] {});
      mocked.verify(() -> SpringApplication.run(CvManagementApplication.class, new String[] {}));
    }
  }
}
