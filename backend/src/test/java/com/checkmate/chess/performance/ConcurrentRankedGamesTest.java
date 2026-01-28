package com.checkmate.chess.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.checkmate.chess.model.Game;
import com.checkmate.chess.model.GameClock;
import com.checkmate.chess.model.User;
import com.checkmate.chess.repository.GameClockRepository;
import com.checkmate.chess.repository.GameRepository;
import com.checkmate.chess.repository.UserRepository;
import com.checkmate.chess.service.ChessClockService;
import com.checkmate.chess.service.MatchmakingService;
import com.checkmate.chess.service.RatingService;

/**
 * Load test for concurrent ranked games.
 * Verifies system can handle 100 concurrent games with clocks.
 */
@SpringBootTest
@ActiveProfiles("test")
class ConcurrentRankedGamesTest {

  @Autowired
  private GameRepository gameRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private GameClockRepository clockRepository;

  @Autowired
  private ChessClockService clockService;

  @Autowired
  private RatingService ratingService;

  @Autowired
  private MatchmakingService matchmakingService;

  @Test
  @DisplayName("System should handle 100 concurrent ranked games")
  void testConcurrentRankedGames() throws InterruptedException {
    int gameCount = 100;
    ExecutorService executor = Executors.newFixedThreadPool(20);
    CountDownLatch latch = new CountDownLatch(gameCount);
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger errorCount = new AtomicInteger(0);

    long startTime = System.currentTimeMillis();

    // Create 100 games concurrently
    for (int i = 0; i < gameCount; i++) {
      final int gameIndex = i;
      executor.submit(() -> {
        try {
          // Create two users
          User white = createTestUser("white_" + gameIndex, 1500 + gameIndex);
          User black = createTestUser("black_" + gameIndex, 1500 + gameIndex);

          // Create game
          Game game = new Game();
          game.setWhitePlayer(white);
          game.setBlackPlayer(black);
          game.setGameType("ranked");
          game.setTimeControl("blitz");
          game.setCurrentFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
          game.setStatus("in_progress");
          game = gameRepository.save(game);

          // Initialize clock
          clockService.initializeClock(game.getId(), "blitz");

          successCount.incrementAndGet();
        } catch (Exception e) {
          errorCount.incrementAndGet();
          System.err.println("Error creating game " + gameIndex + ": " + e.getMessage());
        } finally {
          latch.countDown();
        }
      });
    }

    // Wait for all games to be created
    boolean completed = latch.await(30, TimeUnit.SECONDS);
    executor.shutdown();

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    // Print results
    System.out.println("\n=== Concurrent Ranked Games Load Test ===");
    System.out.println("Total games: " + gameCount);
    System.out.println("Successful: " + successCount.get());
    System.out.println("Errors: " + errorCount.get());
    System.out.println("Duration: " + duration + "ms");
    System.out.println("Avg time per game: " + (duration / gameCount) + "ms");
    System.out.println("Games per second: " + (gameCount * 1000.0 / duration));

    // Verify results
    org.junit.jupiter.api.Assertions.assertTrue(completed, "All games should complete within 30 seconds");
    org.junit.jupiter.api.Assertions.assertTrue(
        successCount.get() >= 95,
        "At least 95% of games should succeed, but only " + successCount.get() + " succeeded"
    );
    org.junit.jupiter.api.Assertions.assertTrue(
        duration < 30000,
        "Should complete in <30 seconds, but took " + duration + "ms"
    );

    // Verify clocks were created
    long clockCount = clockRepository.count();
    System.out.println("Clocks created: " + clockCount);
    org.junit.jupiter.api.Assertions.assertTrue(
        clockCount >= 95,
        "At least 95 clocks should be created"
    );
  }

  @Test
  @DisplayName("System should handle concurrent rating updates")
  void testConcurrentRatingUpdates() throws InterruptedException {
    int updateCount = 100;
    ExecutorService executor = Executors.newFixedThreadPool(20);
    CountDownLatch latch = new CountDownLatch(updateCount);
    AtomicInteger successCount = new AtomicInteger(0);

    // Create test users
    User player1 = createTestUser("concurrent_player1", 1500);
    User player2 = createTestUser("concurrent_player2", 1500);

    long startTime = System.currentTimeMillis();

    // Perform 100 concurrent rating updates
    for (int i = 0; i < updateCount; i++) {
      final String result = (i % 3 == 0) ? "white" : (i % 3 == 1) ? "black" : "draw";
      executor.submit(() -> {
        try {
          ratingService.updateRatings(player1.getId(), player2.getId(), result);
          successCount.incrementAndGet();
        } catch (Exception e) {
          System.err.println("Error updating ratings: " + e.getMessage());
        } finally {
          latch.countDown();
        }
      });
    }

    boolean completed = latch.await(30, TimeUnit.SECONDS);
    executor.shutdown();

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    System.out.println("\n=== Concurrent Rating Updates Test ===");
    System.out.println("Total updates: " + updateCount);
    System.out.println("Successful: " + successCount.get());
    System.out.println("Duration: " + duration + "ms");
    System.out.println("Updates per second: " + (updateCount * 1000.0 / duration));

    org.junit.jupiter.api.Assertions.assertTrue(completed, "All updates should complete within 30 seconds");
    org.junit.jupiter.api.Assertions.assertTrue(
        successCount.get() >= 95,
        "At least 95% of updates should succeed"
    );
  }

