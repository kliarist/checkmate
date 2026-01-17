package com.checkmate.chess.exception;

import com.checkmate.chess.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });

    ErrorResponse response =
        new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            errors.toString(),
            LocalDateTime.now());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
    ErrorResponse response =
        new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Authentication failed",
            "Invalid email or password",
            LocalDateTime.now());

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
    ErrorResponse response =
        new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Resource not found",
            ex.getMessage(),
            LocalDateTime.now());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
    ErrorResponse response =
        new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal server error",
            ex.getMessage(),
            LocalDateTime.now());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}

