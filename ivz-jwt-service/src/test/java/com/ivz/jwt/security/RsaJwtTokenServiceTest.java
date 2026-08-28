package com.ivz.jwt.security;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RsaJwtTokenServiceTest {
  private static final RSAPrivateKey TEST_PRIVATE_KEY = readPrivateKey("jwt-private-key.pem");
  private static final RSAPublicKey TEST_PUBLIC_KEY = readPublicKey("jwt-public-key.pem");

  @Test
  void shouldCreateAndValidateToken() {
    Instant now = Instant.parse("2026-08-23T12:00:00Z");
    JwtSecurityConfig config = new JwtSecurityConfig(TEST_PRIVATE_KEY, TEST_PUBLIC_KEY, "ivz", Duration.ofMinutes(15), Clock.fixed(now, ZoneOffset.UTC));
    JwtTokenService tokenService = new RsaJwtTokenService(config);

    String token = tokenService.createToken("vishnu", Map.of("role", "admin", "clientId", "core"));
    JwtTokenValidationResult result = tokenService.validateToken(token);

    Assertions.assertTrue(result.isValid());
    Assertions.assertEquals("vishnu", result.getSubject());
    Assertions.assertEquals("admin", result.getClaims().get("role"));
    Assertions.assertEquals("core", result.getClaims().get("clientId"));
    Assertions.assertEquals(now, result.getIssuedAt());
    Assertions.assertEquals(now.plus(Duration.ofMinutes(15)), result.getExpiresAt());
  }

  @Test
  void shouldRejectExpiredToken() {
    Instant issuedAt = Instant.parse("2026-08-23T12:00:00Z");
    JwtSecurityConfig signingConfig = new JwtSecurityConfig(TEST_PRIVATE_KEY, TEST_PUBLIC_KEY, "ivz", Duration.ofMinutes(1), Clock.fixed(issuedAt, ZoneOffset.UTC));
    JwtTokenService createTokenService = new RsaJwtTokenService(signingConfig);
    JwtSecurityConfig validationConfig = new JwtSecurityConfig(TEST_PUBLIC_KEY, "ivz", Duration.ofMinutes(1),
      Clock.fixed(issuedAt.plus(Duration.ofMinutes(2)), ZoneOffset.UTC));
    JwtTokenService validateTokenService = new RsaJwtTokenService(validationConfig);

    String token = createTokenService.createToken("vishnu");
    JwtTokenValidationResult result = validateTokenService.validateToken(token);

    Assertions.assertFalse(result.isValid());
    Assertions.assertEquals("JWT token is expired", result.getFailureMessage());
  }

  @Test
  void shouldRejectTokenWithWrongIssuer() {
    Instant now = Instant.parse("2026-08-23T12:00:00Z");
    JwtTokenService createTokenService = new RsaJwtTokenService(
      new JwtSecurityConfig(TEST_PRIVATE_KEY, TEST_PUBLIC_KEY, "issuer-a", Duration.ofMinutes(5), Clock.fixed(now, ZoneOffset.UTC)));
    JwtTokenService validateTokenService = new RsaJwtTokenService(
      new JwtSecurityConfig(TEST_PUBLIC_KEY, "issuer-b", Duration.ofMinutes(5), Clock.fixed(now, ZoneOffset.UTC)));

    String token = createTokenService.createToken("vishnu");
    JwtTokenValidationResult result = validateTokenService.validateToken(token);

    Assertions.assertFalse(result.isValid());
    Assertions.assertEquals("invalid JWT issuer", result.getFailureMessage());
  }

  @Test
  void shouldRejectTokenWithWrongKey() throws Exception {
    Instant now = Instant.parse("2026-08-23T12:00:00Z");
    KeyPair validationKeyPair = generateKeyPair();
    JwtTokenService createTokenService = new RsaJwtTokenService(
      new JwtSecurityConfig(TEST_PRIVATE_KEY, TEST_PUBLIC_KEY, "ivz", Duration.ofMinutes(5), Clock.fixed(now, ZoneOffset.UTC)));
    JwtTokenService validateTokenService = new RsaJwtTokenService(
      new JwtSecurityConfig((RSAPublicKey) validationKeyPair.getPublic(), "ivz", Duration.ofMinutes(5), Clock.fixed(now, ZoneOffset.UTC)));

    String token = createTokenService.createToken("vishnu");
    JwtTokenValidationResult result = validateTokenService.validateToken(token);

    Assertions.assertFalse(result.isValid());
    Assertions.assertEquals("invalid JWT signature", result.getFailureMessage());
  }

  @Test
  void shouldRejectReservedClaimNames() {
    JwtTokenService tokenService = new RsaJwtTokenService(
      new JwtSecurityConfig(TEST_PRIVATE_KEY, TEST_PUBLIC_KEY, "ivz", Duration.ofMinutes(5)));

    IllegalArgumentException exception = Assertions.assertThrows(
      IllegalArgumentException.class,
      () -> tokenService.createToken("vishnu", Map.of("sub", "other-subject")));

    Assertions.assertEquals("claim name is reserved: sub", exception.getMessage());
  }

  private static RSAPrivateKey readPrivateKey(String resourceName) {
    try {
      String pem = readPem(resourceName);
      String normalizedPem = pem.replace("-----BEGIN PRIVATE KEY-----", "")
        .replace("-----END PRIVATE KEY-----", "")
        .replaceAll("\\s", "");
      byte[] keyBytes = Base64.getDecoder().decode(normalizedPem);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      return (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    } catch (IOException | GeneralSecurityException exception) {
      throw new IllegalStateException("Failed to read private key: " + resourceName, exception);
    }
  }

  private static RSAPublicKey readPublicKey(String resourceName) {
    try {
      String pem = readPem(resourceName);
      String normalizedPem = pem.replace("-----BEGIN PUBLIC KEY-----", "")
        .replace("-----END PUBLIC KEY-----", "")
        .replaceAll("\\s", "");
      byte[] keyBytes = Base64.getDecoder().decode(normalizedPem);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      return (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
    } catch (IOException | GeneralSecurityException exception) {
      throw new IllegalStateException("Failed to read public key: " + resourceName, exception);
    }
  }

  private static String readPem(String resourceName) throws IOException {
    try (InputStream inputStream = RsaJwtTokenServiceTest.class.getClassLoader().getResourceAsStream(resourceName)) {
      if (inputStream == null) {
        throw new IllegalStateException("Missing test resource: " + resourceName);
      }
      return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  private static KeyPair generateKeyPair() throws Exception {
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    keyPairGenerator.initialize(2048);
    return keyPairGenerator.generateKeyPair();
  }
}