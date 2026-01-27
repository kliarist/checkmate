package com.checkmate.chess.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.checkmate.chess.dto.SuccessResponse;
import com.checkmate.chess.service.MatchmakingService;

/**
 * REST controller for matchmaking operations.
 * Handles queue management for ranked games.
 */
@RestController
@RequestMapping("/api/matchmaking")
public class MatchmakingController {

  private static final Logger logger = LoggerFactory.getLogger(MatchmakingController.class);

  private final MatchmakingService matchmakingService;

  public MatchmakingController(MatchmakingService matchmakingService) {
    this.matchmakingService = matchmakingService;
  }

  /**
   * Join matchmaking queue.
   *
   * @param userId the user ID
   * @param timeControl the time control (bullet, blitz, rapid, classical)
   * @return success response
   */
  @PostMapping("/queue")
  public ResponseEntity<SuccessResponse> joinQueue(
      @RequestParam UUID userId,
      @RequestParam String timeControl) {
    
    logger.info("User {} joining {} queue", userId, timeControl);
    
    matchmakingService.joinQueue(userId, timeControl);
    
    return ResponseEntity.ok(new SuccessResponse(
        "Successfully joined matchmaking queue",
        null
    ));
  }

  /**
   * Leave matchmaking queue.
   *
   * @param userId the user ID
   * @return success response
   */
  @DeleteMapping("/queue/{userId}")
  public ResponseEntity<SuccessResponse> leaveQueue(@PathVariable UUID userId) {
    logger.info("User {} leaving queue", userId);
    
    matchmakingService.leaveQueue(userId);
    
    return ResponseEntity.ok(new SuccessResponse(
        "Successfully left matchmaking queue",
        null
    ));
  }
}
