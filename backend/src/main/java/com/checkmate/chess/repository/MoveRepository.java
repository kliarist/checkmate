package com.checkmate.chess.repository;

import com.checkmate.chess.model.Game;
import com.checkmate.chess.model.Move;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoveRepository extends JpaRepository<Move, UUID> {
  List<Move> findByGameOrderByMoveNumberAsc(Game game);

  long countByGame(Game game);
}

