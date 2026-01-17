package com.checkmate.chess.service;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import org.springframework.stereotype.Service;

@Service
public class ChessRulesService {

  public boolean isLegalMove(String fen, String from, String to) {
    try {
      Board board = new Board();
      board.loadFromFen(fen);
      Move move = new Move(Square.fromValue(from.toUpperCase()), Square.fromValue(to.toUpperCase()));
      return board.legalMoves().contains(move);
    } catch (Exception e) {
      return false;
    }
  }

  public String makeMove(String fen, String from, String to, String promotion) {
    Board board = new Board();
    board.loadFromFen(fen);

    Square fromSquare = Square.fromValue(from.toUpperCase());
    Square toSquare = Square.fromValue(to.toUpperCase());
    Move move = new Move(fromSquare, toSquare);

    board.doMove(move);
    return board.getFen();
  }

  public boolean isCheckmate(String fen) {
    Board board = new Board();
    board.loadFromFen(fen);
    return board.isMated();
  }

  public boolean isStalemate(String fen) {
    Board board = new Board();
    board.loadFromFen(fen);
    return board.isDraw();
  }

  public boolean isCheck(String fen) {
    Board board = new Board();
    board.loadFromFen(fen);
    return board.isKingAttacked();
  }

  public String getMoveNotation(String fen, String from, String to) {
    Board board = new Board();
    board.loadFromFen(fen);
    Square fromSquare = Square.fromValue(from.toUpperCase());
    Square toSquare = Square.fromValue(to.toUpperCase());
    Move move = new Move(fromSquare, toSquare);
    return move.toString();
  }
}

