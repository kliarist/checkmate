package com.checkmate.chess.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.chess.model.Game;
import com.checkmate.chess.model.GameClock;
import com.checkmate.chess.repository.GameClockRepository;
import com.checkmate.chess.repository.GameRepository;

/**
 * Service for chess clock management.
 * Handles time tracking, increments, delays, and timeout detection.
 */
@Service
public class ChessClockService {

  private static final Logger logger = LoggerFactory.getLogger(ChessClockService.class);

  private final GameClockRepository clockRepository;
  private final GameRepository gameRepository;

  // Time control configurations (in milliseconds)
  private static final Map<String, Long> TIME_CONTROLS = new HashMap<>();
  
  static {
    TIME_CONTROLS.put("bullet", 60000L);      // 1 minute
    TIME_CONTROLS.put("blitz", 300000L);      // 5 minutes
    TIME_CONTROLS.put("rapid", 600000L);      // 10 minutes
    TIME_CONTROLS.put("classical", 1800000L); // 30 minutes
  }

  public ChessClockService(GameClockRepository clockRepository, GameRepository gameRepository) {
    this.clockRepository = clockRepository;
    this.gameRepository = gameRepository;
  }

  /**
   * Initialize clock for a game.
   *
   * @param gameId the game ID
   * @param timeControl the time control
   */
  @Transactional
  public void initializeClock(UUID gameId, String timeControl) {
    Long initialTime = TIME_CONTROLS.getOrDefault(timeControl, 300000L);
    Long increment = getIncrementForTimeControl(timeControl);
    Long delay = 0L; // No delay by default

    GameClock clock = new GameClock(gameId, initialTime, increment, delay);
    clockRepository.save(clock);

    logger.info("Initialized clock for game {} with {} ms", gameId, initialTime);
  }

  /**
   * Update clock after time elapsed.
   *
   * @param gameId the game ID
   * @param player "white" or "black"
   * @param elapsedMs milliseconds elapsed
   */
  @Transactional
  public void updateClock(UUID gameId, String player, Long elapsedMs) {
    GameClock clock = clockRepository.findByGameId(gameId)
        .orElseThrow(() -> new IllegalArgumentException("Clock not found"));

    if (clock.isPaused()) {
      return; // Don't update if paused
    }

    // Handle delay
    long timeToDeduct = elapsedMs;
    if (clock.getDelayMs() > 0) {
      timeToDeduct = Math.max(0, elapsedMs - clock.getDelayMs());
    }

    // Deduct time
    if ("white".equals(player)) {
      clock.setWhiteTimeMs(Math.max(0, clock.getWhiteTimeMs() - timeToDeduct));
    } else {
      clock.setBlackTimeMs(Math.max(0, clock.getBlackTimeMs() - timeToDeduct));
    }

    clockRepository.save(clock);
  }

  /**
   * Add increment after a move.
   *
   * @param gameId the game ID
   * @param player "white" or "black"
   */
  @Transactional
  public void addIncrement(UUID gameId, String player) {
    GameClock clock = clockRepository.findByGameId(gameId)
        .orElseThrow(() -> new IllegalArgumentException("Clock not found"));

    if (clock.getIncrementMs() > 0) {
      if ("white".equals(player)) {
        clock.setWhiteTimeMs(clock.getWhiteTimeMs() + clock.getIncrementMs());
      } else {
        clock.setBlackTimeMs(clock.getBlackTimeMs() + clock.getIncrementMs());
      }
      clockRepository.save(clock);
    }
  }

  /**
   * Check if a player has run out of time.
   *
   * @param gameId the game ID
   * @return true if timeout occurred
   */
  @Transactional
  public boolean checkTimeout(UUID gameId) {
    GameClock clock = clockRepository.findByGameId(gameId)
        .orElseThrow(() -> new IllegalArgumentException("Clock not found"));

    boolean whiteTimeout = clock.getWhiteTimeMs() <= 0;
    boolean blackTimeout = clock.getBlackTimeMs() <= 0;

    if (whiteTimeout || blackTimeout) {
      Game game = gameRepository.findById(gameId)
          .orElseThrow(() -> new IllegalArgumentException("Game not found"));

      String winner = whiteTimeout ? "black" : "white";
      game.endGame(winner, "timeout");
      gameRepository.save(game);

      logger.info("Game {} ended by timeout, winner: {}", gameId, winner);
      return true;
    }

    return false;
  }

  /**
   * Switch turn to the other player.
   *
   * @param gameId the game ID
   */
  @Transactional
  public void switchTurn(UUID gameId) {
    GameClock clock = clockRepository.findByGameId(gameId)
        .orElseThrow(() -> new IllegalArgumentException("Clock not found"));

    clock.setCurrentTurn("white".equals(clock.getCurrentTurn()) ? "black" : "white");
    clockRepository.save(clock);
  }

  /**
   * Get remaining time for a player.
   *
   * @param gameId the game ID
   * @param player "white" or "black"
   * @return remaining time in milliseconds
   */
  public Long getRemainingTime(UUID gameId, String player) {
    GameClock clock = clockRepository.findByGameId(gameId)
        .orElseThrow(() -> new IllegalArgumentException("Clock not found"));

    return "white".equals(player) ? clock.getWhiteTimeMs() : clock.getBlackTimeMs();
  }

  /**
   * Pause the clock.
   *
   * @param gameId the game ID
   */
  @Transactional
  public void pauseClock(UUID gameId) {
    GameClock clock = clockRepository.findByGameId(gameId)
        .orElseThrow(() -> new IllegalArgumentException("Clock not found"));

    clock.setPaused(true);
    clockRepository.save(clock);
  }

  /**
   * Resume the clock.
   *
   * @param gameId the game ID
   */
  @Transactional
  public void resumeClock(UUID gameId) {
    GameClock clock = clockRepository.findByGameId(gameId)
        .orElseThrow(() -> new IllegalArgumentException("Clock not found"));

    clock.setPaused(false);
    clockRepository.save(clock);
  }

  /**
   * Get increment for time control.
   */
  private Long getIncrementForTimeControl(String timeControl) {
    return switch (timeControl) {
      case "bullet" -> 0L; // No increment for bullet
      case "blitz" -> 2000L; // 2 seconds
      case "rapid" -> 5000L; // 5 seconds
      case "classical" -> 30000L; // 30 seconds
      default -> 0L;
    };
  }
}
