package com.checkmate.chess.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Chess Rules Service Tests")
class ChessRulesServiceTest {

  private ChessRulesService chessRulesService;

  @BeforeEach
  void setUp() {
    chessRulesService = new ChessRulesService();
  }

  @Test
  @DisplayName("Should validate legal pawn move")
  void shouldValidateLegalPawnMove() {
    final String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    final boolean isValid = chessRulesService.isLegalMove(fen, "e2", "e4");

    assertThat(isValid).isTrue();
  }

  @Test
  @DisplayName("Should reject illegal pawn move")
  void shouldRejectIllegalPawnMove() {
    final String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    final boolean isValid = chessRulesService.isLegalMove(fen, "e2", "e5");

    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("Should validate legal knight move")
  void shouldValidateLegalKnightMove() {
    final String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    final boolean isValid = chessRulesService.isLegalMove(fen, "g1", "f3");

    assertThat(isValid).isTrue();
  }

  @Test
  @DisplayName("Should reject illegal knight move")
  void shouldRejectIllegalKnightMove() {
    final String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    final boolean isValid = chessRulesService.isLegalMove(fen, "g1", "e2");

    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("Should validate pawn capture")
  void shouldValidatePawnCapture() {
    final String fen = "rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq - 0 1";

    final boolean isValid = chessRulesService.isLegalMove(fen, "e7", "e5");

    assertThat(isValid).isTrue();
  }

  @Test
  @DisplayName("Should make valid move and return new FEN")
  void shouldMakeValidMoveAndReturnNewFen() {
    final String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    final String newFen = chessRulesService.makeMove(fen, "e2", "e4", null);

    assertThat(newFen)
        .isNotNull()
        .contains("b KQkq e3");
  }

  @Test
  @DisplayName("Should handle invalid move gracefully")
  void shouldHandleInvalidMoveGracefully() {
    final String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    assertThatThrownBy(() -> chessRulesService.makeMove(fen, "e2", "e5", null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid move");
  }

  @Test
  @DisplayName("Should validate bishop move")
  void shouldValidateBishopMove() {
    final String fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1";

    final boolean isValid = chessRulesService.isLegalMove(fen, "f1", "b5");

    assertThat(isValid).isTrue();
  }

  @Test
  @DisplayName("Should reject blocked bishop move")
  void shouldRejectBlockedBishopMove() {
    final String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    final boolean isValid = chessRulesService.isLegalMove(fen, "f1", "b5");

    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("Should validate castle kingside")
  void shouldValidateCastleKingside() {
    final String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQK2R w KQkq - 0 1";

    final boolean isValid = chessRulesService.isLegalMove(fen, "e1", "g1");

    assertThat(isValid).isTrue();
  }

  @Test
  @DisplayName("Should reject castle when blocked")
  void shouldRejectCastleWhenBlocked() {
    final String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    final boolean isValid = chessRulesService.isLegalMove(fen, "e1", "g1");

    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("Should handle pawn promotion")
  void shouldHandlePawnPromotion() {
    final String fen = "4k3/P7/8/8/8/8/8/4K3 w - - 0 1";

    final String newFen = chessRulesService.makeMove(fen, "a7", "a8", "q");

    assertThat(newFen).isNotNull();
  }

  @Test
  @DisplayName("Should validate rook move")
  void shouldValidateRookMove() {
    final String fen = "rnbqkbnr/pppppppp/8/8/8/P7/1PPPPPPP/RNBQKBNR w KQkq - 0 1";

    final boolean isValid = chessRulesService.isLegalMove(fen, "a1", "a2");

    assertThat(isValid).isTrue();
  }

  @Test
  @DisplayName("Should validate queen move")
  void shouldValidateQueenMove() {
    final String fen = "rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR w KQkq - 0 1";

    final boolean isValid = chessRulesService.isLegalMove(fen, "d1", "d3");

    assertThat(isValid).isTrue();
  }

  @Test
  @DisplayName("Should validate king move")
  void shouldValidateKingMove() {
    final String fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1";

    final boolean isValid = chessRulesService.isLegalMove(fen, "e1", "e2");

    assertThat(isValid).isTrue();
  }

  @Test
  @DisplayName("Should reject king move into check")
  void shouldRejectKingMoveIntoCheck() {
    final String fen = "rnb1kbnr/pppppppp/8/8/4q3/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    final boolean isValid = chessRulesService.isLegalMove(fen, "e1", "e2");

    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("Should handle invalid FEN gracefully")
  void shouldHandleInvalidFenGracefully() {
    final String invalidFen = "invalid fen string";

    final boolean isValid = chessRulesService.isLegalMove(invalidFen, "e2", "e4");

    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("Should handle invalid square notation")
  void shouldHandleInvalidSquareNotation() {
    final String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    final boolean isValid = chessRulesService.isLegalMove(fen, "z9", "a1");

    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("Should validate en passant capture")
  void shouldValidateEnPassantCapture() {
    final String fen = "rnbqkbnr/ppp1pppp/8/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 1";

    final boolean isValid = chessRulesService.isLegalMove(fen, "e5", "d6");

    assertThat(isValid).isTrue();
  }
}

