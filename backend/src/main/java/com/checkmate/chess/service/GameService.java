package com.checkmate.chess.service;

import com.checkmate.chess.dto.GameDto.*;
import com.checkmate.chess.exception.ResourceNotFoundException;
import com.checkmate.chess.model.Game;
import com.checkmate.chess.model.Move;
import com.checkmate.chess.model.User;
import com.checkmate.chess.repository.GameRepository;
import java.util.Random;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameService {

  private final GameRepository gameRepository;
  private final GuestService guestService;
  private final MoveService moveService;
  private final ChessRulesService chessRulesService;

  public GameService(
      GameRepository gameRepository,
      GuestService guestService,
      MoveService moveService,
      ChessRulesService chessRulesService) {
    this.gameRepository = gameRepository;
    this.guestService = guestService;
    this.moveService = moveService;
    this.chessRulesService = chessRulesService;
  }

  @Transactional
  public CreateGuestGameResponse createGuestGame(String guestUsername) {
    User guestUser = guestService.createGuestUser(guestUsername);
    User computerUser = guestService.createGuestUser("Computer");

    boolean guestIsWhite = new Random().nextBoolean();
    User whitePlayer = guestIsWhite ? guestUser : computerUser;
    User blackPlayer = guestIsWhite ? computerUser : guestUser;

    Game game = new Game(whitePlayer, blackPlayer, "GUEST");
    game = gameRepository.save(game);

    return new CreateGuestGameResponse(
        game.getId(), guestUser.getId(), guestIsWhite ? "white" : "black");
  }

  public Game findById(UUID gameId) {
    return gameRepository
        .findById(gameId)
        .orElseThrow(() -> new ResourceNotFoundException("Game not found: " + gameId));
  }

  public GameStateResponse getGameState(UUID gameId) {
    Game game = findById(gameId);
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
  public MakeMoveResponse makeMove(UUID gameId, String from, String to, String promotion) {
    Game game = findById(gameId);

    if (!"IN_PROGRESS".equals(game.getStatus())) {
      throw new IllegalStateException("Game is not in progress");
    }

    if (!chessRulesService.isLegalMove(game.getCurrentFen(), from, to)) {
      throw new IllegalArgumentException("Illegal move");
    }

    String newFen = chessRulesService.makeMove(game.getCurrentFen(), from, to, promotion);
    String notation = chessRulesService.getMoveNotation(game.getCurrentFen(), from, to);

    Move move = moveService.saveMove(game, notation, newFen);

    game.setCurrentFen(newFen);
    gameRepository.save(game);

    boolean isCheckmate = chessRulesService.isCheckmate(newFen);
    boolean isStalemate = chessRulesService.isStalemate(newFen);
    boolean isCheck = chessRulesService.isCheck(newFen);

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
  public void resignGame(UUID gameId, UUID playerId) {
    Game game = findById(gameId);
    game.endGame("RESIGNATION", "Player resigned");
    gameRepository.save(game);
  }
}

