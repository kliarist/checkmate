package com.checkmate.chess.dto;

public record MoveDto(
    Integer moveNumber,
    String playerColor,
    String algebraicNotation) {}

