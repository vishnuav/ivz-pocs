package com.ivz.jwt.security;

import java.util.Map;

public interface JwtTokenService {
  String createToken(String subject);

  String createToken(String subject, Map<String, Object> claims);

  JwtTokenValidationResult validateToken(String token);
}