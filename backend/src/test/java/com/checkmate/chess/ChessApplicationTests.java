package com.checkmate.chess;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Basic integration test to verify Spring Boot application context loads successfully.
 */
@SpringBootTest
@ActiveProfiles("test")
class ChessApplicationTests {

  @Test
  void contextLoads() {
    // This test verifies that the Spring application context can be loaded
    // without any configuration errors
  }
}

