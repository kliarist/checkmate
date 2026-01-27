package com.checkmate.chess.service;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.chess.dto.CreateGuestGameResponse;
import com.checkmate.chess.dto.GameStateResponse;
import com.checkmate.chess.dto.MakeMoveResponse;
import com.checkmate.chess.dto.MoveDto;
import com.checkmate.chess.exception.ResourceNotFoundException;
import com.checkmate.chess.model.Game;
import com.checkmate.chess.repository.GameRepository;
import com.checkmate.chess.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameService {

  private final GameRepository gameRepository;
  private final GuestService guestService;
  private final MoveService moveService;
  private final ChessRulesService chessRulesService;
  private final JwtService jwtService;
  private final RatingService ratingService;

  @Transactional
  public CreateGuestGameResponse createGuestGame(final String guestUsername) {
    final com.checkmate.chess.model.User guestUser = guestService.createGuestUser(guestUsername);
    final com.checkmate.chess.model.User computerUser = guestService.createGuestUser("Computer");

    final boolean guestIsWhite = new Random().nextBoolean();
    final com.checkmate.chess.model.User whitePlayer = guestIsWhite ? guestUser : computerUser;
    final com.checkmate.chess.model.User blackPlayer = guestIsWhite ? computerUser : guestUser;

    Game game = new Game(whitePlayer, blackPlayer, "GUEST");
    game = gameRepository.save(game);

    final UserDetails userDetails = User.builder()
        .username(guestUser.getUsername())
        .password(guestUser.getPasswordHash())
        .authorities("ROLE_GUEST")
        .build();
    final String token = jwtService.generateToken(userDetails);

    return new CreateGuestGameResponse(
        game.getId(), guestUser.getId(), guestIsWhite ? "white" : "black", token);
  }

  public Game findById(final UUID gameId) {
    return gameRepository
        .findById(gameId)
        .orElseThrow(() -> new ResourceNotFoundException("Game not found: " + gameId));
  }

  public GameStateResponse getGameState(final UUID gameId) {
    final Game game = findById(gameId);
    return new GameStateResponse(
        game.getId(),
        game.getCurrentFen(),
        game.getStatus(),
        game.getResult(),
        game.getPgn(),
        game.getWhitePlayer().getId(),
        game.getBlackPlayer().getId());
  }

  public List<MoveDto> getGameMoves(final UUID gameId) {
    final Game game = findById(gameId);
    return moveService.getGameMovesAsDto(game);
  }

  @Transactional
  public MakeMoveResponse makeMove(final UUID gameId, final String from, final String to, final String promotion) {
    final Game game = findById(gameId);

    if (!"IN_PROGRESS".equals(game.getStatus())) {
      throw new IllegalStateException("Game is not in progress");
    }

    if (!chessRulesService.isLegalMove(game.getCurrentFen(), from, to)) {
      throw new IllegalArgumentException("Illegal move");
    }

    final String newFen = chessRulesService.makeMove(game.getCurrentFen(), from, to, promotion);
    final String notation = chessRulesService.getMoveNotation(game.getCurrentFen(), from, to);

    moveService.saveMove(game, notation, newFen);

    game.setCurrentFen(newFen);
    gameRepository.save(game);

    final boolean isCheckmate = chessRulesService.isCheckmate(newFen);
    final boolean isStalemate = chessRulesService.isStalemate(newFen);
    final boolean isCheck = chessRulesService.isCheck(newFen);

    if (isCheckmate) {
      game.endGame("CHECKMATE", "Checkmate");
      gameRepository.save(game);
      updateRatingsIfRanked(game);
    } else if (isStalemate) {
      game.endGame("DRAW", "Stalemate");
      gameRepository.save(game);
      updateRatingsIfRanked(game);
    }

    return new MakeMoveResponse(notation, newFen, isCheckmate, isStalemate, isCheck);
  }

  @Transactional
  public void resignGame(final UUID gameId, final UUID playerId) {
    final Game game = findById(gameId);
    game.endGame("RESIGNATION", "Player resigned");
    gameRepository.save(game);
    updateRatingsIfRanked(game);
  }

  /**
   * Update ratings if game is ranked.
   */
  private void updateRatingsIfRanked(Game game) {
    if ("ranked".equalsIgnoreCase(game.getGameType())) {
      String result = determineWinner(game);
      ratingService.updateRatings(
          game.getWhitePlayer().getId(),
          game.getBlackPlayer().getId(),
          result
      );
    }
  }

  /**
   * Determine winner from game result.
   */
  private String determineWinner(Game game) {
    if ("DRAW".equals(game.getResult())) {
      return "draw";
    }
    // For checkmate or resignation, determine winner from current turn
    String currentTurn = chessRulesService.getCurrentTurn(game.getCurrentFen());
    // If it's white's turn and game ended, black won (white couldn't move)
    return "white".equals(currentTurn) ? "black" : "white";
  }
}

