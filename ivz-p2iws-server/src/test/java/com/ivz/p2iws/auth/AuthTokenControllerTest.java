package com.ivz.p2iws.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivz.jwt.security.JwtTokenService;
import com.ivz.jwt.security.JwtTokenValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthTokenControllerTest {
  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private JwtTokenService jwtTokenService;

  @BeforeEach
  void setUp() {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setOutputStreaming(false);
    testRestTemplate.getRestTemplate().setRequestFactory(requestFactory);
  }

  @Test
  void shouldReturnJwtTokenForValidClientCredentials() throws Exception {
    AuthTokenRequest request = new AuthTokenRequest();
    request.setClientId("demo-client");
    request.setClientSecret("demo-secret");
    request.setActorId("actor-123");
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> httpEntity = new HttpEntity<>(objectMapper.writeValueAsString(request), httpHeaders);

    ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/auth/token", httpEntity, String.class);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    JsonNode response = objectMapper.readTree(responseEntity.getBody());
    Assertions.assertEquals("Bearer", response.get("tokenType").asText());
    Assertions.assertFalse(response.get("token").asText().isBlank());
    JwtTokenValidationResult validationResult = jwtTokenService.validateToken(response.get("token").asText());

    Assertions.assertTrue(validationResult.isValid());
    Assertions.assertEquals("actor-123", validationResult.getSubject());
    Assertions.assertEquals("demo-client", validationResult.getClaims().get("clientId"));
    Assertions.assertEquals(response.get("expiresAt").asText(), validationResult.getExpiresAt().toString());
  }

  @Test
  void shouldRejectInvalidClientCredentials() throws Exception {
    AuthTokenRequest request = new AuthTokenRequest();
    request.setClientId("demo-client");
    request.setClientSecret("wrong-secret");
    request.setActorId("actor-123");
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> httpEntity = new HttpEntity<>(objectMapper.writeValueAsString(request), httpHeaders);

    ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/auth/token", httpEntity, String.class);

    Assertions.assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
  }
}