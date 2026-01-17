package com.checkmate.chess.service;

import com.checkmate.chess.dto.CreateGuestGameResponse;
import com.checkmate.chess.dto.GameStateResponse;
import com.checkmate.chess.dto.MakeMoveResponse;
import com.checkmate.chess.exception.ResourceNotFoundException;
import com.checkmate.chess.model.Game;
import com.checkmate.chess.repository.GameRepository;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public final class GameService {

  private final GameRepository gameRepository;
  private final GuestService guestService;
  private final MoveService moveService;
  private final ChessRulesService chessRulesService;

  @Transactional
  public CreateGuestGameResponse createGuestGame(final String guestUsername) {
    final var guestUser = guestService.createGuestUser(guestUsername);
    final var computerUser = guestService.createGuestUser("Computer");

    final var guestIsWhite = new Random().nextBoolean();
    final var whitePlayer = guestIsWhite ? guestUser : computerUser;
    final var blackPlayer = guestIsWhite ? computerUser : guestUser;

    var game = new Game(whitePlayer, blackPlayer, "GUEST");
    game = gameRepository.save(game);

    return new CreateGuestGameResponse(
        game.getId(), guestUser.getId(), guestIsWhite ? "white" : "black");
  }

  public Game findById(final UUID gameId) {
    return gameRepository
        .findById(gameId)
        .orElseThrow(() -> new ResourceNotFoundException("Game not found: " + gameId));
  }

  public GameStateResponse getGameState(final UUID gameId) {
    final var game = findById(gameId);
    return new GameStateResponse(
        game.getId(),
        game.getCurrentFen(),
        game.getStatus(),
        game.getResult(),
        game.getPgn(),
        game.getWhitePlayer().getId(),
        game.getBlackPlayer().getId());
  }

  @Transactional
  public MakeMoveResponse makeMove(final UUID gameId, final String from, final String to, final String promotion) {
    final var game = findById(gameId);

    if (!"IN_PROGRESS".equals(game.getStatus())) {
      throw new IllegalStateException("Game is not in progress");
    }

    if (!chessRulesService.isLegalMove(game.getCurrentFen(), from, to)) {
      throw new IllegalArgumentException("Illegal move");
    }

    final var newFen = chessRulesService.makeMove(game.getCurrentFen(), from, to, promotion);
    final var notation = chessRulesService.getMoveNotation(game.getCurrentFen(), from, to);

    moveService.saveMove(game, notation, newFen);

    game.setCurrentFen(newFen);
    gameRepository.save(game);

    final var isCheckmate = chessRulesService.isCheckmate(newFen);
    final var isStalemate = chessRulesService.isStalemate(newFen);
    final var isCheck = chessRulesService.isCheck(newFen);

    if (isCheckmate) {
      game.endGame("CHECKMATE", "Checkmate");
      gameRepository.save(game);
    } else if (isStalemate) {
      game.endGame("DRAW", "Stalemate");
      gameRepository.save(game);
    }

    return new MakeMoveResponse(notation, newFen, isCheckmate, isStalemate, isCheck);
  }

  @Transactional
  public void resignGame(final UUID gameId, final UUID playerId) {
    final var game = findById(gameId);
    game.endGame("RESIGNATION", "Player resigned");
    gameRepository.save(game);
  }
}

