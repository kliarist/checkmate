package com.checkmate.chess.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a chess clock for a game.
 * Tracks time remaining for each player.
 */
@Entity
@Table(name = "game_clocks")
@Getter
@Setter
@NoArgsConstructor
public class GameClock {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "game_id", nullable = false, unique = true)
  private UUID gameId;

  @Column(name = "white_time_ms", nullable = false)
  private Long whiteTimeMs;

  @Column(name = "black_time_ms", nullable = false)
  private Long blackTimeMs;

  @Column(name = "increment_ms", nullable = false)
  private Long incrementMs = 0L;

  @Column(name = "delay_ms", nullable = false)
  private Long delayMs = 0L;

  @Column(name = "current_turn", nullable = false)
  private String currentTurn = "white";

  @Column(name = "is_paused", nullable = false)
  private boolean isPaused = false;

  @Column(name = "last_move_time")
  private LocalDateTime lastMoveTime;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  public GameClock(UUID gameId, Long initialTimeMs, Long incrementMs, Long delayMs) {
    this.gameId = gameId;
    this.whiteTimeMs = initialTimeMs;
    this.blackTimeMs = initialTimeMs;
    this.incrementMs = incrementMs;
    this.delayMs = delayMs;
    this.createdAt = LocalDateTime.now();
    this.lastMoveTime = LocalDateTime.now();
  }
}
