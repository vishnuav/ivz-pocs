package com.ivz.p2iws.health;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HealthControllerTest {
  @Autowired
  private TestRestTemplate testRestTemplate;

  @Test
  void shouldReturnUpHealthStatus() {
    ResponseEntity<String> responseEntity = testRestTemplate.getForEntity("/health", String.class);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals("{\"status\":\"UP\"}", responseEntity.getBody());
  }
}