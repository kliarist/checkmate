package com.checkmate.chess.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Game Entity Tests")
class GameTest {

  private User whitePlayer;
  private User blackPlayer;
  private Game game;

  @BeforeEach
  void setUp() {
    whitePlayer = new User();
    whitePlayer.setId(java.util.UUID.randomUUID());
    whitePlayer.setUsername("WhitePlayer");

    blackPlayer = new User();
    blackPlayer.setId(java.util.UUID.randomUUID());
    blackPlayer.setUsername("BlackPlayer");

    game = new Game(whitePlayer, blackPlayer, "GUEST");
  }

  @Test
  @DisplayName("Should create game with players")
  void shouldCreateGameWithPlayers() {
    assertThat(game).isNotNull();
    assertThat(game.getWhitePlayer()).isEqualTo(whitePlayer);
    assertThat(game.getBlackPlayer()).isEqualTo(blackPlayer);
    assertThat(game.getGameType()).isEqualTo("GUEST");
  }

  @Test
  @DisplayName("Should initialize with default FEN on persist")
  void shouldInitializeWithDefaultFen() {
    game.onCreate();

    assertThat(game.getCurrentFen())
        .isEqualTo("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    assertThat(game.getStatus()).isEqualTo("IN_PROGRESS");
    assertThat(game.getCreatedAt()).isNotNull();
  }

  @Test
  @DisplayName("Should end game with result")
  void shouldEndGameWithResult() {
    game.endGame("WHITE_WIN", "Checkmate");

    assertThat(game.getResult()).isEqualTo("WHITE_WIN");
    assertThat(game.getEndReason()).isEqualTo("Checkmate");
    assertThat(game.getStatus()).isEqualTo("FINISHED");
    assertThat(game.getEndedAt()).isNotNull();
  }

  @Test
  @DisplayName("Should set time control")
  void shouldSetTimeControl() {
    game.setTimeControl("10+0");

    assertThat(game.getTimeControl()).isEqualTo("10+0");
  }

  @Test
  @DisplayName("Should update FEN position")
  void shouldUpdateFenPosition() {
    final String newFen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
    game.setCurrentFen(newFen);

    assertThat(game.getCurrentFen()).isEqualTo(newFen);
  }

  @Test
  @DisplayName("Should set PGN")
  void shouldSetPgn() {
    final String pgn = "1. e4 e5 2. Nf3 Nc6";
    game.setPgn(pgn);

    assertThat(game.getPgn()).isEqualTo(pgn);
  }

  @Test
  @DisplayName("Should track game status")
  void shouldTrackGameStatus() {
    game.onCreate();
    assertThat(game.getStatus()).isEqualTo("IN_PROGRESS");

    game.setStatus("FINISHED");
    assertThat(game.getStatus()).isEqualTo("FINISHED");
  }

  @Test
  @DisplayName("Should create game with different types")
  void shouldCreateGameWithDifferentTypes() {
    final Game rankedGame = new Game(whitePlayer, blackPlayer, "RANKED");
    assertThat(rankedGame.getGameType()).isEqualTo("RANKED");

    final Game guestGame = new Game(whitePlayer, blackPlayer, "GUEST");
    assertThat(guestGame.getGameType()).isEqualTo("GUEST");
  }

  @Test
  @DisplayName("Should track created timestamp")
  void shouldTrackCreatedTimestamp() {
    final LocalDateTime before = LocalDateTime.now();
    game.onCreate();
    final LocalDateTime after = LocalDateTime.now();

    assertThat(game.getCreatedAt())
        .isNotNull()
        .isAfterOrEqualTo(before)
        .isBeforeOrEqualTo(after);
  }

  @Test
  @DisplayName("Should track ended timestamp when game ends")
  void shouldTrackEndedTimestamp() {
    final LocalDateTime before = LocalDateTime.now();
    game.endGame("DRAW", "Stalemate");
    final LocalDateTime after = LocalDateTime.now();

    assertThat(game.getEndedAt())
        .isNotNull()
        .isAfterOrEqualTo(before)
        .isBeforeOrEqualTo(after);
  }
}

