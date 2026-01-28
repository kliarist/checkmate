package com.checkmate.chess.scheduler;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.checkmate.chess.dto.ClockUpdateMessage;
import com.checkmate.chess.model.GameClock;
import com.checkmate.chess.repository.GameClockRepository;
import com.checkmate.chess.service.ChessClockService;

/**
 * Unit tests for ClockUpdateScheduler.
 * Tests WebSocket clock update broadcasting.
 */
@ExtendWith(MockitoExtension.class)
class ClockUpdateSchedulerTest {

  @Mock
  private GameClockRepository clockRepository;

  @Mock
  private ChessClockService clockService;

  @Mock
  private SimpMessagingTemplate messagingTemplate;

  @InjectMocks
  private ClockUpdateScheduler scheduler;

  private UUID gameId;
  private GameClock activeClock;

  @BeforeEach
  void setUp() {
    gameId = UUID.randomUUID();
    activeClock = new GameClock(gameId, 300000L, 2000L, 0L);
    activeClock.setWhiteTimeMs(250000L);
    activeClock.setBlackTimeMs(280000L);
    activeClock.setCurrentTurn("white");
    activeClock.setPaused(false);
  }

  @Test
  @DisplayName("Should send clock updates for active games")
  void testSendClockUpdates() {
    // Given
    when(clockRepository.findAll()).thenReturn(Arrays.asList(activeClock));
    when(clockService.checkTimeout(gameId)).thenReturn(false);

    // When
    scheduler.sendClockUpdates();

    // Then
    verify(messagingTemplate).convertAndSend(
        eq("/topic/game/" + gameId + "/clock"),
        any(ClockUpdateMessage.class)
    );
  }

  @Test
  @DisplayName("Should not send updates for paused clocks")
  void testDoNotSendUpdatesForPausedClocks() {
    // Given
    activeClock.setPaused(true);
    when(clockRepository.findAll()).thenReturn(Arrays.asList(activeClock));

    // When
    scheduler.sendClockUpdates();

    // Then
    verify(messagingTemplate, never()).convertAndSend(
        eq("/topic/game/" + gameId + "/clock"),
        any(ClockUpdateMessage.class)
    );
  }

  @Test
  @DisplayName("Should not send updates when timeout occurs")
  void testDoNotSendUpdatesOnTimeout() {
    // Given
    when(clockRepository.findAll()).thenReturn(Arrays.asList(activeClock));
    when(clockService.checkTimeout(gameId)).thenReturn(true);

    // When
    scheduler.sendClockUpdates();

    // Then
    verify(messagingTemplate, never()).convertAndSend(
        eq("/topic/game/" + gameId + "/clock"),
        any(ClockUpdateMessage.class)
    );
  }

  @Test
  @DisplayName("Should handle multiple active games")
  void testHandleMultipleGames() {
    // Given
    UUID gameId2 = UUID.randomUUID();
    GameClock clock2 = new GameClock(gameId2, 300000L, 2000L, 0L);
    clock2.setPaused(false);

    when(clockRepository.findAll()).thenReturn(Arrays.asList(activeClock, clock2));
    when(clockService.checkTimeout(gameId)).thenReturn(false);
    when(clockService.checkTimeout(gameId2)).thenReturn(false);

    // When
    scheduler.sendClockUpdates();

    // Then
    verify(messagingTemplate).convertAndSend(
        eq("/topic/game/" + gameId + "/clock"),
        any(ClockUpdateMessage.class)
    );
    verify(messagingTemplate).convertAndSend(
        eq("/topic/game/" + gameId2 + "/clock"),
        any(ClockUpdateMessage.class)
    );
  }

  @Test
  @DisplayName("Should handle empty clock list")
  void testHandleEmptyClockList() {
    // Given
    when(clockRepository.findAll()).thenReturn(List.of());

    // When
    scheduler.sendClockUpdates();

    // Then
    verify(clockService, never()).checkTimeout(any(UUID.class));
    verify(messagingTemplate, never()).convertAndSend(
        any(String.class),
        any(ClockUpdateMessage.class)
    );
  }

  @Test
  @DisplayName("Should handle exceptions gracefully")
  void testHandleExceptions() {
    // Given
    when(clockRepository.findAll()).thenThrow(new RuntimeException("Database error"));

    // When/Then: Should not throw exception
    org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> scheduler.sendClockUpdates());
  }
}
