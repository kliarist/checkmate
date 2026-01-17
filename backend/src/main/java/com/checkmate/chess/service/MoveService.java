package com.checkmate.chess.service;

import com.checkmate.chess.model.Game;
import com.checkmate.chess.model.Move;
import com.checkmate.chess.repository.MoveRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MoveService {

  private final MoveRepository moveRepository;

  public MoveService(MoveRepository moveRepository) {
    this.moveRepository = moveRepository;
  }

  @Transactional
  public Move saveMove(Game game, String notation, String fenAfterMove) {
    long moveCount = moveRepository.countByGame(game);
    int moveNumber = (int) (moveCount / 2) + 1;
    String color = moveCount % 2 == 0 ? "white" : "black";

    Move move = new Move(game, moveNumber, color, notation, fenAfterMove);
    return moveRepository.save(move);
  }

  public List<Move> getGameMoves(Game game) {
    return moveRepository.findByGameOrderByMoveNumberAsc(game);
  }
}

