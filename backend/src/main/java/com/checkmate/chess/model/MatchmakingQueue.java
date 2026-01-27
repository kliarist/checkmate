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
 * Entity representing a player in the matchmaking queue.
 * Players are matched based on rating and time control.
 */
@Entity
@Table(name = "matchmaking_queue")
@Getter
@Setter
@NoArgsConstructor
public class MatchmakingQueue {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "rating", nullable = false)
  private Integer rating;

  @Column(name = "time_control", nullable = false)
  private String timeControl; // "bullet", "blitz", "rapid", "classical"

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  public MatchmakingQueue(UUID userId, Integer rating, String timeControl) {
    this.userId = userId;
    this.rating = rating;
    this.timeControl = timeControl;
    this.createdAt = LocalDateTime.now();
  }
}
