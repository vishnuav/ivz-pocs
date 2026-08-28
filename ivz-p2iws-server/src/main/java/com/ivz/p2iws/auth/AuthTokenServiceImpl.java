package com.ivz.p2iws.auth;

import com.ivz.common.model.latency.LatencyMonitor;
import com.ivz.jwt.security.JwtTokenService;
import com.ivz.jwt.security.JwtTokenValidationResult;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthTokenServiceImpl implements AuthTokenService {
  private final IvzAuthProperties properties;
  private final JwtTokenService jwtTokenService;

  public AuthTokenServiceImpl(IvzAuthProperties properties, JwtTokenService jwtTokenService) {
    this.properties = Objects.requireNonNull(properties, "properties is required");
    this.jwtTokenService = Objects.requireNonNull(jwtTokenService, "jwtTokenService is required");
  }

  @Override
  public AuthTokenResponse createToken(AuthTokenRequest request) {
    long requestId = LatencyMonitor.monitor(0L, "AuthTokenServiceImpl.createToken", System.currentTimeMillis(), request.getActorId());
    validateCredentials(request);
    String token = jwtTokenService.createToken(request.getActorId(), Map.of("clientId", request.getClientId()));
    JwtTokenValidationResult validationResult = jwtTokenService.validateToken(token);
    LatencyMonitor.monitor(requestId, "AuthTokenServiceImpl.createToken", System.currentTimeMillis(), request.getActorId());
    return new AuthTokenResponse(token, "Bearer", validationResult.getExpiresAt());
  }

  private void validateCredentials(AuthTokenRequest request) {
    if (!Objects.equals(properties.getClientId(), request.getClientId())
        || !Objects.equals(properties.getClientSecret(), request.getClientSecret())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid client credentials");
    }
  }
}