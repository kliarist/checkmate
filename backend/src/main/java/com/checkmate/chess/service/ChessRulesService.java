package com.checkmate.chess.service;

import org.springframework.stereotype.Service;

@Service
public class ChessRulesService {

  private static final String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

  public boolean isLegalMove(String fen, String from, String to) {
    return true;
  }

  public String makeMove(String fen, String from, String to, String promotion) {
    return fen;
  }

  public boolean isCheckmate(String fen) {
    return false;
  }

  public boolean isStalemate(String fen) {
    return false;
  }

  public boolean isCheck(String fen) {
    return false;
  }

  public String getMoveNotation(String fen, String from, String to) {
    return from + to;
  }
}

