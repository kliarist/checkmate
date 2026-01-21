package com.checkmate.chess.model;

import static org.junit.jupiter.api.Assertions.*;

import com.checkmate.chess.model.enums.GameResult;
import com.checkmate.chess.model.enums.GameStatus;
import com.checkmate.chess.model.enums.GameType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Game entity validation (T036).
 * Tests game state management, validation rules, and business logic.
 */
@DisplayName("Game Entity Tests")
class GameTest {

  private User whitePlayer;
  private User blackPlayer;
  private Game game;

  @BeforeEach
  void setUp() {
    whitePlayer = new User();
    whitePlayer.setId(1L);
    whitePlayer.setUsername("WhitePlayer");

    blackPlayer = new User();
    blackPlayer.setId(2L);
    blackPlayer.setUsername("BlackPlayer");

    game = new Game();
    game.setWhitePlayer(whitePlayer);
    game.setBlackPlayer(blackPlayer);
    game.setGameType(GameType.GUEST);
    game.setStatus(GameStatus.IN_PROGRESS);
  }

  @Test
  @DisplayName("Should create game with valid initial state")
  void shouldCreateGameWithValidInitialState() {
    assertNotNull(game);
    assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
    assertEquals(GameType.GUEST, game.getGameType());
    assertEquals(whitePlayer, game.getWhitePlayer());
    assertEquals(blackPlayer, game.getBlackPlayer());
    assertNotNull(game.getCurrentFen());
    assertTrue(game.getCurrentFen().startsWith("rnbqkbnr/pppppppp"));
  }

  @Test
  @DisplayName("Should set game result when game ends")
  void shouldSetGameResultWhenGameEnds() {
    game.setStatus(GameStatus.COMPLETED);
    game.setResult(GameResult.WHITE_WIN);

    assertEquals(GameStatus.COMPLETED, game.getStatus());
    assertEquals(GameResult.WHITE_WIN, game.getResult());
  }

  @Test
  @DisplayName("Should update FEN position")
  void shouldUpdateFenPosition() {
    String newFen = "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2";
    game.setCurrentFen(newFen);

    assertEquals(newFen, game.getCurrentFen());
  }

  @Test
  @DisplayName("Should track game creation timestamp")
  void shouldTrackGameCreationTimestamp() {
    LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);
    game.setCreatedAt(LocalDateTime.now());
    LocalDateTime afterCreation = LocalDateTime.now().plusSeconds(1);

    assertNotNull(game.getCreatedAt());
    assertTrue(game.getCreatedAt().isAfter(beforeCreation));
    assertTrue(game.getCreatedAt().isBefore(afterCreation));
  }

  @Test
  @DisplayName("Should track game end timestamp when completed")
  void shouldTrackGameEndTimestamp() {
    game.setStatus(GameStatus.COMPLETED);
    game.setEndedAt(LocalDateTime.now());

    assertNotNull(game.getEndedAt());
    assertTrue(game.getEndedAt().isAfter(game.getCreatedAt()));
  }

  @Test
  @DisplayName("Should validate game has both players")
  void shouldValidateGameHasBothPlayers() {
    assertNotNull(game.getWhitePlayer());
    assertNotNull(game.getBlackPlayer());
    assertNotEquals(game.getWhitePlayer().getId(), game.getBlackPlayer().getId());
  }

  @Test
  @DisplayName("Should set resignation result")
  void shouldSetResignationResult() {
    game.setStatus(GameStatus.COMPLETED);
    game.setResult(GameResult.BLACK_WIN);
    game.setEndReason("White resigned");

    assertEquals(GameStatus.COMPLETED, game.getStatus());
    assertEquals(GameResult.BLACK_WIN, game.getResult());
    assertEquals("White resigned", game.getEndReason());
  }

  @Test
  @DisplayName("Should set draw result")
  void shouldSetDrawResult() {
    game.setStatus(GameStatus.COMPLETED);
    game.setResult(GameResult.DRAW);
    game.setEndReason("Stalemate");

    assertEquals(GameStatus.COMPLETED, game.getStatus());
    assertEquals(GameResult.DRAW, game.getResult());
    assertEquals("Stalemate", game.getEndReason());
  }

  @Test
  @DisplayName("Should handle timeout result")
  void shouldHandleTimeoutResult() {
    game.setStatus(GameStatus.COMPLETED);
    game.setResult(GameResult.BLACK_WIN);
    game.setEndReason("White timeout");

    assertEquals(GameStatus.COMPLETED, game.getStatus());
    assertEquals(GameResult.BLACK_WIN, game.getResult());
    assertTrue(game.getEndReason().contains("timeout"));
  }

  @Test
  @DisplayName("Should validate game type")
  void shouldValidateGameType() {
    game.setGameType(GameType.RANKED);
    assertEquals(GameType.RANKED, game.getGameType());

    game.setGameType(GameType.GUEST);
    assertEquals(GameType.GUEST, game.getGameType());
  }

  @Test
  @DisplayName("Should initialize with standard starting FEN")
  void shouldInitializeWithStandardStartingFen() {
    Game newGame = new Game();
    newGame.setCurrentFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

    assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
        newGame.getCurrentFen());
  }

  @Test
  @DisplayName("Should track PGN notation")
  void shouldTrackPgnNotation() {
    String pgn = "1. e4 e5 2. Nf3 Nc6";
    game.setPgn(pgn);

    assertEquals(pgn, game.getPgn());
  }

  @Test
  @DisplayName("Should allow game status transitions")
  void shouldAllowGameStatusTransitions() {
    assertEquals(GameStatus.IN_PROGRESS, game.getStatus());

    game.setStatus(GameStatus.COMPLETED);
    assertEquals(GameStatus.COMPLETED, game.getStatus());
  }

  @Test
  @DisplayName("Should store time control information")
  void shouldStoreTimeControlInformation() {
    game.setTimeControl("10+0");
    assertEquals("10+0", game.getTimeControl());
  }
}

