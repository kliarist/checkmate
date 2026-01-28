package com.checkmate.chess.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.chess.model.GameInvitation;
import com.checkmate.chess.repository.GameInvitationRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InvitationExpirationScheduler {

  private static final Logger logger = LoggerFactory.getLogger(InvitationExpirationScheduler.class);
  private final GameInvitationRepository invitationRepository;

  @Scheduled(fixedRate = 60000) // Run every minute
  @Transactional
  public void expireOldInvitations() {
    final LocalDateTime now = LocalDateTime.now();
    final List<GameInvitation> expiredInvitations = 
        invitationRepository.findByStatusAndExpiresAtBefore("PENDING", now);

    if (!expiredInvitations.isEmpty()) {
      expiredInvitations.forEach(invitation -> {
        invitation.setStatus("EXPIRED");
        invitationRepository.save(invitation);
      });
      
      logger.info("Expired {} invitations", expiredInvitations.size());
    }
  }
}
