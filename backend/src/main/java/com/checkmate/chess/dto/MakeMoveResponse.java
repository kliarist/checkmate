package com.checkmate.chess.dto;

public record MakeMoveResponse(
    String algebraicNotation, String fen, boolean isCheckmate, boolean isStalemate, boolean isCheck) {}

