package com.checkmate.chess.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.checkmate.chess.model.MatchmakingQueue;

/**
 * Repository for MatchmakingQueue entity.
 * Provides access to matchmaking queue data.
 */
@Repository
public interface MatchmakingQueueRepository extends JpaRepository<MatchmakingQueue, UUID> {

  /**
   * Find all queue entries for a specific time control, ordered by creation time.
   *
   * @param timeControl the time control
   * @return list of queue entries
   */
  List<MatchmakingQueue> findByTimeControlOrderByCreatedAtAsc(String timeControl);

  /**
   * Find queue entry for a specific user.
   *
   * @param userId the user ID
   * @return optional queue entry
   */
  Optional<MatchmakingQueue> findByUserId(UUID userId);

  /**
   * Find all queue entries created before a specific time.
   *
   * @param time the cutoff time
   * @return list of expired entries
   */
  List<MatchmakingQueue> findByCreatedAtBefore(LocalDateTime time);
}
