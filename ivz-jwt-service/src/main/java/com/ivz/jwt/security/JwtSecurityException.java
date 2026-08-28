package com.ivz.jwt.security;

public class JwtSecurityException extends RuntimeException {
  public JwtSecurityException(String message, Throwable cause) {
    super(message, cause);
  }
}