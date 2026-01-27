package com.checkmate.chess.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.checkmate.chess.model.Rating;

/**
 * Repository for Rating entity.
 * Provides access to rating history data.
 */
@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {

  /**
   * Find all rating history entries for a user, ordered by creation date descending.
   *
   * @param userId the user ID
   * @return list of rating entries
   */
  List<Rating> findByUserIdOrderByCreatedAtDesc(UUID userId);

  /**
   * Find rating history for a specific game.
   *
   * @param gameId the game ID
   * @return list of rating entries for that game
   */
  List<Rating> findByGameId(UUID gameId);
}
