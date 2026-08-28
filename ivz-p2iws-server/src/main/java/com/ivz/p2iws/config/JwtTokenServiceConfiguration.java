package com.ivz.p2iws.config;

import com.ivz.jwt.security.JwtSecurityConfig;
import com.ivz.jwt.security.JwtTokenService;
import com.ivz.jwt.security.RsaJwtTokenService;
import com.ivz.p2iws.auth.IvzAuthProperties;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class JwtTokenServiceConfiguration {
  @Bean
  public JwtTokenService jwtTokenService(IvzAuthProperties properties) {
    RSAPrivateKey privateKey = readPrivateKey(properties.getPrivateKeyLocation());
    RSAPublicKey publicKey = readPublicKey(properties.getPublicKeyLocation());
    JwtSecurityConfig config = new JwtSecurityConfig(privateKey, publicKey, properties.getIssuer(), properties.getTokenLifetime());
    return new RsaJwtTokenService(config);
  }

  private RSAPrivateKey readPrivateKey(Resource resource) {
    try {
      String pem = readPem(resource);
      String normalizedPem = pem.replace("-----BEGIN PRIVATE KEY-----", "")
        .replace("-----END PRIVATE KEY-----", "")
        .replaceAll("\\s", "");
      byte[] keyBytes = Base64.getDecoder().decode(normalizedPem);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      return (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    } catch (IOException | GeneralSecurityException exception) {
      throw new IllegalStateException("Failed to read private key from " + resource.getDescription(), exception);
    }
  }

  private RSAPublicKey readPublicKey(Resource resource) {
    try {
      String pem = readPem(resource);
      String normalizedPem = pem.replace("-----BEGIN PUBLIC KEY-----", "")
        .replace("-----END PUBLIC KEY-----", "")
        .replaceAll("\\s", "");
      byte[] keyBytes = Base64.getDecoder().decode(normalizedPem);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      return (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
    } catch (IOException | GeneralSecurityException exception) {
      throw new IllegalStateException("Failed to read public key from " + resource.getDescription(), exception);
    }
  }

  private String readPem(Resource resource) throws IOException {
    try (InputStream inputStream = resource.getInputStream()) {
      return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
  }
}