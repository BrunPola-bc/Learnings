package com.brunpola.api_gateway;

import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiGatewayApplicationTests {

  @Test
  void contextLoads() {}

  @Test
  void mainMethodRuns() {
    try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
      ApiGatewayApplication.main(new String[] {});
      mocked.verify(() -> SpringApplication.run(ApiGatewayApplication.class, new String[] {}));
    }
  }
}
