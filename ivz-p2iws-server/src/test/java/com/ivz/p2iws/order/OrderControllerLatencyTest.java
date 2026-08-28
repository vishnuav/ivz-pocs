package com.ivz.p2iws.order;

import com.ivz.common.model.order.OrderStatus;
import com.ivz.common.model.order.OrderStatusResponse;
import com.ivz.p2iws.auth.AuthTokenRequest;
import com.ivz.p2iws.auth.AuthTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

@Slf4j
@Tag("latency")
class OrderControllerLatencyTest {
  private static final String LOCAL_BASE_URL = "http://localhost:6080";
  private static final int REQUEST_COUNT = 1000;
  private static final WebTestClient client = WebTestClient.bindToServer().baseUrl(LOCAL_BASE_URL).responseTimeout(Duration.ofSeconds(10)).build();

  @Test
  void shouldReturnOrderStatusForOneThousandLocalRequests() {
    assertLocalServerIsRunning();
    String bearerToken = createBearerToken();

    for (int orderId = 1; orderId <= REQUEST_COUNT; orderId++) {
      OrderStatusResponse result = client
        .get()
        .uri("/api/orders/status?orderId=" + orderId)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
        .exchange()
        .expectStatus().isOk()
        .expectBody(OrderStatusResponse.class)
        .returnResult()
        .getResponseBody();

      Assertions.assertNotNull(result);
      Assertions.assertEquals(String.valueOf(orderId), result.getOrderId());
      Assertions.assertEquals(OrderStatus.Open, result.getStatus());
      log.info("validated order id {} and status {}", result.getOrderId(), result.getStatus());
    }
  }

  private String createBearerToken() {
    AuthTokenRequest request = new AuthTokenRequest();
    request.setClientId("demo-client");
    request.setClientSecret("demo-secret");
    request.setActorId("latency-test");
    AuthTokenResponse result = client
      .post()
      .uri("/auth/token")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isOk()
      .expectBody(AuthTokenResponse.class)
      .returnResult()
      .getResponseBody();

    Assertions.assertNotNull(result);
    Assertions.assertNotNull(result.getToken());
    return result.getToken();
  }

  private void assertLocalServerIsRunning() {
    String result = client
      .get()
      .uri("/health")
      .exchange()
      .expectStatus().isOk()
      .expectBody(String.class)
      .returnResult()
      .getResponseBody();

    Assertions.assertEquals("{\"status\":\"UP\"}", result);
  }
}