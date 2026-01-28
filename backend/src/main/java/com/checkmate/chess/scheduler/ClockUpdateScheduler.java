package com.checkmate.chess.scheduler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.checkmate.chess.dto.ClockUpdateMessage;
import com.checkmate.chess.model.GameClock;
import com.checkmate.chess.repository.GameClockRepository;
import com.checkmate.chess.service.ChessClockService;

/**
 * Scheduler for chess clock updates.
 * Sends clock state to clients every second via WebSocket.
 */
@Component
public class ClockUpdateScheduler {

  private static final Logger logger = LoggerFactory.getLogger(ClockUpdateScheduler.class);

  private final GameClockRepository clockRepository;
  private final ChessClockService clockService;
  private final SimpMessagingTemplate messagingTemplate;

  public ClockUpdateScheduler(
      GameClockRepository clockRepository,
      ChessClockService clockService,
      SimpMessagingTemplate messagingTemplate) {
    this.clockRepository = clockRepository;
    this.clockService = clockService;
    this.messagingTemplate = messagingTemplate;
  }

  /**
   * Send clock updates every second for all active games.
   */
  @Scheduled(fixedRate = 1000)
  public void sendClockUpdates() {
    try {
      List<GameClock> activeClocks = clockRepository.findAll();
      
      for (GameClock clock : activeClocks) {
        if (!clock.isPaused()) {
          // Check for timeout
          boolean timeout = clockService.checkTimeout(clock.getGameId());
          
          if (!timeout) {
            // Send clock update
            ClockUpdateMessage message = new ClockUpdateMessage(
                clock.getWhiteTimeMs(),
                clock.getBlackTimeMs(),
                clock.getCurrentTurn()
            );
            
            messagingTemplate.convertAndSend(
                "/topic/game/" + clock.getGameId() + "/clock",
                message
            );
          }
        }
      }
    } catch (IllegalArgumentException e) {
      logger.error("Invalid clock or game data: {}", e.getMessage());
    } catch (RuntimeException e) {
      logger.error("Error sending clock updates", e);
    }
  }
}
