package com.checkmate.chess.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
public final class Game {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "white_player_id")
  private User whitePlayer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "black_player_id")
  private User blackPlayer;

  @Column(name = "game_type", nullable = false)
  private String gameType;

  @Column(name = "time_control")
  private String timeControl;

  @Column(name = "current_fen", columnDefinition = "TEXT")
  private String currentFen;

  @Column(columnDefinition = "TEXT")
  private String pgn;

  @Column(nullable = false)
  private String status;

  private String result;

  @Column(name = "end_reason")
  private String endReason;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "ended_at")
  private LocalDateTime endedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    if (currentFen == null) {
      currentFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    }
    if (status == null) {
      status = "IN_PROGRESS";
    }
  }

  public Game(final User whitePlayer, final User blackPlayer, final String gameType) {
    this.whitePlayer = whitePlayer;
    this.blackPlayer = blackPlayer;
    this.gameType = gameType;
  }

  public void endGame(final String result, final String endReason) {
    this.result = result;
    this.endReason = endReason;
    this.status = "FINISHED";
    this.endedAt = LocalDateTime.now();
  }
}

