package com.checkmate.chess.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.checkmate.chess.model.GameClock;

/**
 * Repository for GameClock entity.
 * Provides access to chess clock data.
 */
@Repository
public interface GameClockRepository extends JpaRepository<GameClock, UUID> {

  /**
   * Find clock by game ID.
   *
   * @param gameId the game ID
   * @return optional clock
   */
  Optional<GameClock> findByGameId(UUID gameId);
}
