package com.ivz.p2iws.auth;

import com.ivz.common.model.latency.LatencyMonitor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthTokenController {
  private final AuthTokenService authTokenService;

  public AuthTokenController(AuthTokenService authTokenService) {
    this.authTokenService = authTokenService;
  }

  @PostMapping("/token")
  public ResponseEntity<AuthTokenResponse> createToken(@Valid @RequestBody AuthTokenRequest request) {
    long requestId = LatencyMonitor.monitor(0L, "AuthTokenController.createToken", System.currentTimeMillis(), request.getActorId());
    ResponseEntity<AuthTokenResponse> responseEntity = ResponseEntity.ok(authTokenService.createToken(request));
    LatencyMonitor.monitor(requestId, "AuthTokenController.createToken", System.currentTimeMillis(), request.getActorId());
    return responseEntity;
  }
}