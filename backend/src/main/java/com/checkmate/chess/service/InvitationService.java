package com.checkmate.chess.service;

import java.security.SecureRandom;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.chess.model.GameInvitation;
import com.checkmate.chess.model.User;
import com.checkmate.chess.repository.GameInvitationRepository;
import com.checkmate.chess.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvitationService {

  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final int CODE_LENGTH = 8;
  private static final SecureRandom RANDOM = new SecureRandom();

  private final GameInvitationRepository invitationRepository;
  private final UserRepository userRepository;

  @Transactional
  public GameInvitation createInvitation(
      final UUID creatorId, final String timeControl, final String gameType) {
    final User creator = userRepository.findById(creatorId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    final String code = generateUniqueCode();
    final GameInvitation invitation = new GameInvitation(creator, code, timeControl, gameType);

    return invitationRepository.save(invitation);
  }

  public GameInvitation findByCode(final String code) {
    return invitationRepository.findByInvitationCode(code)
        .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));
  }

  public boolean isValid(final String code) {
    return invitationRepository.findByInvitationCode(code)
        .map(GameInvitation::isValid)
        .orElse(false);
  }

  @Transactional
  public void markAsUsed(final String code) {
    final GameInvitation invitation = findByCode(code);
    invitation.setStatus("USED");
    invitationRepository.save(invitation);
  }

  public void validateInvitation(final String code) {
    final GameInvitation invitation = findByCode(code);
    
    if (invitation.isExpired()) {
      throw new IllegalArgumentException("Invitation has expired");
    }
    
    if (!"PENDING".equals(invitation.getStatus())) {
      throw new IllegalArgumentException("Invitation is no longer valid");
    }
  }

  private String generateUniqueCode() {
    String code;
    do {
      code = generateCode();
    } while (invitationRepository.findByInvitationCode(code).isPresent());
    return code;
  }

  private String generateCode() {
    final StringBuilder code = new StringBuilder(CODE_LENGTH);
    for (int i = 0; i < CODE_LENGTH; i++) {
      code.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
    }
    return code.toString();
  }
}
