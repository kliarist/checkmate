package com.checkmate.chess.service;

import org.springframework.stereotype.Service;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

@Service
public class ChessRulesService {

  public boolean isLegalMove(final String fen, final String from, final String to) {
    try {
      final Board board = new Board();
      board.loadFromFen(fen);

      final Square fromSquare = Square.valueOf(from.toUpperCase());
      final Square toSquare = Square.valueOf(to.toUpperCase());

      // For promotion moves, we need to check if ANY promotion move from->to is legal
      // because the promotion piece will be specified separately
      for (final Move legalMove : board.legalMoves()) {
        if (legalMove.getFrom().equals(fromSquare) && legalMove.getTo().equals(toSquare)) {
          return true;
        }
      }

      return false;
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

  public String getCurrentTurn(final String fen) {
    try {
      final Board board = new Board();
      board.loadFromFen(fen);
      return board.getSideToMove().name().toLowerCase();
    } catch (Exception e) {
      return "white";
    }
  }

  public String getMoveNotation(final String fen, final String from, final String to) {
    try {
      final Board board = new Board();
      board.loadFromFen(fen);

      final Square fromSquare = Square.valueOf(from.toUpperCase());
      final Square toSquare = Square.valueOf(to.toUpperCase());

      Move matchingMove = null;
      for (final Move legalMove : board.legalMoves()) {
        if (legalMove.getFrom().equals(fromSquare) && legalMove.getTo().equals(toSquare)) {
          matchingMove = legalMove;
          break;
        }
      }

      if (matchingMove == null) {
        return from.toLowerCase() + to.toLowerCase();
      }

      final Piece piece = board.getPiece(fromSquare);
      final Piece captured = board.getPiece(toSquare);
      final boolean isCapture = captured != Piece.NONE;

      board.doMove(matchingMove);
      final boolean isCheck = board.isKingAttacked();
      final boolean isCheckmate = board.isMated();
      board.undoMove();

      final StringBuilder san = new StringBuilder();

      if (matchingMove.toString().equals("O-O") || matchingMove.toString().equals("O-O-O")) {
        return matchingMove.toString() + (isCheckmate ? "#" : isCheck ? "+" : "");
      }

      final String pieceSymbol = piece.getFenSymbol().toUpperCase();

      if (!pieceSymbol.equals("P")) {
        san.append(pieceSymbol);

        boolean needsDisambiguation = false;
        String disambiguation = "";

        for (final Move otherMove : board.legalMoves()) {
          if (!otherMove.equals(matchingMove) &&
              otherMove.getTo().equals(toSquare) &&
              board.getPiece(otherMove.getFrom()) == piece) {
            needsDisambiguation = true;
            if (otherMove.getFrom().getFile() != fromSquare.getFile()) {
              disambiguation = String.valueOf(fromSquare.getFile().getNotation()).toLowerCase();
            } else if (otherMove.getFrom().getRank() != fromSquare.getRank()) {
              disambiguation = String.valueOf(fromSquare.getRank().getNotation());
            } else {
              disambiguation = fromSquare.toString().toLowerCase();
            }
            break;
          }
        }

        if (needsDisambiguation) {
          san.append(disambiguation);
        }
      } else if (isCapture) {
        san.append(String.valueOf(fromSquare.getFile().getNotation()).toLowerCase());
      }

      if (isCapture) {
        san.append("x");
      }

      san.append(toSquare.toString().toLowerCase());

      if (matchingMove.getPromotion() != Piece.NONE) {
        san.append("=").append(matchingMove.getPromotion().getFenSymbol().toUpperCase());
      }

      if (isCheckmate) {
        san.append("#");
      } else if (isCheck) {
        san.append("+");
      }

      return san.toString();
    } catch (Exception e) {
      return from + to;
    }
  }

  /**
   * Check if the current position has occurred three times (threefold repetition).
   * 
   * @param fen The current FEN position
   * @param positionHistory List of all FEN positions in the game
   * @return true if threefold repetition has occurred
   */
  public boolean isThreefoldRepetition(final String fen, final java.util.List<String> positionHistory) {
    if (positionHistory == null || positionHistory.isEmpty()) {
      return false;
    }

    // Extract just the position part of FEN (ignore move counters)
    final String currentPosition = extractPosition(fen);
    
    int count = 0;
    for (final String historicalFen : positionHistory) {
      if (extractPosition(historicalFen).equals(currentPosition)) {
        count++;
        if (count >= 3) {
          return true;
        }
      }
    }
    
    return false;
  }

  /**
   * Check if the fifty-move rule applies (50 moves without capture or pawn move).
   * 
   * @param fen The current FEN position
   * @return true if fifty-move rule applies
   */
  public boolean isFiftyMoveRule(final String fen) {
    try {
      final Board board = new Board();
      board.loadFromFen(fen);
      
      // The halfmove clock is the 5th field in FEN notation
      final String[] fenParts = fen.split(" ");
      if (fenParts.length >= 5) {
        final int halfmoveClock = Integer.parseInt(fenParts[4]);
        return halfmoveClock >= 100; // 100 half-moves = 50 full moves
      }
      
      return false;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Extract the position part of FEN (first 4 fields, ignoring move counters).
   */
  private String extractPosition(final String fen) {
    final String[] parts = fen.split(" ");
    if (parts.length >= 4) {
      return String.join(" ", parts[0], parts[1], parts[2], parts[3]);
    }
    return fen;
  }
}
