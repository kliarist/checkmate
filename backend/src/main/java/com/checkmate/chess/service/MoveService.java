package com.checkmate.chess.service;

import com.checkmate.chess.model.Game;
import com.checkmate.chess.model.Move;
import com.checkmate.chess.repository.MoveRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MoveService {

  private final MoveRepository moveRepository;

  @Transactional
  public Move saveMove(final Game game, final String notation, final String fenAfterMove) {
    final var moveCount = moveRepository.countByGame(game);
    final var moveNumber = (int) (moveCount / 2) + 1;
    final var color = moveCount % 2 == 0 ? "white" : "black";

    final var move = new Move(game, moveNumber, color, notation, fenAfterMove);
    return moveRepository.save(move);
  }

  public List<Move> getGameMoves(final Game game) {
    return moveRepository.findByGameOrderByMoveNumberAsc(game);
  }
}

