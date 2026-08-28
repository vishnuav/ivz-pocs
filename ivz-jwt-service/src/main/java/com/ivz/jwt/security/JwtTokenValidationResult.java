package com.ivz.jwt.security;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class JwtTokenValidationResult {
  private final boolean valid;
  private final String subject;
  private final Map<String, Object> claims;
  private final Instant issuedAt;
  private final Instant expiresAt;
  private final String failureMessage;

  private JwtTokenValidationResult(boolean valid, String subject, Map<String, Object> claims, Instant issuedAt, Instant expiresAt,
                                   String failureMessage) {
    this.valid = valid;
    this.subject = subject;
    this.claims = Collections.unmodifiableMap(new LinkedHashMap<>(Objects.requireNonNull(claims, "claims are required")));
    this.issuedAt = issuedAt;
    this.expiresAt = expiresAt;
    this.failureMessage = failureMessage;
  }

  public static JwtTokenValidationResult valid(String subject, Map<String, Object> claims, Instant issuedAt, Instant expiresAt) {
    return new JwtTokenValidationResult(true, subject, claims, issuedAt, expiresAt, null);
  }

  public static JwtTokenValidationResult invalid(String failureMessage) {
    return new JwtTokenValidationResult(false, null, Map.of(), null, null,
      Objects.requireNonNull(failureMessage, "failureMessage is required"));
  }

  public boolean isValid() {
    return valid;
  }

  public String getSubject() {
    return subject;
  }

  public Map<String, Object> getClaims() {
    return claims;
  }

  public Instant getIssuedAt() {
    return issuedAt;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public String getFailureMessage() {
    return failureMessage;
  }
}