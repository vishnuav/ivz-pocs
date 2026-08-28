package com.ivz.p2iws.auth;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties(prefix = "ivz.auth")
public class IvzAuthProperties {
  private String clientId;
  private String clientSecret;
  private String issuer;
  private Duration tokenLifetime = Duration.ofMinutes(15);
  private Resource privateKeyLocation;
  private Resource publicKeyLocation;

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  public String getIssuer() {
    return issuer;
  }

  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  public Duration getTokenLifetime() {
    return tokenLifetime;
  }

  public void setTokenLifetime(Duration tokenLifetime) {
    this.tokenLifetime = tokenLifetime;
  }

  public Resource getPrivateKeyLocation() {
    return privateKeyLocation;
  }

  public void setPrivateKeyLocation(Resource privateKeyLocation) {
    this.privateKeyLocation = privateKeyLocation;
  }

  public Resource getPublicKeyLocation() {
    return publicKeyLocation;
  }

  public void setPublicKeyLocation(Resource publicKeyLocation) {
    this.publicKeyLocation = publicKeyLocation;
  }
}