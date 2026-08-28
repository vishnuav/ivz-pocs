package com.ivz.p2iws.auth;

public interface AuthTokenService {
  AuthTokenResponse createToken(AuthTokenRequest request);
}