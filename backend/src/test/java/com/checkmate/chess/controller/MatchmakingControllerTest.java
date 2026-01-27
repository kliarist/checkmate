package com.checkmate.chess.controller;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.chess.model.User;
import com.checkmate.chess.repository.MatchmakingQueueRepository;
import com.checkmate.chess.repository.UserRepository;

/**
 * Integration tests for MatchmakingController.
 * Tests matchmaking queue endpoints.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Matchmaking Controller Integration Tests")
class MatchmakingControllerTest {

  @Autowired
  private MatchmakingController matchmakingController;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MatchmakingQueueRepository queueRepository;

  private User testUser;

  @BeforeEach
  void setUp() {
    queueRepository.deleteAll();
    userRepository.deleteAll();

    testUser = new User();
    testUser.setEmail("test@example.com");
    testUser.setUsername("testuser");
    testUser.setPasswordHash("hashedpassword");
    testUser.setEloRating(1500);
    testUser = userRepository.save(testUser);
  }

  @Test
  @DisplayName("Should join matchmaking queue successfully")
  void testJoinQueue() {
    // When
    var response = matchmakingController.joinQueue(testUser.getId(), "blitz");

    // Then
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getBody().message()).isEqualTo("Successfully joined matchmaking queue");

    // Verify queue entry created
    var queueEntry = queueRepository.findByUserId(testUser.getId());
    assertThat(queueEntry).isPresent();
    assertThat(queueEntry.get().getTimeControl()).isEqualTo("blitz");
  }

  @Test
  @DisplayName("Should leave matchmaking queue successfully")
  void testLeaveQueue() {
    // Given: User in queue
    matchmakingController.joinQueue(testUser.getId(), "blitz");

    // When
    var response = matchmakingController.leaveQueue(testUser.getId());

    // Then
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getBody().message()).isEqualTo("Successfully left matchmaking queue");

    // Verify queue entry removed
    var queueEntry = queueRepository.findByUserId(testUser.getId());
    assertThat(queueEntry).isEmpty();
  }

  @Test
  @DisplayName("Should replace existing queue entry when joining again")
  void testReplaceQueueEntry() {
    // Given: User in blitz queue
    matchmakingController.joinQueue(testUser.getId(), "blitz");

    // When: Join rapid queue
    matchmakingController.joinQueue(testUser.getId(), "rapid");

    // Then: Only rapid queue entry exists
    var queueEntry = queueRepository.findByUserId(testUser.getId());
    assertThat(queueEntry).isPresent();
    assertThat(queueEntry.get().getTimeControl()).isEqualTo("rapid");
  }

  @Test
  @DisplayName("Should store correct rating in queue")
  void testQueueStoresRating() {
    // When
    matchmakingController.joinQueue(testUser.getId(), "blitz");

    // Then
    var queueEntry = queueRepository.findByUserId(testUser.getId());
    assertThat(queueEntry).isPresent();
    assertThat(queueEntry.get().getRating()).isEqualTo(1500);
  }
}
