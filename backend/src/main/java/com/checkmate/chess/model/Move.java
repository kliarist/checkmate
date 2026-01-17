package com.checkmate.chess.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "moves")
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

  public Move() {}

  public Move(
      Game game,
      Integer moveNumber,
      String playerColor,
      String algebraicNotation,
      String fenAfterMove) {
    this.game = game;
    this.moveNumber = moveNumber;
    this.playerColor = playerColor;
    this.algebraicNotation = algebraicNotation;
    this.fenAfterMove = fenAfterMove;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Game getGame() {
    return game;
  }

  public void setGame(Game game) {
    this.game = game;
  }

  public Integer getMoveNumber() {
    return moveNumber;
  }

  public void setMoveNumber(Integer moveNumber) {
    this.moveNumber = moveNumber;
  }

  public String getPlayerColor() {
    return playerColor;
  }

  public void setPlayerColor(String playerColor) {
    this.playerColor = playerColor;
  }

  public String getAlgebraicNotation() {
    return algebraicNotation;
  }

  public void setAlgebraicNotation(String algebraicNotation) {
    this.algebraicNotation = algebraicNotation;
  }

  public String getFenAfterMove() {
    return fenAfterMove;
  }

  public void setFenAfterMove(String fenAfterMove) {
    this.fenAfterMove = fenAfterMove;
  }

  public Integer getTimeRemaining() {
    return timeRemaining;
  }

  public void setTimeRemaining(Integer timeRemaining) {
    this.timeRemaining = timeRemaining;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}

