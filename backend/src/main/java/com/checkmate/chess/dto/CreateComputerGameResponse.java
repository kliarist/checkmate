package com.checkmate.chess.dto;

import java.util.UUID;

public record CreateComputerGameResponse(
    UUID gameId, UUID playerId, String color, String difficulty) {}
