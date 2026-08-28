package com.ivz.p2iws.auth;

import java.time.Instant;

public final class AuthTokenResponse {
  private final String token;
  private final String tokenType;
  private final Instant expiresAt;

  public AuthTokenResponse(String token, String tokenType, Instant expiresAt) {
    this.token = token;
    this.tokenType = tokenType;
    this.expiresAt = expiresAt;
  }

  public String getToken() {
    return token;
  }

  public String getTokenType() {
    return tokenType;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }
}