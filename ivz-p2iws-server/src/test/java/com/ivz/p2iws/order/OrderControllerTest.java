package com.ivz.p2iws.order;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivz.jwt.security.JwtTokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerTest {
  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private JwtTokenService jwtTokenService;

  @Test
  void shouldRejectOrderStatusWithoutBearerToken() {
    ResponseEntity<String> responseEntity = testRestTemplate.getForEntity("/api/orders/status?orderId=XXXXX", String.class);

    Assertions.assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
  }

  @Test
  void shouldReturnOrderStatusWithBearerToken() throws Exception {
    String token = jwtTokenService.createToken("actor-123");
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setBearerAuth(token);
    HttpEntity<Void> httpEntity = new HttpEntity<>(httpHeaders);
    ResponseEntity<String> responseEntity =
      testRestTemplate.exchange("/api/orders/status?orderId=XXXXX", HttpMethod.GET, httpEntity, String.class);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    JsonNode response = objectMapper.readTree(responseEntity.getBody());

    Assertions.assertEquals("XXXXX", response.get("orderId").asText());
    Assertions.assertEquals("Open", response.get("status").asText());
  }
}