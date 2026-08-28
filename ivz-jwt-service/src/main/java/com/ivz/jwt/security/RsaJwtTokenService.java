package com.ivz.jwt.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public final class RsaJwtTokenService implements JwtTokenService {
  private final JwtSecurityConfig config;

  public RsaJwtTokenService(JwtSecurityConfig config) {
    this.config = Objects.requireNonNull(config, "config is required");
  }

  @Override
  public String createToken(String subject) {
    return createToken(subject, Map.of());
  }

  @Override
  public String createToken(String subject, Map<String, Object> claims) {
    validateSubject(subject);
    validateClaims(claims);
    validatePrivateKey();
    Instant now = config.getClock().instant();
    JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
      .subject(subject)
      .issuer(config.getIssuer())
      .issueTime(Date.from(now))
      .expirationTime(Date.from(now.plus(config.getTokenLifetime())));
    for (Map.Entry<String, Object> claim : claims.entrySet()) {
      claimsBuilder.claim(claim.getKey(), claim.getValue());
    }
    SignedJWT signedJwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).build(), claimsBuilder.build());
    try {
      signedJwt.sign(new RSASSASigner(config.getPrivateKey()));
      return signedJwt.serialize();
    } catch (JOSEException exception) {
      throw new JwtSecurityException("Failed to create JWT token", exception);
    }
  }

  @Override
  public JwtTokenValidationResult validateToken(String token) {
    if (token == null || token.isBlank()) {
      return JwtTokenValidationResult.invalid("token is required");
    }
    try {
      SignedJWT signedJwt = SignedJWT.parse(token);
      if (!JWSAlgorithm.RS256.equals(signedJwt.getHeader().getAlgorithm())) {
        return JwtTokenValidationResult.invalid("unsupported JWT algorithm");
      }
      if (!signedJwt.verify(new RSASSAVerifier(config.getPublicKey()))) {
        return JwtTokenValidationResult.invalid("invalid JWT signature");
      }
      JWTClaimsSet claimsSet = signedJwt.getJWTClaimsSet();
      if (!config.getIssuer().equals(claimsSet.getIssuer())) {
        return JwtTokenValidationResult.invalid("invalid JWT issuer");
      }
      if (claimsSet.getExpirationTime() == null) {
        return JwtTokenValidationResult.invalid("JWT expiration is missing");
      }
      Instant expirationTime = claimsSet.getExpirationTime().toInstant();
      if (config.getClock().instant().isAfter(expirationTime)) {
        return JwtTokenValidationResult.invalid("JWT token is expired");
      }
      Instant issueTime = claimsSet.getIssueTime() == null ? null : claimsSet.getIssueTime().toInstant();
      return JwtTokenValidationResult.valid(claimsSet.getSubject(), claimsSet.getClaims(), issueTime, expirationTime);
    } catch (ParseException | JOSEException exception) {
      return JwtTokenValidationResult.invalid("Failed to validate JWT token: " + exception.getMessage());
    }
  }

  private void validateSubject(String subject) {
    Objects.requireNonNull(subject, "subject is required");
    if (subject.isBlank()) {
      throw new IllegalArgumentException("subject is required");
    }
  }

  private void validateClaims(Map<String, Object> claims) {
    Objects.requireNonNull(claims, "claims are required");
    for (String claimName : claims.keySet()) {
      if (claimName == null || claimName.isBlank()) {
        throw new IllegalArgumentException("claim name is required");
      }
      if (isRegisteredClaim(claimName)) {
        throw new IllegalArgumentException("claim name is reserved: " + claimName);
      }
    }
  }

  private void validatePrivateKey() {
    if (config.getPrivateKey() == null) {
      throw new IllegalStateException("privateKey is required to create JWT token");
    }
  }

  private boolean isRegisteredClaim(String claimName) {
    return "sub".equals(claimName)
      || "iss".equals(claimName)
      || "iat".equals(claimName)
      || "exp".equals(claimName)
      || "nbf".equals(claimName)
      || "jti".equals(claimName);
  }
}