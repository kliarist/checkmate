package com.checkmate.chess.dto;

public record MakeMoveRequest(String from, String to, String promotion, String difficulty) {}

