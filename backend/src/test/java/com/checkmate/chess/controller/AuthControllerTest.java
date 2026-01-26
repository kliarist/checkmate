package com.checkmate.chess.controller;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.chess.dto.AuthResponse;
import com.checkmate.chess.dto.LoginRequest;
import com.checkmate.chess.dto.RegisterRequest;
import com.checkmate.chess.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

  @Autowired
  private AuthController authController;

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
  }

  @Test
  void shouldRegisterNewUser() {
    // Given
    final var request = new RegisterRequest();
    request.setEmail("test@example.com");
    request.setPassword("password123");
    request.setUsername("TestUser");

    // When
    final var response = authController.register(request);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    final var body = (AuthResponse) response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.getToken()).isNotNull();
    assertThat(body.getEmail()).isEqualTo("test@example.com");
    assertThat(body.getUsername()).isEqualTo("TestUser");

    // Verify user was saved
    final var users = userRepository.findAll();
    assertThat(users).hasSize(1);
    assertThat(users.get(0).getEmail()).isEqualTo("test@example.com");
  }

  @Test
  void shouldRejectDuplicateEmail() {
    // Given
    final var request1 = new RegisterRequest();
    request1.setEmail("test@example.com");
    request1.setPassword("password123");
    request1.setUsername("TestUser1");

    final var request2 = new RegisterRequest();
    request2.setEmail("test@example.com");
    request2.setPassword("password456");
    request2.setUsername("TestUser2");

    // Register first user
    authController.register(request1);

    // When - Try to register with same email
    final var response = authController.register(request2);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void shouldLoginWithValidCredentials() {
    // Given - Register a user first
    final var registerRequest = new RegisterRequest();
    registerRequest.setEmail("test@example.com");
    registerRequest.setPassword("password123");
    registerRequest.setUsername("TestUser");
    authController.register(registerRequest);

    final var loginRequest = new LoginRequest();
    loginRequest.setEmail("test@example.com");
    loginRequest.setPassword("password123");

    // When
    final var response = authController.login(loginRequest);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final var body = (AuthResponse) response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.getToken()).isNotNull();
    assertThat(body.getEmail()).isEqualTo("test@example.com");
  }

  @Test
  void shouldRejectInvalidPassword() {
    // Given - Register a user first
    final var registerRequest = new RegisterRequest();
    registerRequest.setEmail("test@example.com");
    registerRequest.setPassword("password123");
    registerRequest.setUsername("TestUser");
    authController.register(registerRequest);

    final var loginRequest = new LoginRequest();
    loginRequest.setEmail("test@example.com");
    loginRequest.setPassword("wrongpassword");

    // When
    final var response = authController.login(loginRequest);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldRejectNonExistentUser() {
    // Given
    final var loginRequest = new LoginRequest();
    loginRequest.setEmail("nonexistent@example.com");
    loginRequest.setPassword("password123");

    // When
    final var response = authController.login(loginRequest);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }
}
