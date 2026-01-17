package com.checkmate.chess.controller;

import com.checkmate.chess.dto.GameDto.*;
import com.checkmate.chess.dto.SuccessResponse;
import com.checkmate.chess.service.GameService;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
public class GameController {

  private final GameService gameService;

  public GameController(GameService gameService) {
    this.gameService = gameService;
  }

  @PostMapping("/guest")
  public ResponseEntity<SuccessResponse<CreateGuestGameResponse>> createGuestGame(
      @RequestBody CreateGuestGameRequest request) {
    CreateGuestGameResponse response = gameService.createGuestGame(request.guestUsername());
    return ResponseEntity.ok(new SuccessResponse<>("Game created successfully", response));
  }

  @GetMapping("/{gameId}")
  public ResponseEntity<SuccessResponse<GameStateResponse>> getGame(@PathVariable UUID gameId) {
    GameStateResponse response = gameService.getGameState(gameId);
    return ResponseEntity.ok(new SuccessResponse<>("Game retrieved successfully", response));
  }

  @PostMapping("/{gameId}/resign")
  public ResponseEntity<SuccessResponse<Void>> resignGame(
      @PathVariable UUID gameId, @RequestParam UUID playerId) {
    gameService.resignGame(gameId, playerId);
    return ResponseEntity.ok(new SuccessResponse<>("Game resigned", null));
  }
}

