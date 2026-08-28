package com.ivz.common.model.jwt;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtClaims {
  private String iss, jti, sub;
  private long iat, exp, nbf;
  private List<String> aud;
  private Map<String, Object> additionalClaims = new LinkedHashMap<>();

  public void setAdditionalClaims(Map<String, Object> additionalClaims) {
    this.additionalClaims = additionalClaims == null ? new LinkedHashMap<>() : new LinkedHashMap<>(additionalClaims);
  }
}