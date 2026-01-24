package com.checkmate.chess.exception;

public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(final String message) {
    super(message);
  }
}

