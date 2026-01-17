package com.checkmate.chess.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "games")
public class Game {

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

  public Game() {}

  public Game(User whitePlayer, User blackPlayer, String gameType) {
    this.whitePlayer = whitePlayer;
    this.blackPlayer = blackPlayer;
    this.gameType = gameType;
  }

  public void endGame(String result, String endReason) {
    this.result = result;
    this.endReason = endReason;
    this.status = "FINISHED";
    this.endedAt = LocalDateTime.now();
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public User getWhitePlayer() {
    return whitePlayer;
  }

  public void setWhitePlayer(User whitePlayer) {
    this.whitePlayer = whitePlayer;
  }

  public User getBlackPlayer() {
    return blackPlayer;
  }

  public void setBlackPlayer(User blackPlayer) {
    this.blackPlayer = blackPlayer;
  }

  public String getGameType() {
    return gameType;
  }

  public void setGameType(String gameType) {
    this.gameType = gameType;
  }

  public String getTimeControl() {
    return timeControl;
  }

  public void setTimeControl(String timeControl) {
    this.timeControl = timeControl;
  }

  public String getCurrentFen() {
    return currentFen;
  }

  public void setCurrentFen(String currentFen) {
    this.currentFen = currentFen;
  }

  public String getPgn() {
    return pgn;
  }

  public void setPgn(String pgn) {
    this.pgn = pgn;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public String getEndReason() {
    return endReason;
  }

  public void setEndReason(String endReason) {
    this.endReason = endReason;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getEndedAt() {
    return endedAt;
  }
}

