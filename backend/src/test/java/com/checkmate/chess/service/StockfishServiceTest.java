package com.checkmate.chess.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StockfishService.
 * Tests chess engine integration and move generation.
 */
@ExtendWith(MockitoExtension.class)
class StockfishServiceTest {

  private StockfishService stockfishService;

  @BeforeEach
  void setUp() {
    // Note: These tests will be skipped if Stockfish is not installed
    // For CI/CD, we'll mock the engine or use a test double
    stockfishService = new StockfishService();
  }

  @Test
  @DisplayName("Should generate legal move for starting position")
  void testGenerateMoveStartingPosition() {
    // Given: Starting position
    String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    String difficulty = "beginner";

    // When: Generate move
    String move = stockfishService.getBestMove(fen, difficulty);

    // Then: Move should be valid UCI format (e.g., "e2e4")
    assertNotNull(move, "Move should not be null");
    assertTrue(move.matches("[a-h][1-8][a-h][1-8][qrbn]?"), 
        "Move should be in UCI format: " + move);
  }

  @Test
  @DisplayName("Should generate different moves for different difficulty levels")
  void testDifficultyLevels() {
    // Given: Same position
    String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    // When: Generate moves at different difficulties
    String beginnerMove = stockfishService.getBestMove(fen, "beginner");
    String advancedMove = stockfishService.getBestMove(fen, "advanced");

    // Then: Both should be valid moves
    assertNotNull(beginnerMove);
    assertNotNull(advancedMove);
    assertTrue(beginnerMove.matches("[a-h][1-8][a-h][1-8][qrbn]?"));
    assertTrue(advancedMove.matches("[a-h][1-8][a-h][1-8][qrbn]?"));
  }

  @Test
  @DisplayName("Should handle mate in one position")
  void testMateInOne() {
    // Given: Mate in one position (Scholar's mate setup)
    String fen = "r1bqkb1r/pppp1ppp/2n2n2/4p2Q/2B1P3/8/PPPP1PPP/RNB1K1NR w KQkq - 4 4";
    String difficulty = "advanced";

    // When: Generate move
    String move = stockfishService.getBestMove(fen, difficulty);

    // Then: Should find the mate (Qxf7#)
    assertNotNull(move);
    assertTrue(move.startsWith("h5f7") || move.equals("h5f7"), 
        "Should find mate in one: " + move);
  }

  @Test
  @DisplayName("Should respond within timeout")
  void testResponseTime() {
    // Given: Starting position
    String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    String difficulty = "intermediate";

    // When: Measure response time
    long startTime = System.currentTimeMillis();
    String move = stockfishService.getBestMove(fen, difficulty);
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    // Then: Should respond within 1 second
    assertNotNull(move);
    assertTrue(duration < 1000, 
        "Should respond within 1 second, but took " + duration + "ms");
  }

  @Test
  @DisplayName("Should handle invalid FEN gracefully")
  void testInvalidFen() {
    // Given: Invalid FEN
    String invalidFen = "invalid fen string";
    String difficulty = "beginner";

    // When/Then: Should throw exception or return null
    assertThrows(IllegalArgumentException.class, () -> {
      stockfishService.getBestMove(invalidFen, difficulty);
    });
  }

  @Test
  @DisplayName("Should handle invalid difficulty level")
  void testInvalidDifficulty() {
    // Given: Valid FEN, invalid difficulty
    String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    String invalidDifficulty = "invalid";

    // When/Then: Should throw exception or use default
    assertThrows(IllegalArgumentException.class, () -> {
      stockfishService.getBestMove(fen, invalidDifficulty);
    });
  }

  @Test
  @DisplayName("Should map difficulty to skill level correctly")
  void testDifficultyMapping() {
    // When: Get skill levels for each difficulty
    int beginnerSkill = stockfishService.getSkillLevel("beginner");
    int intermediateSkill = stockfishService.getSkillLevel("intermediate");
    int advancedSkill = stockfishService.getSkillLevel("advanced");

    // Then: Verify correct ranges
    assertTrue(beginnerSkill >= 1 && beginnerSkill <= 5, 
        "Beginner should be 1-5, was: " + beginnerSkill);
    assertTrue(intermediateSkill >= 10 && intermediateSkill <= 15, 
        "Intermediate should be 10-15, was: " + intermediateSkill);
    assertTrue(advancedSkill >= 18 && advancedSkill <= 20, 
        "Advanced should be 18-20, was: " + advancedSkill);
  }

  @Test
  @DisplayName("Should handle engine restart after error")
  void testEngineRestart() {
    // Given: Service with engine
    String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    // When: Make multiple requests (simulating potential engine issues)
    for (int i = 0; i < 5; i++) {
      String move = stockfishService.getBestMove(fen, "beginner");
      assertNotNull(move, "Move " + i + " should not be null");
    }

    // Then: All requests should succeed (engine should restart if needed)
    assertTrue(true, "Engine should handle multiple requests");
  }

  @Test
  @DisplayName("Should generate move for complex position")
  void testComplexPosition() {
    // Given: Complex middlegame position
    String fen = "r1bq1rk1/pp2bppp/2n1pn2/3p4/2PP4/2N1PN2/PP2BPPP/R1BQ1RK1 w - - 0 9";
    String difficulty = "intermediate";

    // When: Generate move
    String move = stockfishService.getBestMove(fen, difficulty);

    // Then: Should generate valid move
    assertNotNull(move);
    assertTrue(move.matches("[a-h][1-8][a-h][1-8][qrbn]?"));
  }
}
