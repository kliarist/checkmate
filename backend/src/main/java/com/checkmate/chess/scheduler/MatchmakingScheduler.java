package com.checkmate.chess.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.checkmate.chess.service.MatchmakingService;

/**
 * Scheduler for matchmaking operations.
 * Runs pairing logic periodically and removes expired queue entries.
 */
@Component
public class MatchmakingScheduler {

  private static final Logger logger = LoggerFactory.getLogger(MatchmakingScheduler.class);

  private final MatchmakingService matchmakingService;

  public MatchmakingScheduler(MatchmakingService matchmakingService) {
    this.matchmakingService = matchmakingService;
  }

  /**
   * Process matchmaking pairing every 2 seconds.
   * Pairs players with compatible ratings in the queue.
   */
  @Scheduled(fixedRate = 2000)
  public void processPairing() {
    try {
      matchmakingService.processPairing();
    } catch (Exception e) {
      logger.error("Error processing matchmaking pairing", e);
    }
  }

  /**
   * Remove expired queue entries every minute.
   * Removes entries older than 5 minutes.
   */
  @Scheduled(fixedRate = 60000)
  public void removeExpiredEntries() {
    try {
      matchmakingService.removeExpiredEntries();
    } catch (Exception e) {
      logger.error("Error removing expired matchmaking entries", e);
    }
  }
}
