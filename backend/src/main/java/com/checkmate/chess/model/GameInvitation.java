package com.checkmate.chess.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "game_invitations")
@Getter
@Setter
@NoArgsConstructor
public class GameInvitation {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_id", nullable = false)
  private User creator;

  @Column(name = "invitation_code", nullable = false, unique = true, length = 8)
  private String invitationCode;

  @Column(name = "time_control", length = 20)
  private String timeControl;

  @Column(name = "game_type", length = 20)
  private String gameType;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @Column(name = "status", nullable = false, length = 20)
  private String status;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
    if (status == null) {
      status = "PENDING";
    }
  }

  public GameInvitation(User creator, String invitationCode, String timeControl, String gameType) {
    this.creator = creator;
    this.invitationCode = invitationCode;
    this.timeControl = timeControl;
    this.gameType = gameType;
    this.createdAt = LocalDateTime.now();
    this.expiresAt = LocalDateTime.now().plusMinutes(10);
    this.status = "PENDING";
  }

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiresAt);
  }

  public boolean isValid() {
    return "PENDING".equals(status) && !isExpired();
  }
}
