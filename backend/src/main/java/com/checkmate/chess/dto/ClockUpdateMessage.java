package com.checkmate.chess.dto;

/**
 * DTO for chess clock updates sent via WebSocket.
 */
public record ClockUpdateMessage(
    Long whiteTimeMs,
    Long blackTimeMs,
    String currentTurn
) {}
