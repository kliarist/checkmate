package com.checkmate.chess.dto;

import java.util.UUID;

public record CreateGuestGameResponse(
    UUID gameId, UUID guestUserId, String color, String token) {}

