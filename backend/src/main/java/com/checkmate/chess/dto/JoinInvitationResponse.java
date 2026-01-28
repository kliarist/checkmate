package com.checkmate.chess.dto;

import java.util.UUID;

public record JoinInvitationResponse(UUID gameId, String playerColor) {}
