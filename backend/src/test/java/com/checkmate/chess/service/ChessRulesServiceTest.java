package com.checkmate.chess.service;

import static org.junit.jupiter.api.Assertions.*;

import com.checkmate.chess.dto.MoveDto;
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
    String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    MoveDto move = new MoveDto("e2", "e4", null);

    boolean isValid = chessRulesService.isValidMove(fen, move);

    assertTrue(isValid, "e2-e4 should be a valid opening move");
  }

  @Test
  @DisplayName("Should reject illegal pawn move")
  void shouldRejectIllegalPawnMove() {
    String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    MoveDto move = new MoveDto("e2", "e5", null);

    boolean isValid = chessRulesService.isValidMove(fen, move);

    assertFalse(isValid, "e2-e5 should be invalid (pawn can't move 3 squares)");
  }

  @Test
  @DisplayName("Should validate legal knight move")
  void shouldValidateLegalKnightMove() {
    String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    MoveDto move = new MoveDto("g1", "f3", null);

    boolean isValid = chessRulesService.isValidMove(fen, move);

    assertTrue(isValid, "g1-f3 should be a valid knight move");
  }

  @Test
  @DisplayName("Should reject move to square occupied by own piece")
  void shouldRejectMoveToSquareOccupiedByOwnPiece() {
    String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    MoveDto move = new MoveDto("e1", "e2", null);

    boolean isValid = chessRulesService.isValidMove(fen, move);

    assertFalse(isValid, "King cannot move to square occupied by own piece");
  }

  @Test
  @DisplayName("Should validate pawn capture")
  void shouldValidatePawnCapture() {
    String fen = "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2";
    MoveDto move = new MoveDto("d2", "d4", null);
    String newFen = chessRulesService.makeMove(fen, move);

    MoveDto capture = new MoveDto("e5", "d4", null);
    boolean isValid = chessRulesService.isValidMove(newFen, capture);

    assertTrue(isValid, "Pawn should be able to capture diagonally");
  }

  @Test
  @DisplayName("Should detect checkmate")
  void shouldDetectCheckmate() {
    // Scholar's mate position
    String fen = "r1bqkb1r/pppp1Qpp/2n2n2/4p3/2B1P3/8/PPPP1PPP/RNB1K1NR b KQkq - 0 4";

    boolean isCheckmate = chessRulesService.isCheckmate(fen);

    assertTrue(isCheckmate, "Should detect checkmate");
  }

  @Test
  @DisplayName("Should detect stalemate")
  void shouldDetectStalemate() {
    // Stalemate position: black king has no legal moves but not in check
    String fen = "7k/5Q2/6K1/8/8/8/8/8 b - - 0 1";

    boolean isStalemate = chessRulesService.isStalemate(fen);

    assertTrue(isStalemate, "Should detect stalemate");
  }

  @Test
  @DisplayName("Should detect check")
  void shouldDetectCheck() {
    String fen = "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2";
    MoveDto move = new MoveDto("f1", "b5", null);
    String newFen = chessRulesService.makeMove(fen, move);

    boolean isCheck = chessRulesService.isCheck(newFen);

    assertTrue(isCheck, "Should detect check");
  }

  @Test
  @DisplayName("Should not detect check in normal position")
  void shouldNotDetectCheckInNormalPosition() {
    String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    boolean isCheck = chessRulesService.isCheck(fen);

    assertFalse(isCheck, "Starting position should not be check");
  }

  @Test
  @DisplayName("Should validate castling kingside")
  void shouldValidateCastlingKingside() {
    // Position where white can castle kingside
    String fen = "rnbqkbnr/pppppppp/8/8/8/5N2/PPPPPPPP/RNBQK2R w KQkq - 0 1";
    MoveDto move = new MoveDto("e1", "g1", null);

    boolean isValid = chessRulesService.isValidMove(fen, move);

    assertTrue(isValid, "Should allow kingside castling when conditions are met");
  }

  @Test
  @DisplayName("Should validate castling queenside")
  void shouldValidateCastlingQueenside() {
    // Position where white can castle queenside
    String fen = "rnbqkbnr/pppppppp/8/8/8/2N5/PPPPPPPP/R3KBNR w KQkq - 0 1";
    MoveDto move = new MoveDto("e1", "c1", null);

    boolean isValid = chessRulesService.isValidMove(fen, move);

    assertTrue(isValid, "Should allow queenside castling when conditions are met");
  }

  @Test
  @DisplayName("Should validate en passant capture")
  void shouldValidateEnPassantCapture() {
    // Position where en passant is possible
    String fen = "rnbqkbnr/ppp1pppp/8/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 2";
    MoveDto move = new MoveDto("e5", "d6", null);

    boolean isValid = chessRulesService.isValidMove(fen, move);

    assertTrue(isValid, "Should allow en passant capture");
  }

  @Test
  @DisplayName("Should validate pawn promotion")
  void shouldValidatePawnPromotion() {
    // White pawn on 7th rank
    String fen = "4k3/P7/8/8/8/8/8/4K3 w - - 0 1";
    MoveDto move = new MoveDto("a7", "a8", "q");

    boolean isValid = chessRulesService.isValidMove(fen, move);

    assertTrue(isValid, "Should allow pawn promotion");
  }

  @Test
  @DisplayName("Should reject pawn promotion without piece selection")
  void shouldRejectPawnPromotionWithoutPieceSelection() {
    String fen = "4k3/P7/8/8/8/8/8/4K3 w - - 0 1";
    MoveDto move = new MoveDto("a7", "a8", null);

    // This should require promotion piece
    assertThrows(IllegalArgumentException.class, () -> {
      chessRulesService.makeMove(fen, move);
    }, "Should require promotion piece when pawn reaches 8th rank");
  }

  @Test
  @DisplayName("Should make valid move and return new FEN")
  void shouldMakeValidMoveAndReturnNewFen() {
    String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    MoveDto move = new MoveDto("e2", "e4", null);

    String newFen = chessRulesService.makeMove(fen, move);

    assertNotNull(newFen);
    assertNotEquals(fen, newFen);
    assertTrue(newFen.contains("4P3"), "FEN should reflect pawn on e4");
  }

  @Test
  @DisplayName("Should throw exception for invalid move")
  void shouldThrowExceptionForInvalidMove() {
    String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    MoveDto move = new MoveDto("e2", "e5", null);

    assertThrows(IllegalArgumentException.class, () -> {
      chessRulesService.makeMove(fen, move);
    }, "Should throw exception for illegal move");
  }

  @Test
  @DisplayName("Should get legal moves for a piece")
  void shouldGetLegalMovesForPiece() {
    String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    var legalMoves = chessRulesService.getLegalMoves(fen, "e2");

    assertNotNull(legalMoves);
    assertTrue(legalMoves.size() >= 2, "Pawn should have at least 2 legal moves");
    assertTrue(legalMoves.contains("e3") || legalMoves.contains("e4"));
  }

  @Test
  @DisplayName("Should return empty list for piece with no legal moves")
  void shouldReturnEmptyListForPieceWithNoLegalMoves() {
    // Rook in corner blocked by own pieces
    String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    var legalMoves = chessRulesService.getLegalMoves(fen, "a1");

    assertNotNull(legalMoves);
    assertTrue(legalMoves.isEmpty(), "Rook blocked by pawns should have no legal moves");
  }

  @Test
  @DisplayName("Should validate game is not over in starting position")
  void shouldValidateGameIsNotOverInStartingPosition() {
    String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    boolean isGameOver = chessRulesService.isGameOver(fen);

    assertFalse(isGameOver, "Game should not be over in starting position");
  }

  @Test
  @DisplayName("Should validate game is over on checkmate")
  void shouldValidateGameIsOverOnCheckmate() {
    String fen = "r1bqkb1r/pppp1Qpp/2n2n2/4p3/2B1P3/8/PPPP1PPP/RNB1K1NR b KQkq - 0 4";

    boolean isGameOver = chessRulesService.isGameOver(fen);

    assertTrue(isGameOver, "Game should be over on checkmate");
  }

  @Test
  @DisplayName("Should get algebraic notation for move")
  void shouldGetAlgebraicNotationForMove() {
    String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    MoveDto move = new MoveDto("e2", "e4", null);

    String notation = chessRulesService.getAlgebraicNotation(fen, move);

    assertNotNull(notation);
    assertEquals("e4", notation, "Should return algebraic notation");
  }

  @Test
  @DisplayName("Should handle invalid FEN string")
  void shouldHandleInvalidFenString() {
    String invalidFen = "invalid-fen-string";
    MoveDto move = new MoveDto("e2", "e4", null);

    assertThrows(IllegalArgumentException.class, () -> {
      chessRulesService.isValidMove(invalidFen, move);
    }, "Should throw exception for invalid FEN");
  }

  @Test
  @DisplayName("Should get current turn from FEN")
  void shouldGetCurrentTurnFromFen() {
    String whiteTurnFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    String blackTurnFen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";

    assertEquals("white", chessRulesService.getCurrentTurn(whiteTurnFen));
    assertEquals("black", chessRulesService.getCurrentTurn(blackTurnFen));
  }
}

