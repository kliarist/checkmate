package com.checkmate.chess.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for Stockfish chess engine integration.
 * Generates computer moves using UCI protocol.
 */
@Service
public class StockfishService {

  private static final Logger logger = LoggerFactory.getLogger(StockfishService.class);
  private static final int MOVE_TIME_MS = 1000; // 1 second per move
  private static final Map<String, Integer> DIFFICULTY_LEVELS = new HashMap<>();

  static {
    DIFFICULTY_LEVELS.put("beginner", 3);      // Skill level 1-5
    DIFFICULTY_LEVELS.put("intermediate", 12); // Skill level 10-15
    DIFFICULTY_LEVELS.put("advanced", 19);     // Skill level 18-20
  }

  private Process stockfishProcess;
  private BufferedReader reader;
  private PrintWriter writer;
  private final Random random = new Random();

  /**
   * Get best move for given position and difficulty.
   *
   * @param fen the position in FEN notation
   * @param difficulty the difficulty level
   * @return the best move in UCI format (e.g., "e2e4")
   */
  public String getBestMove(String fen, String difficulty) {
    validateDifficulty(difficulty);
    int skillLevel = getSkillLevel(difficulty);
    
    try {
      ensureEngineRunning();
      return generateMove(fen, skillLevel);
    } catch (Exception e) {
      logger.error("Error generating move", e);
      // Fallback to random legal move if engine fails
      return generateFallbackMove(fen, skillLevel);
    }
  }

  /**
   * Get skill level for difficulty.
   *
   * @param difficulty the difficulty level
   * @return the Stockfish skill level (0-20)
   */
  public int getSkillLevel(String difficulty) {
    Integer level = DIFFICULTY_LEVELS.get(difficulty.toLowerCase());
    if (level == null) {
      throw new IllegalArgumentException("Invalid difficulty: " + difficulty);
    }
    return level;
  }

  /**
   * Ensure Stockfish engine is running.
   */
  private void ensureEngineRunning() throws IOException {
    if (stockfishProcess == null || !stockfishProcess.isAlive()) {
      startEngine();
    }
  }

  /**
   * Start Stockfish engine process.
   */
  private void startEngine() throws IOException {
    try {
      // Try to start Stockfish (assumes it's in PATH)
      stockfishProcess = new ProcessBuilder("stockfish").start();
      
      reader = new BufferedReader(
          new InputStreamReader(stockfishProcess.getInputStream()));
      writer = new PrintWriter(
          new OutputStreamWriter(stockfishProcess.getOutputStream()), true);

      // Initialize UCI
      sendCommand("uci");
      waitForResponse("uciok");
      
      logger.info("Stockfish engine started successfully");
    } catch (IOException e) {
      logger.warn("Stockfish not found in PATH, using fallback mode");
      throw e;
    }
  }

  /**
   * Generate move using Stockfish.
   */
  private String generateMove(String fen, int skillLevel) throws IOException {
    // Set skill level
    sendCommand("setoption name Skill Level value " + skillLevel);
    
    // Set position
    sendCommand("position fen " + fen);
    
    // Calculate move with time limit
    sendCommand("go movetime " + MOVE_TIME_MS);
    
    // Wait for bestmove response
    String bestMove = waitForBestMove();
    
    if (bestMove == null || bestMove.isEmpty()) {
      throw new IllegalStateException("No move generated");
    }
    
    return bestMove;
  }

  /**
   * Send command to engine.
   */
  private void sendCommand(String command) {
    writer.println(command);
    logger.debug("Sent to engine: {}", command);
  }

  /**
   * Wait for specific response from engine.
   */
  private void waitForResponse(String expected) throws IOException {
    String line;
    while ((line = reader.readLine()) != null) {
      logger.debug("Engine: {}", line);
      if (line.contains(expected)) {
        return;
      }
    }
  }

  /**
   * Wait for bestmove response.
   */
  private String waitForBestMove() throws IOException {
    String line;
    while ((line = reader.readLine()) != null) {
      logger.debug("Engine: {}", line);
      if (line.startsWith("bestmove")) {
        String[] parts = line.split(" ");
        if (parts.length >= 2) {
          return parts[1]; // Return the move (e.g., "e2e4")
        }
      }
    }
    return null;
  }

