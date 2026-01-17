package com.checkmate.chess.dto;

import java.util.UUID;

public record CreateGuestGameRequest(String guestUsername) {}

public record CreateGuestGameResponse(UUID gameId, UUID guestUserId, String color) {}

public record GameStateResponse(
    UUID gameId,
    String currentFen,
    String status,
    String result,
    String pgn,
    UUID whitePlayerId,
    UUID blackPlayerId) {}

public record MakeMoveRequest(String from, String to, String promotion) {}

public record MakeMoveResponse(
    String algebraicNotation, String fen, boolean isCheckmate, boolean isStalemate, boolean isCheck) {}

