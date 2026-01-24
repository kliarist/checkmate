package com.checkmate.chess.service;

import com.checkmate.chess.dto.MoveDto;
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
    final long moveCount = moveRepository.countByGame(game);
    final int moveNumber = (int) (moveCount / 2) + 1;
    final String color = moveCount % 2 == 0 ? "white" : "black";

    final Move move = new Move(game, moveNumber, color, notation, fenAfterMove);
    final Move savedMove = moveRepository.save(move);

    final List<Move> allMoves = moveRepository.findByGameOrderByMoveNumberAsc(game);
    final StringBuilder pgnBuilder = new StringBuilder();

    for (int i = 0; i < allMoves.size(); i++) {
      final Move m = allMoves.get(i);
      if (i % 2 == 0) {
        pgnBuilder.append(m.getMoveNumber()).append(". ");
      }
      pgnBuilder.append(m.getAlgebraicNotation()).append(" ");
    }

    game.setPgn(pgnBuilder.toString().trim());

    return savedMove;
  }

  public List<Move> getGameMoves(final Game game) {
    return moveRepository.findByGameOrderByMoveNumberAsc(game);
  }

  public List<MoveDto> getGameMovesAsDto(final Game game) {
    return moveRepository.findByGameOrderByMoveNumberAsc(game).stream()
        .map(move -> new MoveDto(
            move.getMoveNumber(),
            move.getPlayerColor(),
            move.getAlgebraicNotation()))
        .toList();
  }
}

