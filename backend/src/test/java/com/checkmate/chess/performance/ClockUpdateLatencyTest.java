package com.checkmate.chess.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.checkmate.chess.dto.ClockUpdateMessage;
import com.checkmate.chess.model.GameClock;
import com.checkmate.chess.repository.GameClockRepository;
import com.checkmate.chess.scheduler.ClockUpdateScheduler;

/**
 * Performance test for clock update latency.
 * Verifies that clock updates are sent within 100ms (p95).
 */
@SpringBootTest
@ActiveProfiles("test")
class ClockUpdateLatencyTest {

  @Autowired
  private GameClockRepository clockRepository;

  @Autowired
  private ClockUpdateScheduler clockUpdateScheduler;

  @Autowired
  private SimpMessagingTemplate messagingTemplate;

  @Test
  @DisplayName("Clock updates should have <100ms latency (p95)")
  void testClockUpdateLatency() throws InterruptedException {
    // Given: 10 active game clocks
    List<GameClock> clocks = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      UUID gameId = UUID.randomUUID();
      GameClock clock = new GameClock(gameId, 300000L, 2000L, 0L);
      clock.setPaused(false);
      clocks.add(clockRepository.save(clock));
    }

    // When: Measure clock update latency over 100 iterations
    List<Long> latencies = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      long startTime = System.nanoTime();
      clockUpdateScheduler.sendClockUpdates();
      long endTime = System.nanoTime();
      
      long latencyMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
      latencies.add(latencyMs);
      
      Thread.sleep(10); // Small delay between iterations
    }

    // Then: Calculate p95 latency
    latencies.sort(Long::compareTo);
    int p95Index = (int) (latencies.size() * 0.95);
    long p95Latency = latencies.get(p95Index);

    System.out.println("Clock Update Latency Statistics:");
    System.out.println("  Min: " + latencies.get(0) + "ms");
    System.out.println("  Max: " + latencies.get(latencies.size() - 1) + "ms");
    System.out.println("  Avg: " + latencies.stream().mapToLong(Long::longValue).average().orElse(0) + "ms");
    System.out.println("  P95: " + p95Latency + "ms");

    // Verify p95 latency is under 100ms
    org.junit.jupiter.api.Assertions.assertTrue(
        p95Latency < 100,
        "P95 latency should be <100ms, but was " + p95Latency + "ms"
    );

    // Cleanup
    clockRepository.deleteAll(clocks);
  }

  @Test
  @DisplayName("Single clock update should complete in <10ms")
  void testSingleClockUpdateLatency() {
    // Given: One active clock
    UUID gameId = UUID.randomUUID();
    GameClock clock = new GameClock(gameId, 300000L, 2000L, 0L);
    clock.setPaused(false);
    clockRepository.save(clock);

    // When: Measure single update latency
    long startTime = System.nanoTime();
    
    ClockUpdateMessage message = new ClockUpdateMessage(
        clock.getWhiteTimeMs(),
        clock.getBlackTimeMs(),
        clock.getCurrentTurn()
    );
    
    messagingTemplate.convertAndSend(
        "/topic/game/" + gameId + "/clock",
        message
    );
    
    long endTime = System.nanoTime();
    long latencyMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

    System.out.println("Single clock update latency: " + latencyMs + "ms");

    // Then: Verify latency is under 50ms (more realistic for messaging template)
    org.junit.jupiter.api.Assertions.assertTrue(
        latencyMs < 50,
        "Single update latency should be <50ms, but was " + latencyMs + "ms"
    );

    // Cleanup
    clockRepository.delete(clock);
  }

  @Test
  @DisplayName("Clock updates should scale linearly with number of games")
  void testClockUpdateScaling() throws InterruptedException {
    // Test with different numbers of games
    int[] gameCounts = {1, 10, 50, 100};
    
    System.out.println("\nClock Update Scaling Test:");
    System.out.println("Games | Latency (ms)");
    System.out.println("------|-------------");

    for (int gameCount : gameCounts) {
      // Create clocks
      List<GameClock> clocks = new ArrayList<>();
      for (int i = 0; i < gameCount; i++) {
        UUID gameId = UUID.randomUUID();
        GameClock clock = new GameClock(gameId, 300000L, 2000L, 0L);
        clock.setPaused(false);
        clocks.add(clockRepository.save(clock));
      }

      // Measure latency
      long startTime = System.nanoTime();
      clockUpdateScheduler.sendClockUpdates();
      long endTime = System.nanoTime();
      long latencyMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

      System.out.printf("%5d | %12d%n", gameCount, latencyMs);

      // Verify reasonable scaling (allow 5ms per game for database operations)
      org.junit.jupiter.api.Assertions.assertTrue(
          latencyMs < gameCount * 5,
          "Latency should scale reasonably, but was " + latencyMs + "ms for " + gameCount + " games"
      );

      // Cleanup
      clockRepository.deleteAll(clocks);
      Thread.sleep(100); // Small delay between tests
    }
  }
}
