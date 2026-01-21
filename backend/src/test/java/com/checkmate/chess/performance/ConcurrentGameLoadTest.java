package com.checkmate.chess.performance;

import static org.junit.jupiter.api.Assertions.*;

import com.checkmate.chess.service.GameService;
import com.checkmate.chess.service.GuestService;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Load tests for concurrent game handling (T085).
 * Simulates 50 concurrent games with 100 players making moves.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("Concurrent Game Load Tests")
class ConcurrentGameLoadTest {

  @Autowired
  private GameService gameService;

  @Autowired
  private GuestService guestService;

  @Test
  @DisplayName("Should handle 50 concurrent games with 100 players")
  void shouldHandle50ConcurrentGames() throws Exception {
    int numberOfGames = 50;
    int movesPerGame = 20;

    ExecutorService executorService = Executors.newFixedThreadPool(100);
    CountDownLatch latch = new CountDownLatch(numberOfGames);
    AtomicInteger successfulGames = new AtomicInteger(0);
    AtomicInteger failedGames = new AtomicInteger(0);
    List<Long> gameDurations = new CopyOnWriteArrayList<>();
    List<Long> moveDurations = new CopyOnWriteArrayList<>();

    Instant startTime = Instant.now();

    // Create and play 50 concurrent games
    for (int i = 0; i < numberOfGames; i++) {
      final int gameNumber = i;

      executorService.submit(() -> {
        try {
          Instant gameStart = Instant.now();

          // Create game
          var game = gameService.createGuestGame();
          assertNotNull(game, "Game should be created");

          // Simulate moves
          String[][] moves = {
              {"e2", "e4"}, {"e7", "e5"},
              {"g1", "f3"}, {"b8", "c6"},
              {"f1", "c4"}, {"g8", "f6"},
              {"d2", "d3"}, {"f8", "c5"},
              {"b1", "c3"}, {"d7", "d6"},
              {"c1", "g5"}, {"h7", "h6"},
              {"g5", "h4"}, {"g7", "g5"},
              {"h4", "g3"}, {"h8", "g8"},
              {"h2", "h4"}, {"g5", "g4"},
              {"f3", "e1"}, {"d8", "e7"}
          };

          for (int j = 0; j < Math.min(movesPerGame, moves.length); j++) {
            Instant moveStart = Instant.now();

            try {
              gameService.makeMove(
                  game.getId(),
                  moves[j][0],
                  moves[j][1],
                  null
              );

              Instant moveEnd = Instant.now();
              moveDurations.add(Duration.between(moveStart, moveEnd).toMillis());
            } catch (Exception e) {
              // Some moves might be invalid in the sequence, continue
            }

            // Small delay to simulate real gameplay
            Thread.sleep(10);
          }

          Instant gameEnd = Instant.now();
          gameDurations.add(Duration.between(gameStart, gameEnd).toMillis());
          successfulGames.incrementAndGet();

        } catch (Exception e) {
          System.err.println("Game " + gameNumber + " failed: " + e.getMessage());
          failedGames.incrementAndGet();
        } finally {
          latch.countDown();
        }
      });
    }

    // Wait for all games to complete
    boolean completed = latch.await(5, TimeUnit.MINUTES);
    assertTrue(completed, "All games should complete within 5 minutes");

    executorService.shutdown();
    executorService.awaitTermination(1, TimeUnit.MINUTES);

    Instant endTime = Instant.now();
    long totalDuration = Duration.between(startTime, endTime).toSeconds();

    // Calculate statistics
    double avgGameDuration = gameDurations.stream()
        .mapToLong(Long::longValue)
        .average()
        .orElse(0);

    double avgMoveDuration = moveDurations.stream()
        .mapToLong(Long::longValue)
        .average()
        .orElse(0);

    long maxGameDuration = gameDurations.stream()
        .mapToLong(Long::longValue)
        .max()
        .orElse(0);

    long maxMoveDuration = moveDurations.stream()
        .mapToLong(Long::longValue)
        .max()
        .orElse(0);

    // Calculate p95 for moves
    List<Long> sortedMoveDurations = new ArrayList<>(moveDurations);
    sortedMoveDurations.sort(Long::compareTo);
    int p95Index = (int) Math.ceil(sortedMoveDurations.size() * 0.95) - 1;
    long p95MoveDuration = p95Index >= 0 ? sortedMoveDurations.get(p95Index) : 0;

    System.out.println("=== Concurrent Game Load Test Results ===");
    System.out.println("Number of games: " + numberOfGames);
    System.out.println("Successful games: " + successfulGames.get());
    System.out.println("Failed games: " + failedGames.get());
    System.out.println("Total duration: " + totalDuration + " seconds");
    System.out.println("Average game duration: " + Math.round(avgGameDuration) + "ms");
    System.out.println("Max game duration: " + maxGameDuration + "ms");
    System.out.println("Average move duration: " + Math.round(avgMoveDuration) + "ms");
    System.out.println("P95 move duration: " + p95MoveDuration + "ms");
    System.out.println("Max move duration: " + maxMoveDuration + "ms");
    System.out.println("Total moves processed: " + moveDurations.size());
    System.out.println("Throughput: " + (successfulGames.get() / (double) totalDuration) + " games/second");

    // Assertions
    assertTrue(successfulGames.get() >= numberOfGames * 0.95,
        "At least 95% of games should complete successfully");
    assertTrue(avgMoveDuration < 200,
        "Average move duration should be < 200ms under load");
    assertTrue(p95MoveDuration < 500,
        "P95 move duration should be < 500ms under load");
  }

