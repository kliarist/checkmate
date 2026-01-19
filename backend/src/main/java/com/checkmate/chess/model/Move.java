package com.checkmate.chess.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "moves")
@Getter
@Setter
@NoArgsConstructor
public class Move {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "game_id", nullable = false)
  private Game game;

  @Column(name = "move_number", nullable = false)
  private Integer moveNumber;

  @Column(name = "player_color", nullable = false)
  private String playerColor;

  @Column(name = "algebraic_notation", nullable = false)
  private String algebraicNotation;

  @Column(name = "fen_after_move", nullable = false, columnDefinition = "TEXT")
  private String fenAfterMove;

  @Column(name = "time_remaining")
  private Integer timeRemaining;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  public Move(
      final Game game,
      final Integer moveNumber,
      final String playerColor,
      final String algebraicNotation,
      final String fenAfterMove) {
    this.game = game;
    this.moveNumber = moveNumber;
    this.playerColor = playerColor;
    this.algebraicNotation = algebraicNotation;
    this.fenAfterMove = fenAfterMove;
  }
}

