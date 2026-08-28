package com.ivz.p2iws.order;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerTest {
  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldReturnOrderStatus() throws Exception {
    ResponseEntity<String> responseEntity = testRestTemplate.getForEntity("/api/orders/status?orderId=XXXXX", String.class);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    JsonNode response = objectMapper.readTree(responseEntity.getBody());

    Assertions.assertEquals("XXXXX", response.get("orderId").asText());
    Assertions.assertEquals("Open", response.get("status").asText());
  }
}