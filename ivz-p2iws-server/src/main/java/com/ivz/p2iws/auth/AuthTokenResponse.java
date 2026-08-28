package com.ivz.p2iws.auth;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokenResponse {
  private String token;
  private String tokenType;
  private Instant expiresAt;
}