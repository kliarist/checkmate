package com.checkmate.chess.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.checkmate.chess.model.Game;
import com.checkmate.chess.model.GameClock;
import com.checkmate.chess.repository.GameClockRepository;
import com.checkmate.chess.repository.GameRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for ChessClockService.
 * Tests clock management, time tracking, and timeout detection.
 */
@ExtendWith(MockitoExtension.class)
class ChessClockServiceTest {

  @Mock
  private GameClockRepository clockRepository;

  @Mock
  private GameRepository gameRepository;

  @InjectMocks
  private ChessClockService clockService;

  private UUID gameId;
  private Game game;
  private GameClock clock;

  @BeforeEach
  void setUp() {
    gameId = UUID.randomUUID();
    
    game = new Game();
    game.setId(gameId);
    game.setTimeControl("blitz");
    game.setStatus("in_progress");

    clock = new GameClock();
    clock.setGameId(gameId);
    clock.setWhiteTimeMs(300000L); // 5 minutes
    clock.setBlackTimeMs(300000L); // 5 minutes
    clock.setIncrementMs(0L);
    clock.setCurrentTurn("white");
  }

  @Test
  @DisplayName("Should initialize clock for blitz game (5 minutes)")
  void testInitializeBlitzClock() {
    // When
    clockService.initializeClock(gameId, "blitz");

    // Then
    verify(clockRepository).save(any(GameClock.class));
  }

  @Test
  @DisplayName("Should initialize clock for rapid game (10 minutes)")
  void testInitializeRapidClock() {
    // When
    clockService.initializeClock(gameId, "rapid");

    // Then
    verify(clockRepository).save(any(GameClock.class));
  }

  @Test
  @DisplayName("Should initialize clock for bullet game (1 minute)")
  void testInitializeBulletClock() {
    // When
    clockService.initializeClock(gameId, "bullet");

    // Then
    verify(clockRepository).save(any(GameClock.class));
  }

  @Test
  @DisplayName("Should deduct time from white player's clock")
  void testDeductTimeWhite() {
    // Given
    when(clockRepository.findByGameId(gameId)).thenReturn(Optional.of(clock));
    long initialTime = clock.getWhiteTimeMs();

    // When: 5 seconds elapsed
    clockService.updateClock(gameId, "white", 5000L);

    // Then: White's time reduced by 5 seconds
    assertThat(clock.getWhiteTimeMs()).isEqualTo(initialTime - 5000L);
    verify(clockRepository).save(clock);
  }

  @Test
  @DisplayName("Should deduct time from black player's clock")
  void testDeductTimeBlack() {
    // Given
    clock.setCurrentTurn("black");
    when(clockRepository.findByGameId(gameId)).thenReturn(Optional.of(clock));
    long initialTime = clock.getBlackTimeMs();

    // When: 3 seconds elapsed
    clockService.updateClock(gameId, "black", 3000L);

    // Then: Black's time reduced by 3 seconds
    assertThat(clock.getBlackTimeMs()).isEqualTo(initialTime - 3000L);
    verify(clockRepository).save(clock);
  }

  @Test
  @DisplayName("Should add increment after move")
  void testAddIncrement() {
    // Given: Clock with 2 second increment
    clock.setIncrementMs(2000L);
    clock.setWhiteTimeMs(100000L);
    when(clockRepository.findByGameId(gameId)).thenReturn(Optional.of(clock));

    // When: White makes a move
    clockService.addIncrement(gameId, "white");

    // Then: White gets 2 seconds added
    assertThat(clock.getWhiteTimeMs()).isEqualTo(102000L);
    verify(clockRepository).save(clock);
  }

  @Test
  @DisplayName("Should detect timeout when time reaches zero")
  void testDetectTimeout() {
    // Given: White has no time left
    clock.setWhiteTimeMs(0L);
    when(clockRepository.findByGameId(gameId)).thenReturn(Optional.of(clock));
    when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

    // When
    boolean timeout = clockService.checkTimeout(gameId);

    // Then: Timeout detected
    assertThat(timeout).isTrue();
    verify(gameRepository).save(game);
    assertThat(game.getStatus()).isEqualTo("FINISHED");
    assertThat(game.getResult()).isEqualTo("black"); // Black wins on time
  }

  @Test
  @DisplayName("Should not detect timeout when both players have time")
  void testNoTimeout() {
    // Given: Both players have time
    when(clockRepository.findByGameId(gameId)).thenReturn(Optional.of(clock));

    // When
    boolean timeout = clockService.checkTimeout(gameId);

    // Then: No timeout
    assertThat(timeout).isFalse();
  }

  @Test
  @DisplayName("Should switch turn after move")
  void testSwitchTurn() {
    // Given
    when(clockRepository.findByGameId(gameId)).thenReturn(Optional.of(clock));
    assertThat(clock.getCurrentTurn()).isEqualTo("white");

    // When
    clockService.switchTurn(gameId);

    // Then: Turn switched to black
    assertThat(clock.getCurrentTurn()).isEqualTo("black");
    verify(clockRepository).save(clock);
  }

  @Test
  @DisplayName("Should get remaining time for player")
  void testGetRemainingTime() {
    // Given
    when(clockRepository.findByGameId(gameId)).thenReturn(Optional.of(clock));

    // When
    long whiteTime = clockService.getRemainingTime(gameId, "white");
    long blackTime = clockService.getRemainingTime(gameId, "black");

    // Then
    assertThat(whiteTime).isEqualTo(300000L);
    assertThat(blackTime).isEqualTo(300000L);
  }

  @Test
  @DisplayName("Should handle delay time control")
  void testDelayTimeControl() {
    // Given: Clock with 5 second delay
    clock.setDelayMs(5000L);
    clock.setWhiteTimeMs(100000L);
    when(clockRepository.findByGameId(gameId)).thenReturn(Optional.of(clock));

    // When: Move made within delay period (3 seconds)
    clockService.updateClock(gameId, "white", 3000L);

    // Then: No time deducted (delay absorbs it)
    assertThat(clock.getWhiteTimeMs()).isEqualTo(100000L);
  }

  @Test
  @DisplayName("Should deduct time after delay expires")
  void testTimeDeductionAfterDelay() {
    // Given: Clock with 5 second delay
    clock.setDelayMs(5000L);
    clock.setWhiteTimeMs(100000L);
    when(clockRepository.findByGameId(gameId)).thenReturn(Optional.of(clock));

    // When: Move takes 7 seconds (exceeds delay)
    clockService.updateClock(gameId, "white", 7000L);

    // Then: Only 2 seconds deducted (7 - 5 delay)
    assertThat(clock.getWhiteTimeMs()).isEqualTo(98000L);
  }

  @Test
  @DisplayName("Should pause clock")
  void testPauseClock() {
    // Given
    when(clockRepository.findByGameId(gameId)).thenReturn(Optional.of(clock));

    // When
    clockService.pauseClock(gameId);

    // Then
    assertThat(clock.isPaused()).isTrue();
    verify(clockRepository).save(clock);
  }

  @Test
  @DisplayName("Should resume clock")
  void testResumeClock() {
    // Given
    clock.setPaused(true);
    when(clockRepository.findByGameId(gameId)).thenReturn(Optional.of(clock));

    // When
    clockService.resumeClock(gameId);

    // Then
    assertThat(clock.isPaused()).isFalse();
    verify(clockRepository).save(clock);
  }
}
