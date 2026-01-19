package com.checkmate.chess.service;

import org.springframework.stereotype.Service;

@Service
public class ChessRulesService {

  private static final String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

  public boolean isLegalMove(final String fen, final String from, final String to) {
    return true;
  }

  public String makeMove(final String fen, final String from, final String to, final String promotion) {
    return fen;
  }

  public boolean isCheckmate(final String fen) {
    return false;
  }

  public boolean isStalemate(final String fen) {
    return false;
  }

  public boolean isCheck(final String fen) {
    return false;
  }

  public String getMoveNotation(final String fen, final String from, final String to) {
    return from + to;
  }
}

