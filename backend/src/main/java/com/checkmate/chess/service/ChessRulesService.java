package com.checkmate.chess.service;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import org.springframework.stereotype.Service;

@Service
public class ChessRulesService {

  public boolean isLegalMove(final String fen, final String from, final String to) {
    try {
      final Board board = new Board();
      board.loadFromFen(fen);

      final Square fromSquare = Square.valueOf(from.toUpperCase());
      final Square toSquare = Square.valueOf(to.toUpperCase());
      final Move move = new Move(fromSquare, toSquare);

      return board.legalMoves().contains(move);
    } catch (Exception e) {
      return false;
    }
  }

  public String makeMove(final String fen, final String from, final String to, final String promotion) {
    try {
      final Board board = new Board();
      board.loadFromFen(fen);

      final Square fromSquare = Square.valueOf(from.toUpperCase());
      final Square toSquare = Square.valueOf(to.toUpperCase());

      Move move;
      if (promotion != null && !promotion.isEmpty()) {
        final Piece promotionPiece = switch (promotion.toLowerCase()) {
          case "q" -> Piece.WHITE_QUEEN;
          case "r" -> Piece.WHITE_ROOK;
          case "b" -> Piece.WHITE_BISHOP;
          case "n" -> Piece.WHITE_KNIGHT;
          default -> Piece.WHITE_QUEEN;
        };
        move = new Move(fromSquare, toSquare, promotionPiece);
      } else {
        move = new Move(fromSquare, toSquare);
      }

      if (!board.legalMoves().contains(move)) {
        throw new IllegalArgumentException("Invalid move: " + from + " to " + to);
      }

      board.doMove(move);
      return board.getFen();
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid move: " + from + " to " + to, e);
    }
  }

  public boolean isCheckmate(final String fen) {
    try {
      final Board board = new Board();
      board.loadFromFen(fen);
      return board.isMated();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isStalemate(final String fen) {
    try {
      final Board board = new Board();
      board.loadFromFen(fen);
      return board.isStaleMate();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isCheck(final String fen) {
    try {
      final Board board = new Board();
      board.loadFromFen(fen);
      return board.isKingAttacked();
    } catch (Exception e) {
      return false;
    }
  }

  public String getMoveNotation(final String fen, final String from, final String to) {
    try {
      final Board board = new Board();
      board.loadFromFen(fen);

      final Square fromSquare = Square.valueOf(from.toUpperCase());
      final Square toSquare = Square.valueOf(to.toUpperCase());
      final Move move = new Move(fromSquare, toSquare);

      return move.toString();
    } catch (Exception e) {
      return from + to;
    }
  }
}
