package com.checkmate.chess.websocket;

import com.checkmate.chess.dto.MakeMoveRequest;
import com.checkmate.chess.dto.MakeMoveResponse;
import com.checkmate.chess.service.GameService;
import java.util.UUID;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameWebSocketHandler {

  private final GameService gameService;

  public GameWebSocketHandler(GameService gameService) {
    this.gameService = gameService;
  }

  @MessageMapping("/game/{gameId}/move")
  @SendTo("/topic/game/{gameId}/moves")
  public MakeMoveResponse handleMove(
      @DestinationVariable UUID gameId, MakeMoveRequest request) {
    return gameService.makeMove(gameId, request.from(), request.to(), request.promotion());
  }
}

