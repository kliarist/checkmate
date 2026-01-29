package com.checkmate.chess.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.checkmate.chess.model.Game;
import com.checkmate.chess.model.Move;
import com.checkmate.chess.model.User;
import com.checkmate.chess.repository.MoveRepository;

/**
 * Unit tests for PgnService.
 */
@ExtendWith(MockitoExtension.class)
class PgnServiceTest {

  @Mock
  private MoveRepository moveRepository;

  @InjectMocks
  private PgnService pgnService;

  private Game testGame;
  private User whitePlayer;
  private User blackPlayer;

  @BeforeEach
  void setUp() {
    whitePlayer = new User();
    whitePlayer.setId(UUID.randomUUID());
    whitePlayer.setUsername("WhitePlayer");

    blackPlayer = new User();
    blackPlayer.setId(UUID.randomUUID());
    blackPlayer.setUsername("BlackPlayer");

    testGame = new Game();
    testGame.setId(UUID.randomUUID());
    testGame.setWhitePlayer(whitePlayer);
    testGame.setBlackPlayer(blackPlayer);
    testGame.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30));
    testGame.setTimeControl("10+0");
    testGame.setResult("WHITE_WINS");
  }

  @Test
  void generatePgn_shouldIncludeHeaders() {
    // Given
    when(moveRepository.findByGameIdOrderByMoveNumberAsc(testGame.getId()))
        .thenReturn(List.of());

    // When
    String pgn = pgnService.generatePgn(testGame);

    // Then
    assertThat(pgn).contains("[Event \"CheckMate Game\"]");
    assertThat(pgn).contains("[Site \"CheckMate.com\"]");
    assertThat(pgn).contains("[Date \"2024.01.15\"]");
    assertThat(pgn).contains("[White \"WhitePlayer\"]");
    assertThat(pgn).contains("[Black \"BlackPlayer\"]");
    assertThat(pgn).contains("[Result \"1-0\"]");
    assertThat(pgn).contains("[TimeControl \"10+0\"]");
  }

  @Test
  void generatePgn_shouldFormatMovesCorrectly() {
    // Given
    Move move1 = createMove(1, "e4");
    Move move2 = createMove(1, "e5");
    Move move3 = createMove(2, "Nf3");
    Move move4 = createMove(2, "Nc6");

    when(moveRepository.findByGameIdOrderByMoveNumberAsc(testGame.getId()))
        .thenReturn(Arrays.asList(move1, move2, move3, move4));

    // When
    String pgn = pgnService.generatePgn(testGame);

    // Then
    assertThat(pgn).contains("1. e4 e5");
    assertThat(pgn).contains("2. Nf3 Nc6");
  }

  @Test
  void generatePgn_shouldHandleWhiteWin() {
    // Given
    testGame.setResult("WHITE_WINS");
    when(moveRepository.findByGameIdOrderByMoveNumberAsc(testGame.getId()))
        .thenReturn(List.of());

    // When
    String pgn = pgnService.generatePgn(testGame);

    // Then
    assertThat(pgn).contains("[Result \"1-0\"]");
    assertThat(pgn).endsWith("1-0\n");
  }

  @Test
  void generatePgn_shouldHandleBlackWin() {
    // Given
    testGame.setResult("BLACK_WINS");
    when(moveRepository.findByGameIdOrderByMoveNumberAsc(testGame.getId()))
        .thenReturn(List.of());

    // When
    String pgn = pgnService.generatePgn(testGame);

    // Then
    assertThat(pgn).contains("[Result \"0-1\"]");
    assertThat(pgn).endsWith("0-1\n");
  }

  @Test
  void generatePgn_shouldHandleDraw() {
    // Given
    testGame.setResult("DRAW");
    when(moveRepository.findByGameIdOrderByMoveNumberAsc(testGame.getId()))
        .thenReturn(List.of());

    // When
    String pgn = pgnService.generatePgn(testGame);

    // Then
    assertThat(pgn).contains("[Result \"1/2-1/2\"]");
    assertThat(pgn).endsWith("1/2-1/2\n");
  }

  @Test
  void generatePgn_shouldHandleGuestPlayers() {
    // Given
    testGame.setWhitePlayer(null);
    testGame.setBlackPlayer(null);
    when(moveRepository.findByGameIdOrderByMoveNumberAsc(testGame.getId()))
        .thenReturn(List.of());

    // When
    String pgn = pgnService.generatePgn(testGame);

    // Then
    assertThat(pgn).contains("[White \"Guest\"]");
    assertThat(pgn).contains("[Black \"Guest\"]");
  }

  @Test
  void generatePgn_shouldHandleOngoingGame() {
    // Given
    testGame.setResult(null);
    when(moveRepository.findByGameIdOrderByMoveNumberAsc(testGame.getId()))
        .thenReturn(List.of());

    // When
    String pgn = pgnService.generatePgn(testGame);

    // Then
    assertThat(pgn).contains("[Result \"*\"]");
    assertThat(pgn).endsWith("*\n");
  }

  private Move createMove(int moveNumber, String algebraicNotation) {
    Move move = new Move();
    move.setMoveNumber(moveNumber);
    move.setAlgebraicNotation(algebraicNotation);
    return move;
  }
}
