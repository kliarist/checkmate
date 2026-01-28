package com.checkmate.chess.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateInvitationResponse(
    UUID invitationId,
    String invitationCode,
    String invitationLink,
    LocalDateTime expiresAt) {}
