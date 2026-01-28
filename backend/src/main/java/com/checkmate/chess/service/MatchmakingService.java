package com.checkmate.chess.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.chess.model.Game;
import com.checkmate.chess.model.MatchmakingQueue;
import com.checkmate.chess.model.User;
import com.checkmate.chess.repository.GameRepository;
import com.checkmate.chess.repository.MatchmakingQueueRepository;
import com.checkmate.chess.repository.UserRepository;

/**
 * Service for matchmaking logic.
 * Manages queue and pairs players based on rating and time control.
 */
@Service
public class MatchmakingService {

  private static final Logger logger = LoggerFactory.getLogger(MatchmakingService.class);
  private static final int MAX_RATING_DIFFERENCE = 200;
  private static final int QUEUE_TIMEOUT_MINUTES = 5;

  private final MatchmakingQueueRepository queueRepository;
  private final UserRepository userRepository;
  private final GameRepository gameRepository;
  private final Random random = new Random();

  public MatchmakingService(
      MatchmakingQueueRepository queueRepository,
      UserRepository userRepository,
      GameRepository gameRepository) {
    this.queueRepository = queueRepository;
    this.userRepository = userRepository;
    this.gameRepository = gameRepository;
  }

  /**
   * Add player to matchmaking queue.
   *
   * @param userId the user ID
   * @param timeControl the time control
   */
  @Transactional
  public void joinQueue(UUID userId, String timeControl) {
    // Validate time control
    validateTimeControl(timeControl);
    
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    // Remove existing queue entry if any
    queueRepository.findByUserId(userId).ifPresent(queueRepository::delete);

    // Add to queue
    MatchmakingQueue entry = new MatchmakingQueue(userId, user.getEloRating(), timeControl);
    queueRepository.save(entry);

    logger.info("User {} joined {} queue with rating {}", 
        user.getUsername(), timeControl, user.getEloRating());
  }

  /**
   * Validate time control format.
   * Valid formats: bullet, blitz, rapid, classical
   *
   * @param timeControl the time control to validate
   * @throws IllegalArgumentException if invalid
   */
  private void validateTimeControl(String timeControl) {
    if (timeControl == null || timeControl.trim().isEmpty()) {
      throw new IllegalArgumentException("Time control cannot be null or empty");
    }
    
    String normalized = timeControl.toLowerCase().trim();
    if (!List.of("bullet", "blitz", "rapid", "classical").contains(normalized)) {
      throw new IllegalArgumentException(
          "Invalid time control: " + timeControl + 
          ". Must be one of: bullet, blitz, rapid, classical");
    }
  }

  /**
   * Remove player from matchmaking queue.
   *
   * @param userId the user ID
   */
  @Transactional
  public void leaveQueue(UUID userId) {
    queueRepository.findByUserId(userId).ifPresent(entry -> {
      queueRepository.delete(entry);
      logger.info("User {} left matchmaking queue", userId);
    });
  }

  /**
   * Process matchmaking for all time controls.
   * Pairs players with similar ratings.
   */
  @Transactional
  public void processPairing() {
    String[] timeControls = {"bullet", "blitz", "rapid", "classical"};
    
    for (String timeControl : timeControls) {
      processPairingForTimeControl(timeControl);
    }
  }

  /**
   * Process pairing for a specific time control.
   */
  private void processPairingForTimeControl(String timeControl) {
    List<MatchmakingQueue> queue = queueRepository
        .findByTimeControlOrderByCreatedAtAsc(timeControl);

    if (queue.size() < 2) {
      return; // Need at least 2 players
    }

    // Try to match first two players with compatible ratings
    for (int i = 0; i < queue.size() - 1; i++) {
      MatchmakingQueue player1 = queue.get(i);
      
      for (int j = i + 1; j < queue.size(); j++) {
        MatchmakingQueue player2 = queue.get(j);
        
        if (canMatch(player1, player2)) {
          createGame(player1, player2, timeControl);
          queueRepository.delete(player1);
          queueRepository.delete(player2);
          return; // Match found, exit
        }
      }
    }
  }

  /**
   * Check if two players can be matched.
   */
  private boolean canMatch(MatchmakingQueue player1, MatchmakingQueue player2) {
    int ratingDiff = Math.abs(player1.getRating() - player2.getRating());
    return ratingDiff <= MAX_RATING_DIFFERENCE;
  }

  /**
   * Create a game between two matched players.
   */
  private void createGame(MatchmakingQueue player1, MatchmakingQueue player2, String timeControl) {
    User user1 = userRepository.findById(player1.getUserId())
        .orElseThrow(() -> new IllegalArgumentException("User 1 not found"));
    User user2 = userRepository.findById(player2.getUserId())
        .orElseThrow(() -> new IllegalArgumentException("User 2 not found"));

    // Randomly assign colors
    boolean user1IsWhite = random.nextBoolean();
    
    Game game = new Game();
    game.setWhitePlayer(user1IsWhite ? user1 : user2);
    game.setBlackPlayer(user1IsWhite ? user2 : user1);
    game.setGameType("ranked");
    game.setTimeControl(timeControl);
    game.setCurrentFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    game.setStatus("in_progress");
    
    gameRepository.save(game);

    logger.info("Created ranked game {} between {} (rating {}) and {} (rating {})",
        game.getId(), user1.getUsername(), player1.getRating(),
        user2.getUsername(), player2.getRating());
  }

  /**
   * Remove expired queue entries (older than 5 minutes).
   */
  @Transactional
  public void removeExpiredEntries() {
    LocalDateTime cutoff = LocalDateTime.now().minusMinutes(QUEUE_TIMEOUT_MINUTES);
    List<MatchmakingQueue> expired = queueRepository.findByCreatedAtBefore(cutoff);
    
    if (!expired.isEmpty()) {
      queueRepository.deleteAll(expired);
      logger.info("Removed {} expired matchmaking entries", expired.size());
    }
  }
}
