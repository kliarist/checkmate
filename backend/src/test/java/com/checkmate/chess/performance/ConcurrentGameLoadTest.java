package com.checkmate.chess.performance;

import static org.assertj.core.api.Assertions.assertThat;

import com.checkmate.chess.dto.CreateGuestGameResponse;
import com.checkmate.chess.service.GameService;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Disabled("Performance tests are environment-dependent and should be run separately")
@DisplayName("Concurrent Game Load Tests")
class ConcurrentGameLoadTest {

  @Autowired
  private GameService gameService;

  @Test
  @DisplayName("Should handle 50 concurrent games with 100 players")
  void shouldHandle50ConcurrentGames() throws Exception {
    final int numberOfGames = 50;
    final int movesPerGame = 10;

    final ExecutorService executorService = Executors.newFixedThreadPool(100);
    final CountDownLatch latch = new CountDownLatch(numberOfGames);
    final AtomicInteger successfulGames = new AtomicInteger(0);
    final AtomicInteger failedGames = new AtomicInteger(0);
    final List<Long> gameDurations = new CopyOnWriteArrayList<>();
    final List<Long> moveDurations = new CopyOnWriteArrayList<>();

    final Instant startTime = Instant.now();

    for (int i = 0; i < numberOfGames; i++) {
      final int gameNumber = i;

      executorService.submit(() -> {
        try {
          final Instant gameStart = Instant.now();

          final CreateGuestGameResponse game = gameService.createGuestGame(null);
          assertThat(game).isNotNull();

          final String[][] moves = {
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

          for (int j = 0; j < movesPerGame; j++) {
            final Instant moveStart = Instant.now();

            try {
              gameService.makeMove(
                  game.gameId(),
                  moves[j][0],
                  moves[j][1],
                  null
              );

              final Instant moveEnd = Instant.now();
              moveDurations.add(Duration.between(moveStart, moveEnd).toMillis());
            } catch (Exception e) {
              System.err.println("Move failed: " + e.getMessage());
            }
          }

          final Instant gameEnd = Instant.now();
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

    final boolean completed = latch.await(5, TimeUnit.MINUTES);
    assertThat(completed).as("All games should complete within 5 minutes").isTrue();

    executorService.shutdown();
    final boolean terminated = executorService.awaitTermination(1, TimeUnit.MINUTES);
    assertThat(terminated).isTrue();

    final Instant endTime = Instant.now();
    final long totalDuration = Duration.between(startTime, endTime).toSeconds();

    final double avgGameDuration = gameDurations.stream()
        .mapToLong(Long::longValue)
        .average()
        .orElse(0);

    final double avgMoveDuration = moveDurations.stream()
        .mapToLong(Long::longValue)
        .average()
        .orElse(0);

    final long maxGameDuration = gameDurations.stream()
        .mapToLong(Long::longValue)
        .max()
        .orElse(0);

    final long maxMoveDuration = moveDurations.stream()
        .mapToLong(Long::longValue)
        .max()
        .orElse(0);

    final List<Long> sortedMoveDurations = new ArrayList<>(moveDurations);
    sortedMoveDurations.sort(Long::compareTo);
    final int p95Index = (int) Math.ceil(sortedMoveDurations.size() * 0.95) - 1;
    final long p95MoveDuration = p95Index >= 0 ? sortedMoveDurations.get(p95Index) : 0;

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

    assertThat(successfulGames.get())
        .as("At least 95%% of games should complete successfully")
        .isGreaterThanOrEqualTo(numberOfGames * 95 / 100);
    assertThat(avgMoveDuration)
        .as("Average move duration should be < 200ms under load")
        .isLessThan(200);
    assertThat(p95MoveDuration)
        .as("P95 move duration should be < 500ms under load")
        .isLessThan(500);
  }

  @Test
  @DisplayName("Should maintain performance with sustained load")
  void shouldMaintainPerformanceWithSustainedLoad() throws Exception {
    final int numberOfRounds = 5;
    final int gamesPerRound = 10;
    final List<Double> roundAverageDurations = new ArrayList<>();

    for (int round = 0; round < numberOfRounds; round++) {
      System.out.println("Starting round " + (round + 1));

      final ExecutorService executorService = Executors.newFixedThreadPool(20);
      final CountDownLatch latch = new CountDownLatch(gamesPerRound);
      final List<Long> roundDurations = new CopyOnWriteArrayList<>();

      for (int i = 0; i < gamesPerRound; i++) {
        executorService.submit(() -> {
          try {
            final Instant start = Instant.now();

            final CreateGuestGameResponse game = gameService.createGuestGame(null);
            gameService.makeMove(game.gameId(), "e2", "e4", null);
            gameService.makeMove(game.gameId(), "e7", "e5", null);
            gameService.makeMove(game.gameId(), "g1", "f3", null);

            final Instant end = Instant.now();
            roundDurations.add(Duration.between(start, end).toMillis());
          } catch (Exception e) {
            System.err.println("Game failed: " + e.getMessage());
          } finally {
            latch.countDown();
          }
        });
      }

      final boolean allCompleted = latch.await(2, TimeUnit.MINUTES);
      assertThat(allCompleted).isTrue();
      executorService.shutdown();
      final boolean allTerminated = executorService.awaitTermination(1, TimeUnit.MINUTES);
      assertThat(allTerminated).isTrue();

      final double avgDuration = roundDurations.stream()
          .mapToLong(Long::longValue)
          .average()
          .orElse(0);

      roundAverageDurations.add(avgDuration);
      System.out.println("Round " + (round + 1) + " average: " + Math.round(avgDuration) + "ms");
    }

    final double firstRoundAvg = roundAverageDurations.getFirst();
    final double lastRoundAvg = roundAverageDurations.get(numberOfRounds - 1);
    final double degradation = ((lastRoundAvg - firstRoundAvg) / firstRoundAvg) * 100;

    System.out.println("=== Sustained Load Test Results ===");
    System.out.println("First round average: " + Math.round(firstRoundAvg) + "ms");
    System.out.println("Last round average: " + Math.round(lastRoundAvg) + "ms");
    System.out.println("Performance degradation: " + Math.round(degradation) + "%");

    assertThat(degradation)
        .as("Performance degradation should be < 20%%, but was " + degradation + "%%")
        .isLessThan(20);
  }

  @Test
  @DisplayName("Should handle memory efficiently under load")
  void shouldHandleMemoryEfficientlyUnderLoad() {
    final Runtime runtime = Runtime.getRuntime();

    System.gc();
    final long baselineMemory = runtime.totalMemory() - runtime.freeMemory();
    System.out.println("Baseline memory: " + (baselineMemory / 1024 / 1024) + " MB");

    final int numberOfGames = 100;

    for (int i = 0; i < numberOfGames; i++) {
      final CreateGuestGameResponse game = gameService.createGuestGame(null);

      gameService.makeMove(game.gameId(), "e2", "e4", null);
      gameService.makeMove(game.gameId(), "e7", "e5", null);
    }

    System.gc();
    final long afterLoadMemory = runtime.totalMemory() - runtime.freeMemory();
    final long memoryIncrease = afterLoadMemory - baselineMemory;
    final double memoryPerGame = memoryIncrease / (double) numberOfGames;

    System.out.println("=== Memory Efficiency Test Results ===");
    System.out.println("After load memory: " + (afterLoadMemory / 1024 / 1024) + " MB");
    System.out.println("Memory increase: " + (memoryIncrease / 1024 / 1024) + " MB");
    System.out.println("Memory per game: " + Math.round(memoryPerGame / 1024) + " KB");

    assertThat(memoryPerGame)
        .as("Each game should use < 1MB, but used " + (memoryPerGame / 1024) + " KB")
        .isLessThan(1024 * 1024);
  }
}

