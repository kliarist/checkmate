package com.checkmate.chess.dto;

public record SuccessResponse<T>(String message, T data) {}

