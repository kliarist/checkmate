package com.checkmate.chess.service;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.chess.dto.CreateComputerGameResponse;
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

  private static final Logger logger = LoggerFactory.getLogger(GameService.class);

  private final GameRepository gameRepository;
  private final GuestService guestService;
  private final MoveService moveService;
  private final ChessRulesService chessRulesService;
  private final JwtService jwtService;
  private final RatingService ratingService;
  private final StockfishService stockfishService;
  private final InvitationService invitationService;

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

  @Transactional
  public CreateComputerGameResponse createComputerGame(
      final UUID playerId, final String difficulty, final String playerColor) {
    validateDifficulty(difficulty);
    validateColor(playerColor);

    final com.checkmate.chess.model.User player = findUserById(playerId);
    // Create computer user with unique username that starts with "Computer-"
    final String computerUsername = "Computer-" + UUID.randomUUID().toString().substring(0, 8);
    final com.checkmate.chess.model.User computerUser = guestService.createGuestUser(computerUsername);
    
    // Set ELO rating based on difficulty
    final int computerElo = switch (difficulty.toLowerCase()) {
      case "beginner" -> 800;      // Novice level
      case "intermediate" -> 1500; // Intermediate level
      case "advanced" -> 2400;     // Strong level
      default -> 1500;
    };
    computerUser.setEloRating(computerElo);

    final boolean playerIsWhite = "white".equalsIgnoreCase(playerColor);
    final com.checkmate.chess.model.User whitePlayer = playerIsWhite ? player : computerUser;
    final com.checkmate.chess.model.User blackPlayer = playerIsWhite ? computerUser : player;

    Game game = new Game(whitePlayer, blackPlayer, "COMPUTER");
    game.setTimeControl(difficulty); // Store difficulty in timeControl field
    game = gameRepository.save(game);

    return new CreateComputerGameResponse(
        game.getId(), player.getId(), playerColor.toLowerCase(), difficulty.toLowerCase());
  }

  private com.checkmate.chess.model.User findUserById(final UUID userId) {
    return guestService.findById(userId);
  }

  private void validateDifficulty(final String difficulty) {
    if (difficulty == null || 
        (!difficulty.equalsIgnoreCase("beginner") && 
         !difficulty.equalsIgnoreCase("intermediate") && 
         !difficulty.equalsIgnoreCase("advanced"))) {
      throw new IllegalArgumentException(
          "Invalid difficulty. Must be: beginner, intermediate, or advanced");
    }
  }

  private void validateColor(final String color) {
    if (color == null || 
        (!color.equalsIgnoreCase("white") && !color.equalsIgnoreCase("black"))) {
      throw new IllegalArgumentException("Invalid color. Must be: white or black");
    }
  }

  public Game findById(final UUID gameId) {
    return gameRepository
        .findById(gameId)
        .orElseThrow(() -> new ResourceNotFoundException("Game not found: " + gameId));
  }

  @Transactional(readOnly = true)
  public GameStateResponse getGameState(final UUID gameId) {
    final Game game = findById(gameId);
    
    // Create player info for white player
    final var whitePlayer = game.getWhitePlayer();
    final var whitePlayerInfo = new GameStateResponse.PlayerInfo(
        whitePlayer.getId(),
        whitePlayer.getUsername(),
        whitePlayer.getEloRating(),
        whitePlayer.getIsGuest()
    );
    
    // Create player info for black player
    final var blackPlayer = game.getBlackPlayer();
    final var blackPlayerInfo = new GameStateResponse.PlayerInfo(
        blackPlayer.getId(),
        blackPlayer.getUsername(),
        blackPlayer.getEloRating(),
        blackPlayer.getIsGuest()
    );
    
    return new GameStateResponse(
        game.getId(),
        game.getCurrentFen(),
        game.getStatus(),
        game.getResult(),
        game.getPgn(),
        game.getWhitePlayer().getId(),
        game.getBlackPlayer().getId(),
        game.getGameType(),
        game.getTimeControl(),
        whitePlayerInfo,
        blackPlayerInfo);
  }

  @Transactional(readOnly = true)
  public Game getGame(final UUID gameId) {
    return findById(gameId);
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

  /**
   * Generate and make computer move.
   * Returns the move response or null if game ended.
   */
  @Transactional
  public MakeMoveResponse makeComputerMove(final UUID gameId, final String difficulty) {
    final Game game = findById(gameId);

    if (!"IN_PROGRESS".equals(game.getStatus())) {
      return null;
    }

    try {
      // Get best move from Stockfish
      final String uciMove = stockfishService.getBestMove(game.getCurrentFen(), difficulty);
      
      logger.info("Stockfish returned UCI move: {} for game {}", uciMove, gameId);
      
      // Validate UCI move format
      if (uciMove == null || uciMove.length() < 4) {
        logger.error("Invalid UCI move format: {}", uciMove);
        return null;
      }
      
      // Parse UCI move (e.g., "e2e4" -> from="e2", to="e4", or "e7e8q" for promotion)
      final String from = uciMove.substring(0, 2);
      final String to = uciMove.substring(2, 4);
      final String promotion = uciMove.length() > 4 ? uciMove.substring(4, 5) : null;
      
      logger.info("Parsed move: from={}, to={}, promotion={}", from, to, promotion);

      // Make the move
      return makeMove(gameId, from, to, promotion);
    } catch (Exception e) {
      logger.error("Error making computer move for game {}", gameId, e);
      return null;
    }
  }

  /**
   * Check if it's computer's turn.
   */
  @Transactional(readOnly = true)
  public boolean isComputerTurn(final UUID gameId) {
    final Game game = findById(gameId);
    
    logger.info("Checking computer turn for game {}: gameType={}", gameId, game.getGameType());
    
    if (!"COMPUTER".equals(game.getGameType())) {
      logger.info("Game {} is not a computer game (type={})", gameId, game.getGameType());
      return false;
    }

    final String currentTurn = chessRulesService.getCurrentTurn(game.getCurrentFen());
    final com.checkmate.chess.model.User whitePlayer = game.getWhitePlayer();
    final com.checkmate.chess.model.User blackPlayer = game.getBlackPlayer();
    
    logger.info("Game {} - currentTurn={}, whitePlayer={}, blackPlayer={}", 
        gameId, currentTurn, 
        whitePlayer != null ? whitePlayer.getUsername() : "null",
        blackPlayer != null ? blackPlayer.getUsername() : "null");
    
    final com.checkmate.chess.model.User currentPlayer = 
        "white".equals(currentTurn) ? whitePlayer : blackPlayer;
    
    // In a computer game, identify the computer by username starting with "Computer"
    final String username = currentPlayer.getUsername();
    final boolean isComputer = username != null && username.startsWith("Computer");
    
    logger.info("Current player username: {}, isComputer: {}", username, isComputer);
    
    return isComputer;
  }

  /**
   * Create game from invitation.
   */
  @Transactional
  public Game createGameFromInvitation(final UUID joiningPlayerId, final String invitationCode) {
    if (!invitationService.isValid(invitationCode)) {
      throw new IllegalArgumentException("Invalid or expired invitation");
    }

    final var invitation = invitationService.findByCode(invitationCode);
    final com.checkmate.chess.model.User creator = invitation.getCreator();
    final com.checkmate.chess.model.User joiner = findUserById(joiningPlayerId);

    if (creator.getId().equals(joiningPlayerId)) {
      throw new IllegalArgumentException("Cannot join your own invitation");
    }

    final boolean creatorIsWhite = new Random().nextBoolean();
    final com.checkmate.chess.model.User whitePlayer = creatorIsWhite ? creator : joiner;
    final com.checkmate.chess.model.User blackPlayer = creatorIsWhite ? joiner : creator;

    Game game = new Game(whitePlayer, blackPlayer, invitation.getGameType());
    game = gameRepository.save(game);

    invitationService.markAsUsed(invitationCode);

    return game;
  }
}

