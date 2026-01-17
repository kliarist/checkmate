package com.checkmate.chess.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public final class User {

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

  public User(final String email, final String username, final String passwordHash) {
    this.email = email;
    this.username = username;
    this.passwordHash = passwordHash;
  }

  public static User createGuest(final String username) {
    final var guest = new User();
    guest.setUsername(username);
    guest.setEmail(username + "@guest.local");
    guest.setPasswordHash("");
    guest.setIsGuest(true);
    return guest;
  }
}

