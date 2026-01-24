package com.checkmate.chess.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.checkmate.chess.model.Game;
import com.checkmate.chess.model.User;

@Repository
public interface GameRepository extends JpaRepository<Game, UUID> {
  List<Game> findByWhitePlayerOrBlackPlayer(User whitePlayer, User blackPlayer);

  List<Game> findByStatus(String status);
  
  Page<Game> findByWhitePlayerIdOrBlackPlayerId(Long whitePlayerId, Long blackPlayerId, Pageable pageable);
}

