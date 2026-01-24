package com.checkmate.chess.controller;

import com.checkmate.chess.dto.CreateGuestGameRequest;
import com.checkmate.chess.dto.CreateGuestGameResponse;
import com.checkmate.chess.dto.GameStateResponse;
import com.checkmate.chess.dto.MoveDto;
import com.checkmate.chess.dto.SuccessResponse;
import com.checkmate.chess.service.GameService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

  private final GameService gameService;

  @PostMapping("/guest")
  public ResponseEntity<SuccessResponse<CreateGuestGameResponse>> createGuestGame(
      @RequestBody final CreateGuestGameRequest request) {
    final var response = gameService.createGuestGame(request.guestUsername());
    return ResponseEntity.ok(new SuccessResponse<>("Game created successfully", response));
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
}