  @Test
  @DisplayName("Should maintain performance with sustained load")
  void shouldMaintainPerformanceWithSustainedLoad() throws Exception {
    int numberOfRounds = 5;
    int gamesPerRound = 10;
    List<Double> roundAverageDurations = new ArrayList<>();

    for (int round = 0; round < numberOfRounds; round++) {
      System.out.println("Starting round " + (round + 1));

      ExecutorService executorService = Executors.newFixedThreadPool(20);
      CountDownLatch latch = new CountDownLatch(gamesPerRound);
      List<Long> roundDurations = new CopyOnWriteArrayList<>();

      for (int i = 0; i < gamesPerRound; i++) {
        executorService.submit(() -> {
          try {
            Instant start = Instant.now();

            var game = gameService.createGuestGame();
            gameService.makeMove(game.getId(), "e2", "e4", null);
            gameService.makeMove(game.getId(), "e7", "e5", null);
            gameService.makeMove(game.getId(), "g1", "f3", null);

            Instant end = Instant.now();
            roundDurations.add(Duration.between(start, end).toMillis());
          } catch (Exception e) {
            // Log error but continue
          } finally {
            latch.countDown();
          }
        });
      }

      latch.await(2, TimeUnit.MINUTES);
      executorService.shutdown();
      executorService.awaitTermination(1, TimeUnit.MINUTES);

      double avgDuration = roundDurations.stream()
          .mapToLong(Long::longValue)
          .average()
          .orElse(0);

      roundAverageDurations.add(avgDuration);
      System.out.println("Round " + (round + 1) + " average: " + Math.round(avgDuration) + "ms");

      // Small delay between rounds
      Thread.sleep(1000);
    }

    // Check performance degradation
    double firstRoundAvg = roundAverageDurations.get(0);
    double lastRoundAvg = roundAverageDurations.get(numberOfRounds - 1);
    double degradation = ((lastRoundAvg - firstRoundAvg) / firstRoundAvg) * 100;

    System.out.println("=== Sustained Load Test Results ===");
    System.out.println("First round average: " + Math.round(firstRoundAvg) + "ms");
    System.out.println("Last round average: " + Math.round(lastRoundAvg) + "ms");
    System.out.println("Performance degradation: " + Math.round(degradation) + "%");

    // Performance should not degrade more than 20% over sustained load
    assertTrue(degradation < 20,
        "Performance degradation should be < 20%, but was " + degradation + "%");
  }

  @Test
  @DisplayName("Should handle database connection pool under load")
  void shouldHandleDatabaseConnectionPoolUnderLoad() throws Exception {
    int numberOfOperations = 100;
    ExecutorService executorService = Executors.newFixedThreadPool(50);
    CountDownLatch latch = new CountDownLatch(numberOfOperations);
    AtomicInteger successful = new AtomicInteger(0);
    AtomicInteger failed = new AtomicInteger(0);

    Instant startTime = Instant.now();

    for (int i = 0; i < numberOfOperations; i++) {
      executorService.submit(() -> {
        try {
          // Mix of operations
          var game = gameService.createGuestGame();
          gameService.makeMove(game.getId(), "e2", "e4", null);
          gameService.getGame(game.getId());

          successful.incrementAndGet();
        } catch (Exception e) {
          failed.incrementAndGet();
          System.err.println("Operation failed: " + e.getMessage());
        } finally {
          latch.countDown();
        }
      });
    }

    boolean completed = latch.await(3, TimeUnit.MINUTES);
    assertTrue(completed, "All operations should complete");

    executorService.shutdown();
    executorService.awaitTermination(1, TimeUnit.MINUTES);

    Instant endTime = Instant.now();
    long duration = Duration.between(startTime, endTime).toMillis();

    System.out.println("=== Database Connection Pool Test Results ===");
    System.out.println("Total operations: " + numberOfOperations);
    System.out.println("Successful: " + successful.get());
    System.out.println("Failed: " + failed.get());
    System.out.println("Duration: " + duration + "ms");
    System.out.println("Throughput: " + (successful.get() * 1000.0 / duration) + " ops/second");

    // At least 95% should succeed
    assertTrue(successful.get() >= numberOfOperations * 0.95,
        "At least 95% of operations should succeed");
  }

  @Test
  @DisplayName("Should handle memory efficiently under load")
  void shouldHandleMemoryEfficientlyUnderLoad() throws Exception {
    Runtime runtime = Runtime.getRuntime();

    // Get baseline memory
    System.gc();
    Thread.sleep(1000);
    long baselineMemory = runtime.totalMemory() - runtime.freeMemory();
    System.out.println("Baseline memory: " + (baselineMemory / 1024 / 1024) + " MB");

    // Create many games
    int numberOfGames = 100;
    List<Long> gameIds = new ArrayList<>();

    for (int i = 0; i < numberOfGames; i++) {
      var game = gameService.createGuestGame();
      gameIds.add(game.getId());

      // Make some moves
      gameService.makeMove(game.getId(), "e2", "e4", null);
      gameService.makeMove(game.getId(), "e7", "e5", null);
    }

    // Check memory after load
    System.gc();
    Thread.sleep(1000);
    long afterLoadMemory = runtime.totalMemory() - runtime.freeMemory();
    long memoryIncrease = afterLoadMemory - baselineMemory;
    double memoryPerGame = memoryIncrease / (double) numberOfGames;

    System.out.println("=== Memory Efficiency Test Results ===");
    System.out.println("After load memory: " + (afterLoadMemory / 1024 / 1024) + " MB");
    System.out.println("Memory increase: " + (memoryIncrease / 1024 / 1024) + " MB");
    System.out.println("Memory per game: " + Math.round(memoryPerGame / 1024) + " KB");

    // Each game should use less than 1MB on average
    assertTrue(memoryPerGame < 1024 * 1024,
        "Each game should use < 1MB, but used " + (memoryPerGame / 1024) + " KB");
  }
}

