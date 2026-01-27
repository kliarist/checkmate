package com.checkmate.chess.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity representing a rating history entry.
 * Tracks ELO rating changes over time for analysis.
 */
@Entity
@Table(name = "ratings")
public class Rating {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "game_id", nullable = false)
  private UUID gameId;

  @Column(name = "old_rating", nullable = false)
  private Integer oldRating;

  @Column(name = "new_rating", nullable = false)
  private Integer newRating;

  @Column(name = "rating_change", nullable = false)
  private Integer ratingChange;

  @Column(name = "opponent_rating", nullable = false)
  private Integer opponentRating;

  @Column(name = "game_result", nullable = false)
  private String gameResult; // "win", "loss", "draw"

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  // Constructors
  public Rating() {
    this.createdAt = LocalDateTime.now();
  }

  // Getters and Setters
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public UUID getGameId() {
    return gameId;
  }

  public void setGameId(UUID gameId) {
    this.gameId = gameId;
  }

  public Integer getOldRating() {
    return oldRating;
  }

  public void setOldRating(Integer oldRating) {
    this.oldRating = oldRating;
  }

  public Integer getNewRating() {
    return newRating;
  }

  public void setNewRating(Integer newRating) {
    this.newRating = newRating;
  }

  public Integer getRatingChange() {
    return ratingChange;
  }

  public void setRatingChange(Integer ratingChange) {
    this.ratingChange = ratingChange;
  }

  public Integer getOpponentRating() {
    return opponentRating;
  }

  public void setOpponentRating(Integer opponentRating) {
    this.opponentRating = opponentRating;
  }

  public String getGameResult() {
    return gameResult;
  }

  public void setGameResult(String gameResult) {
    this.gameResult = gameResult;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
