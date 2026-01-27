package com.checkmate.chess.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.chess.model.Rating;
import com.checkmate.chess.model.User;
import com.checkmate.chess.repository.RatingRepository;
import com.checkmate.chess.repository.UserRepository;

/**
 * Service for ELO rating calculations.
 * Implements standard ELO rating system with K-factor adjustments.
 */
@Service
public class RatingService {

  private static final Logger logger = LoggerFactory.getLogger(RatingService.class);

  private final RatingRepository ratingRepository;
  private final UserRepository userRepository;

  public RatingService(RatingRepository ratingRepository, UserRepository userRepository) {
    this.ratingRepository = ratingRepository;
    this.userRepository = userRepository;
  }

  /**
   * Update ratings for both players after a game.
   *
   * @param whitePlayerId the white player ID
   * @param blackPlayerId the black player ID
   * @param result "white", "black", or "draw"
   */
  @Transactional
  public void updateRatings(UUID whitePlayerId, UUID blackPlayerId, String result) {
    User whitePlayer = userRepository.findById(whitePlayerId)
        .orElseThrow(() -> new IllegalArgumentException("White player not found"));
    User blackPlayer = userRepository.findById(blackPlayerId)
        .orElseThrow(() -> new IllegalArgumentException("Black player not found"));

    int whiteOldRating = whitePlayer.getEloRating();
    int blackOldRating = blackPlayer.getEloRating();

    // Calculate expected scores
    double whiteExpected = calculateExpectedScore(whiteOldRating, blackOldRating);
    double blackExpected = calculateExpectedScore(blackOldRating, whiteOldRating);

    // Determine actual scores
    double whiteScore = getActualScore(result, true);
    double blackScore = getActualScore(result, false);

    // Calculate K-factors
    int whiteK = getKFactor(whitePlayer);
    int blackK = getKFactor(blackPlayer);

    // Calculate rating changes
    int whiteChange = (int) Math.round(whiteK * (whiteScore - whiteExpected));
    int blackChange = (int) Math.round(blackK * (blackScore - blackExpected));

    // Update ratings
    whitePlayer.setEloRating(whiteOldRating + whiteChange);
    blackPlayer.setEloRating(blackOldRating + blackChange);

    userRepository.save(whitePlayer);
    userRepository.save(blackPlayer);

    // Save rating history
    saveRatingHistory(whitePlayer, whiteOldRating, whiteChange, blackOldRating, result, null);
    saveRatingHistory(blackPlayer, blackOldRating, blackChange, whiteOldRating, result, null);

    logger.info("Updated ratings: White {} -> {}, Black {} -> {}",
        whiteOldRating, whitePlayer.getEloRating(),
        blackOldRating, blackPlayer.getEloRating());
  }

  /**
   * Calculate expected score using ELO formula.
   *
   * @param playerRating the player's rating
   * @param opponentRating the opponent's rating
   * @return expected score (0.0 to 1.0)
   */
  public double calculateExpectedScore(int playerRating, int opponentRating) {
    return 1.0 / (1.0 + Math.pow(10.0, (opponentRating - playerRating) / 400.0));
  }

  /**
   * Get actual score based on game result.
   *
   * @param result "white", "black", or "draw"
   * @param isWhite true if calculating for white player
   * @return 1.0 for win, 0.5 for draw, 0.0 for loss
   */
  private double getActualScore(String result, boolean isWhite) {
    if ("draw".equals(result)) {
      return 0.5;
    }
    boolean won = (isWhite && "white".equals(result)) || (!isWhite && "black".equals(result));
    return won ? 1.0 : 0.0;
  }

  /**
   * Get K-factor based on player experience and rating.
   *
   * @param player the player
   * @return K-factor (32, 24, or 16)
   */
  private int getKFactor(User player) {
    if (player.getGamesPlayed() < 30) {
      return 32; // New players
    } else if (player.getEloRating() < 2400) {
      return 24; // Intermediate players
    } else {
      return 16; // Master level players
    }
  }

  /**
   * Save rating history entry.
   */
  private void saveRatingHistory(User player, int oldRating, int change,
                                   int opponentRating, String gameResult, UUID gameId) {
    Rating rating = new Rating();
    rating.setUserId(player.getId());
    rating.setGameId(gameId);
    rating.setOldRating(oldRating);
    rating.setNewRating(oldRating + change);
    rating.setRatingChange(change);
    rating.setOpponentRating(opponentRating);
    rating.setGameResult(gameResult);
    ratingRepository.save(rating);
  }
}
