package com.checkmate.chess.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.checkmate.chess.model.GameInvitation;

@Repository
public interface GameInvitationRepository extends JpaRepository<GameInvitation, UUID> {

  Optional<GameInvitation> findByInvitationCode(String invitationCode);

  List<GameInvitation> findByStatusAndExpiresAtBefore(String status, LocalDateTime dateTime);

  List<GameInvitation> findByCreatorId(UUID creatorId);
}
