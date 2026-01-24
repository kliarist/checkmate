package com.checkmate.chess.repository;

import com.checkmate.chess.model.Game;
import com.checkmate.chess.model.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, UUID> {
  List<Game> findByWhitePlayerOrBlackPlayer(User whitePlayer, User blackPlayer);

  List<Game> findByStatus(String status);
}

