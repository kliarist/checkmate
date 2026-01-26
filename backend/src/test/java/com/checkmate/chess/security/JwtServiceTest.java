package com.checkmate.chess.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;

class JwtServiceTest {

  private JwtService jwtService;
  private UserDetails userDetails;

  @BeforeEach
  void setUp() {
    jwtService = new JwtService();
    // Set test values using reflection
    ReflectionTestUtils.setField(jwtService, "secret",
        "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
    ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L); // 24 hours

    userDetails = User.builder()
        .username("test@example.com")
        .password("password")
        .authorities("USER")
        .build();
  }

  @Test
  void shouldGenerateToken() {
    // When
    final var token = jwtService.generateToken(userDetails);

    // Then
    assertThat(token).isNotNull();
    assertThat(token).isNotEmpty();
    assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
  }

  @Test
  void shouldGenerateTokenWithExtraClaims() {
    // Given
    final Map<String, Object> extraClaims = new HashMap<>();
    extraClaims.put("role", "ADMIN");
    extraClaims.put("userId", "123");

    // When
    final var token = jwtService.generateToken(extraClaims, userDetails);

    // Then
    assertThat(token).isNotNull();
    assertThat(token).isNotEmpty();
  }

  @Test
  void shouldExtractUsername() {
    // Given
    final var token = jwtService.generateToken(userDetails);

    // When
    final var username = jwtService.extractUsername(token);

    // Then
    assertThat(username).isEqualTo("test@example.com");
  }

  @Test
  void shouldExtractExpiration() {
    // Given
    final var token = jwtService.generateToken(userDetails);

    // When
    final var expiration = jwtService.extractClaim(token, claims -> claims.getExpiration());

    // Then
    assertThat(expiration).isAfter(new Date());
    assertThat(expiration).isBefore(new Date(System.currentTimeMillis() + 86400001L));
  }

  @Test
  void shouldValidateValidToken() {
    // Given
    final var token = jwtService.generateToken(userDetails);

    // When
    final var isValid = jwtService.isTokenValid(token, userDetails);

    // Then
    assertThat(isValid).isTrue();
  }

  @Test
  void shouldRejectTokenWithWrongUsername() {
    // Given
    final var token = jwtService.generateToken(userDetails);
    final var differentUser = User.builder()
        .username("different@example.com")
        .password("password")
        .authorities("USER")
        .build();

    // When
    final var isValid = jwtService.isTokenValid(token, differentUser);

    // Then
    assertThat(isValid).isFalse();
  }

  @Test
  void shouldRejectExpiredToken() {
    // Given
    ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L); // Expired
    final var token = jwtService.generateToken(userDetails);

    // When/Then
    assertThatThrownBy(() -> jwtService.isTokenValid(token, userDetails))
        .isInstanceOf(ExpiredJwtException.class);
  }

  @Test
  void shouldRejectTokenWithInvalidSignature() {
    // Given
    final var token = jwtService.generateToken(userDetails);
    // Change the secret to invalidate signature
    ReflectionTestUtils.setField(jwtService, "secret",
        "DifferentSecretKey123456789012345678901234567890123456789012345678");

    // When/Then
    assertThatThrownBy(() -> jwtService.extractUsername(token))
        .isInstanceOf(SignatureException.class);
  }

  @Test
  void shouldExtractCustomClaim() {
    // Given
    final Map<String, Object> extraClaims = new HashMap<>();
    extraClaims.put("customField", "customValue");
    final var token = jwtService.generateToken(extraClaims, userDetails);

    // When
    final var customValue = jwtService.extractClaim(token, 
        claims -> claims.get("customField", String.class));

    // Then
    assertThat(customValue).isEqualTo("customValue");
  }

  @Test
  void shouldHandleNullExtraClaims() {
    // When
    final var token = jwtService.generateToken(userDetails);

    // Then
    assertThat(token).isNotNull();
    assertThat(jwtService.extractUsername(token)).isEqualTo("test@example.com");
  }
}
