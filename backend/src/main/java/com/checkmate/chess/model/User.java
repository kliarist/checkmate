package com.checkmate.chess.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(name = "elo_rating")
  private Integer eloRating = 1500;

  @Column(name = "games_played")
  private Integer gamesPlayed = 0;

  @Column(name = "wins")
  private Integer wins = 0;

  @Column(name = "losses")
  private Integer losses = 0;

  @Column(name = "draws")
  private Integer draws = 0;

  @Column(name = "is_guest")
  private Boolean isGuest = false;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public User() {}

  public User(String email, String username, String passwordHash) {
    this.email = email;
    this.username = username;
    this.passwordHash = passwordHash;
  }

  public static User createGuest(String username) {
    User guest = new User();
    guest.setUsername(username);
    guest.setEmail(username + "@guest.local");
    guest.setPasswordHash("");
    guest.setIsGuest(true);
    return guest;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public Integer getEloRating() {
    return eloRating;
  }

  public void setEloRating(Integer eloRating) {
    this.eloRating = eloRating;
  }

  public Integer getGamesPlayed() {
    return gamesPlayed;
  }

  public void setGamesPlayed(Integer gamesPlayed) {
    this.gamesPlayed = gamesPlayed;
  }

  public Integer getWins() {
    return wins;
  }

  public void setWins(Integer wins) {
    this.wins = wins;
  }

  public Integer getLosses() {
    return losses;
  }

  public void setLosses(Integer losses) {
    this.losses = losses;
  }

  public Integer getDraws() {
    return draws;
  }

  public void setDraws(Integer draws) {
    this.draws = draws;
  }

  public Boolean getIsGuest() {
    return isGuest;
  }

  public void setIsGuest(Boolean guest) {
    isGuest = guest;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}