  @Test
  @DisplayName("System should handle concurrent matchmaking requests")
  void testConcurrentMatchmaking() throws InterruptedException {
    int playerCount = 100;
    ExecutorService executor = Executors.newFixedThreadPool(20);
    CountDownLatch latch = new CountDownLatch(playerCount);
    AtomicInteger successCount = new AtomicInteger(0);

    long startTime = System.currentTimeMillis();

    // Create users and join matchmaking queue
    List<User> users = new ArrayList<>();
    for (int i = 0; i < playerCount; i++) {
      User user = createTestUser("matchmaking_player_" + i, 1500 + (i * 10));
      users.add(user);
    }

    // Join queue concurrently
    for (User user : users) {
      executor.submit(() -> {
        try {
          matchmakingService.joinQueue(user.getId(), "blitz");
          successCount.incrementAndGet();
        } catch (Exception e) {
          System.err.println("Error joining queue: " + e.getMessage());
        } finally {
          latch.countDown();
        }
      });
    }

    boolean completed = latch.await(30, TimeUnit.SECONDS);
    executor.shutdown();

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    System.out.println("\n=== Concurrent Matchmaking Test ===");
    System.out.println("Total players: " + playerCount);
    System.out.println("Successful joins: " + successCount.get());
    System.out.println("Duration: " + duration + "ms");

    org.junit.jupiter.api.Assertions.assertTrue(completed, "All joins should complete within 30 seconds");
    org.junit.jupiter.api.Assertions.assertTrue(
        successCount.get() >= 95,
        "At least 95% of joins should succeed"
    );

    // Process pairing
    matchmakingService.processPairing();

    // Verify games were created
    long gameCount = gameRepository.count();
    System.out.println("Games created from pairing: " + gameCount);
    org.junit.jupiter.api.Assertions.assertTrue(
        gameCount >= 40, // At least 40 pairs from 100 players
        "Should create at least 40 games from 100 players"
    );
  }

  @Test
  @DisplayName("Clock updates should handle 100 concurrent games efficiently")
  void testClockUpdatesUnderLoad() throws InterruptedException {
    int gameCount = 100;

    // Create 100 games with clocks
    List<GameClock> clocks = new ArrayList<>();
    for (int i = 0; i < gameCount; i++) {
      UUID gameId = UUID.randomUUID();
      GameClock clock = new GameClock(gameId, 300000L, 2000L, 0L);
      clock.setPaused(false);
      clocks.add(clockRepository.save(clock));
    }

    // Measure clock update performance
    List<Long> latencies = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      long startTime = System.nanoTime();
      
      // Simulate clock updates for all games
      for (GameClock clock : clocks) {
        clockService.checkTimeout(clock.getGameId());
      }
      
      long endTime = System.nanoTime();
      long latencyMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
      latencies.add(latencyMs);
      
      Thread.sleep(100);
    }

    long avgLatency = (long) latencies.stream().mapToLong(Long::longValue).average().orElse(0);

    System.out.println("\n=== Clock Updates Under Load ===");
    System.out.println("Games: " + gameCount);
    System.out.println("Avg latency: " + avgLatency + "ms");
    System.out.println("Latency per game: " + (avgLatency / gameCount) + "ms");

    org.junit.jupiter.api.Assertions.assertTrue(
        avgLatency < 1000,
        "Average latency should be <1000ms for 100 games, but was " + avgLatency + "ms"
    );

    // Cleanup
    clockRepository.deleteAll(clocks);
  }

  private User createTestUser(String username, int rating) {
    User user = new User();
    user.setUsername(username);
    user.setEmail(username + "@test.com");
    user.setPasswordHash("test");
    user.setEloRating(rating);
    user.setGamesPlayed(0);
    user.setWins(0);
    user.setLosses(0);
    user.setDraws(0);
    return userRepository.save(user);
  }
}