  /**
   * Generate fallback move when engine is unavailable.
   * Uses chesslib to generate a legal move based on difficulty.
   */
  private String generateFallbackMove(String fen, int skillLevel) {
    logger.warn("Using fallback move generation with chesslib (skill level: {})", skillLevel);
    
    try {
      // Use chesslib to parse position and generate legal moves
      com.github.bhlangonijr.chesslib.Board board = new com.github.bhlangonijr.chesslib.Board();
      board.loadFromFen(fen);
      
      // Get all legal moves
      java.util.List<com.github.bhlangonijr.chesslib.move.Move> legalMoves = board.legalMoves();
      
      if (legalMoves.isEmpty()) {
        throw new IllegalStateException("No legal moves available");
      }
      
      // Adjust move selection based on skill level
      // Skill level 0-20: lower = more random, higher = better moves
      com.github.bhlangonijr.chesslib.move.Move selectedMove;
      
      if (skillLevel <= 5) {
        // Beginner: completely random move
        selectedMove = legalMoves.get(random.nextInt(legalMoves.size()));
      } else if (skillLevel <= 10) {
        // Intermediate: prefer captures and checks, but still somewhat random
        java.util.List<com.github.bhlangonijr.chesslib.move.Move> goodMoves = new java.util.ArrayList<>();
        
        for (com.github.bhlangonijr.chesslib.move.Move move : legalMoves) {
          board.doMove(move);
          boolean isCheck = board.isKingAttacked();
          board.undoMove();
          
          // Prefer captures and checks
          if (move.toString().contains("x") || isCheck) {
            goodMoves.add(move);
          }
        }
        
        // 50% chance to pick from good moves, 50% random
        if (!goodMoves.isEmpty() && random.nextBoolean()) {
          selectedMove = goodMoves.get(random.nextInt(goodMoves.size()));
        } else {
          selectedMove = legalMoves.get(random.nextInt(legalMoves.size()));
        }
      } else {
        // Advanced: prefer captures, checks, and center control
        java.util.List<com.github.bhlangonijr.chesslib.move.Move> bestMoves = new java.util.ArrayList<>();
        
        for (com.github.bhlangonijr.chesslib.move.Move move : legalMoves) {
          board.doMove(move);
          boolean isCheck = board.isKingAttacked();
          board.undoMove();
          
          String moveStr = move.toString().toLowerCase();
          boolean isCapture = moveStr.contains("x");
          boolean isCenterMove = moveStr.contains("e4") || moveStr.contains("d4") || 
                                 moveStr.contains("e5") || moveStr.contains("d5");
          
          // Prefer captures, checks, and center moves
          if (isCapture || isCheck || isCenterMove) {
            bestMoves.add(move);
          }
        }
        
        // 70% chance to pick from best moves, 30% random
        if (!bestMoves.isEmpty() && random.nextInt(10) < 7) {
          selectedMove = bestMoves.get(random.nextInt(bestMoves.size()));
        } else {
          selectedMove = legalMoves.get(random.nextInt(legalMoves.size()));
        }
      }
      
      // Convert to UCI format (e.g., "e2e4")
      String uciMove = selectedMove.toString().toLowerCase();
      
      logger.info("Generated fallback move (skill {}): {}", skillLevel, uciMove);
      return uciMove;
      
    } catch (Exception e) {
      logger.error("Error generating fallback move", e);
      
      // Last resort: return a common opening move for starting position
      if (fen.equals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")) {
        String[] openingMoves = {"e2e4", "d2d4", "c2c4", "g1f3", "e2e3"};
        return openingMoves[random.nextInt(openingMoves.length)];
      }
      
      throw new IllegalStateException(
          "Stockfish not available and fallback move generation failed", e);
    }
  }

  /**
   * Validate difficulty level.
   */
  private void validateDifficulty(String difficulty) {
    if (difficulty == null || !DIFFICULTY_LEVELS.containsKey(difficulty.toLowerCase())) {
      throw new IllegalArgumentException(
          "Invalid difficulty. Must be one of: beginner, intermediate, advanced");
    }
  }

  /**
   * Shutdown engine.
   */
  public void shutdown() {
    if (stockfishProcess != null && stockfishProcess.isAlive()) {
      sendCommand("quit");
      stockfishProcess.destroy();
      logger.info("Stockfish engine shut down");
    }
  }
}
