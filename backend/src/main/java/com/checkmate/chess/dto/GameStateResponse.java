package com.checkmate.chess.dto;

import java.util.UUID;

public record GameStateResponse(
    UUID gameId,
    String currentFen,
    String status,
    String result,
    String pgn,
    UUID whitePlayerId,
    UUID blackPlayerId) {}

