package com.checkmate.chess.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.checkmate.chess.dto.CreateComputerGameRequest;
import com.checkmate.chess.dto.CreateComputerGameResponse;
import com.checkmate.chess.dto.CreateGuestGameRequest;
import com.checkmate.chess.dto.CreateGuestGameResponse;
import com.checkmate.chess.dto.CreateInvitationRequest;
import com.checkmate.chess.dto.CreateInvitationResponse;
import com.checkmate.chess.dto.GameStateResponse;
import com.checkmate.chess.dto.JoinInvitationResponse;
import com.checkmate.chess.dto.MoveDto;
import com.checkmate.chess.dto.SuccessResponse;
import com.checkmate.chess.model.Game;
import com.checkmate.chess.model.GameInvitation;
import com.checkmate.chess.service.GameService;
import com.checkmate.chess.service.InvitationService;
import com.checkmate.chess.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

  private final GameService gameService;
  private final InvitationService invitationService;
  private final UserService userService;

  @PostMapping("/guest")
  public ResponseEntity<SuccessResponse<CreateGuestGameResponse>> createGuestGame(
      @RequestBody final CreateGuestGameRequest request) {
    final var response = gameService.createGuestGame(request.guestUsername());
    return ResponseEntity.ok(new SuccessResponse<>("Game created successfully", response));
  }

  @PostMapping("/computer")
  public ResponseEntity<SuccessResponse<CreateComputerGameResponse>> createComputerGame(
      @RequestBody final CreateComputerGameRequest request,
      final Authentication authentication) {
    // Extract user ID from authentication
    final UUID playerId = extractUserId(authentication);
    final var response = gameService.createComputerGame(
        playerId, request.difficulty(), request.playerColor());
    return ResponseEntity.ok(new SuccessResponse<>("Computer game created successfully", response));
  }

  private UUID extractUserId(final Authentication authentication) {
    // The JWT token contains the email as the subject
    final String email = authentication.getName();
    
    // Look up the user by email to get the UUID
    final var user = userService.getUserByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found for email: " + email));
    
    return user.getId();
  }

  @GetMapping("/{gameId}")
  public ResponseEntity<SuccessResponse<GameStateResponse>> getGame(@PathVariable final UUID gameId) {
    final GameStateResponse response = gameService.getGameState(gameId);
    return ResponseEntity.ok(new SuccessResponse<>("Game retrieved successfully", response));
  }

  @GetMapping("/{gameId}/moves")
  public ResponseEntity<SuccessResponse<List<MoveDto>>> getMoves(@PathVariable final UUID gameId) {
    final List<MoveDto> moves = gameService.getGameMoves(gameId);
    return ResponseEntity.ok(new SuccessResponse<>("Moves retrieved successfully", moves));
  }

  @PostMapping("/{gameId}/resign")
  public ResponseEntity<SuccessResponse<Void>> resignGame(
      @PathVariable final UUID gameId, @RequestParam final UUID playerId) {
    gameService.resignGame(gameId, playerId);
    return ResponseEntity.ok(new SuccessResponse<>("Game resigned", null));
  }

  @PostMapping("/invite")
  public ResponseEntity<SuccessResponse<CreateInvitationResponse>> createInvitation(
      @RequestBody final CreateInvitationRequest request,
      final Authentication authentication) {
    final UUID creatorId = extractUserId(authentication);
    final GameInvitation invitation = invitationService.createInvitation(
        creatorId, request.timeControl(), request.gameType());
    
    final String invitationLink = "http://localhost:5173/join/" + invitation.getInvitationCode();
    final CreateInvitationResponse response = new CreateInvitationResponse(
        invitation.getId(),
        invitation.getInvitationCode(),
        invitationLink,
        invitation.getExpiresAt()
    );
    
    return ResponseEntity.ok(new SuccessResponse<>("Invitation created successfully", response));
  }

  @PostMapping("/join/{code}")
  public ResponseEntity<SuccessResponse<JoinInvitationResponse>> joinInvitation(
      @PathVariable final String code,
      final Authentication authentication) {
    final UUID playerId = extractUserId(authentication);
    final Game game = gameService.createGameFromInvitation(playerId, code);
    
    final boolean isWhite = game.getWhitePlayer().getId().equals(playerId);
    final JoinInvitationResponse response = new JoinInvitationResponse(
        game.getId(),
        isWhite ? "white" : "black"
    );
    
    return ResponseEntity.ok(new SuccessResponse<>("Joined game successfully", response));
  }
}

