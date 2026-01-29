package com.checkmate.chess.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.checkmate.chess.model.Game;
import com.checkmate.chess.model.Move;

@Repository
public interface MoveRepository extends JpaRepository<Move, UUID> {
  List<Move> findByGameOrderByMoveNumberAsc(Game game);
  
  List<Move> findByGameIdOrderByMoveNumberAsc(UUID gameId);

  long countByGame(Game game);
}

