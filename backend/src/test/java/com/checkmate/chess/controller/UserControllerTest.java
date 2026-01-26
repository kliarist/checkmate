package com.checkmate.chess.controller;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.chess.dto.RegisterRequest;
import com.checkmate.chess.dto.UserProfileResponse;
import com.checkmate.chess.model.User;
import com.checkmate.chess.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

  @Autowired
  private UserController userController;

  @Autowired
  private AuthController authController;

  @Autowired
  private UserRepository userRepository;

  private User testUser;
  private Authentication authentication;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();

    // Register a test user
    final var registerRequest = new RegisterRequest();
    registerRequest.setEmail("test@example.com");
    registerRequest.setPassword("password123");
    registerRequest.setUsername("TestUser");
    authController.register(registerRequest);

    testUser = userRepository.findByEmail("test@example.com").orElseThrow();

    // Create authentication object
    authentication = new UsernamePasswordAuthenticationToken(
        "test@example.com",
        null,
        List.of(new SimpleGrantedAuthority("ROLE_USER"))
    );
  }

  @Test
  void shouldGetCurrentUserProfile() {
    // When
    final var response = userController.getCurrentUser(authentication);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final var body = (UserProfileResponse) response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.getEmail()).isEqualTo("test@example.com");
    assertThat(body.getUsername()).isEqualTo("TestUser");
    assertThat(body.getUserId()).isEqualTo(testUser.getId());
  }

  @Test
  void shouldRejectUnauthenticatedRequest() {
    // When
    final var response = userController.getCurrentUser(null);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldGetUserGames() {
    // When
    final var response = userController.getUserGames(authentication, 0, 20);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void shouldGetUserStats() {
    // When
    final var response = userController.getUserStats(authentication);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void shouldRejectUnauthenticatedGamesRequest() {
    // When
    final var response = userController.getUserGames(null, 0, 20);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldRejectUnauthenticatedStatsRequest() {
    // When
    final var response = userController.getUserStats(null);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }
}
