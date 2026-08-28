package com.ivz.jwt.security;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Clock;
import java.time.Duration;
import java.util.Objects;

public final class JwtSecurityConfig {
  private final RSAPrivateKey privateKey;
  private final RSAPublicKey publicKey;
  private final String issuer;
  private final Duration tokenLifetime;
  private final Clock clock;

  public JwtSecurityConfig(RSAPublicKey publicKey, String issuer, Duration tokenLifetime, Clock clock) {
    this(null, publicKey, issuer, tokenLifetime, clock);
  }

  public JwtSecurityConfig(RSAPrivateKey privateKey, RSAPublicKey publicKey, String issuer, Duration tokenLifetime) {
    this(privateKey, publicKey, issuer, tokenLifetime, Clock.systemUTC());
  }

  public JwtSecurityConfig(RSAPrivateKey privateKey, RSAPublicKey publicKey, String issuer, Duration tokenLifetime, Clock clock) {
    this.privateKey = privateKey;
    this.publicKey = Objects.requireNonNull(publicKey, "publicKey is required");
    this.issuer = Objects.requireNonNull(issuer, "issuer is required");
    this.tokenLifetime = Objects.requireNonNull(tokenLifetime, "tokenLifetime is required");
    this.clock = Objects.requireNonNull(clock, "clock is required");
    if (issuer.isBlank()) {
      throw new IllegalArgumentException("issuer is required");
    }
    if (tokenLifetime.isZero() || tokenLifetime.isNegative()) {
      throw new IllegalArgumentException("tokenLifetime must be greater than zero");
    }
  }

  public RSAPrivateKey getPrivateKey() {
    return privateKey;
  }

  public RSAPublicKey getPublicKey() {
    return publicKey;
  }

  public String getIssuer() {
    return issuer;
  }

  public Duration getTokenLifetime() {
    return tokenLifetime;
  }

  public Clock getClock() {
    return clock;
  }
}