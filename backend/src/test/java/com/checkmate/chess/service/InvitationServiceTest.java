package com.checkmate.chess.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.checkmate.chess.model.GameInvitation;
import com.checkmate.chess.model.User;
import com.checkmate.chess.repository.GameInvitationRepository;
import com.checkmate.chess.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Invitation Service Tests")
class InvitationServiceTest {

  @Autowired
  private InvitationService invitationService;

  @Autowired
  private GameInvitationRepository invitationRepository;

  @Autowired
  private UserRepository userRepository;

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = User.createGuest("TestUser");
    testUser = userRepository.save(testUser);
  }

  @Test
  @DisplayName("Should generate unique invitation code")
  void shouldGenerateUniqueInvitationCode() {
    final GameInvitation invitation = invitationService.createInvitation(
        testUser.getId(), "10+0", "PRIVATE");

    assertThat(invitation.getInvitationCode()).isNotNull();
    assertThat(invitation.getInvitationCode()).hasSize(8);
    assertThat(invitation.getInvitationCode()).matches("[A-Z0-9]{8}");
  }

  @Test
  @DisplayName("Should create invitation with correct expiration time")
  void shouldCreateInvitationWithCorrectExpirationTime() {
    final LocalDateTime before = LocalDateTime.now().plusMinutes(9);
    final GameInvitation invitation = invitationService.createInvitation(
        testUser.getId(), "10+0", "PRIVATE");
    final LocalDateTime after = LocalDateTime.now().plusMinutes(11);

    assertThat(invitation.getExpiresAt()).isAfter(before);
    assertThat(invitation.getExpiresAt()).isBefore(after);
  }

  @Test
  @DisplayName("Should set invitation status to PENDING")
  void shouldSetInvitationStatusToPending() {
    final GameInvitation invitation = invitationService.createInvitation(
        testUser.getId(), "10+0", "PRIVATE");

    assertThat(invitation.getStatus()).isEqualTo("PENDING");
  }

  @Test
  @DisplayName("Should find invitation by code")
  void shouldFindInvitationByCode() {
    final GameInvitation created = invitationService.createInvitation(
        testUser.getId(), "10+0", "PRIVATE");

    final GameInvitation found = invitationService.findByCode(created.getInvitationCode());

    assertThat(found).isNotNull();
    assertThat(found.getId()).isEqualTo(created.getId());
  }

  @Test
  @DisplayName("Should throw exception for non-existent invitation code")
  void shouldThrowExceptionForNonExistentCode() {
    assertThatThrownBy(() -> invitationService.findByCode("INVALID1"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invitation not found");
  }

  @Test
  @DisplayName("Should validate non-expired invitation")
  void shouldValidateNonExpiredInvitation() {
    final GameInvitation invitation = invitationService.createInvitation(
        testUser.getId(), "10+0", "PRIVATE");

    final boolean isValid = invitationService.isValid(invitation.getInvitationCode());

    assertThat(isValid).isTrue();
  }

  @Test
  @DisplayName("Should reject expired invitation")
  void shouldRejectExpiredInvitation() {
    final GameInvitation invitation = invitationService.createInvitation(
        testUser.getId(), "10+0", "PRIVATE");
    
    // Manually expire the invitation
    invitation.setExpiresAt(LocalDateTime.now().minusMinutes(1));
    invitationRepository.save(invitation);

    final boolean isValid = invitationService.isValid(invitation.getInvitationCode());

    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("Should reject used invitation")
  void shouldRejectUsedInvitation() {
    final GameInvitation invitation = invitationService.createInvitation(
        testUser.getId(), "10+0", "PRIVATE");
    
    // Mark as used
    invitation.setStatus("USED");
    invitationRepository.save(invitation);

    final boolean isValid = invitationService.isValid(invitation.getInvitationCode());

    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("Should mark invitation as used")
  void shouldMarkInvitationAsUsed() {
    final GameInvitation invitation = invitationService.createInvitation(
        testUser.getId(), "10+0", "PRIVATE");

    invitationService.markAsUsed(invitation.getInvitationCode());

    final GameInvitation updated = invitationRepository.findById(invitation.getId()).orElseThrow();
    assertThat(updated.getStatus()).isEqualTo("USED");
  }

  @Test
  @DisplayName("Should generate different codes for multiple invitations")
  void shouldGenerateDifferentCodes() {
    final GameInvitation inv1 = invitationService.createInvitation(
        testUser.getId(), "10+0", "PRIVATE");
    final GameInvitation inv2 = invitationService.createInvitation(
        testUser.getId(), "10+0", "PRIVATE");

    assertThat(inv1.getInvitationCode()).isNotEqualTo(inv2.getInvitationCode());
  }
}
