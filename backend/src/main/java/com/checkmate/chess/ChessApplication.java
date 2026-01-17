package com.checkmate.chess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for Chess Web Application backend.
 *
 * <p>This Spring Boot application provides:
 * <ul>
 *   <li>RESTful API for chess game management</li>
 *   <li>WebSocket support for real-time game updates</li>
 *   <li>JWT-based authentication and authorization</li>
 *   <li>PostgreSQL database integration</li>
 * </ul>
 */
@SpringBootApplication
public class ChessApplication {

  public static void main(String[] args) {
    SpringApplication.run(ChessApplication.class, args);
  }
}

