package com.checkmate.chess.websocket;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.checkmate.chess.dto.MakeMoveRequest;
import com.checkmate.chess.dto.MakeMoveResponse;
import com.checkmate.chess.service.GameService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class GameWebSocketHandler {

  private static final Logger logger = LoggerFactory.getLogger(GameWebSocketHandler.class);
  private final GameService gameService;
  private final SimpMessagingTemplate messagingTemplate;

  @MessageMapping("/game/{gameId}/move")
  @SendTo("/topic/game/{gameId}/moves")
  public MakeMoveResponse handleMove(
      @DestinationVariable final UUID gameId, final MakeMoveRequest request) {
    logger.info("Received move for game {}: from={}, to={}, promotion={}, difficulty={}", 
        gameId, request.from(), request.to(), request.promotion(), request.difficulty());
    
    final MakeMoveResponse response = 
        gameService.makeMove(gameId, request.from(), request.to(), request.promotion());
    
    logger.info("Move processed successfully. Response: {}, checking if computer turn...", 
        response.algebraicNotation());
    
    // Trigger computer move asynchronously if it's computer's turn
    triggerComputerMoveIfNeeded(gameId, request.difficulty());
    
    return response;
  }

  private void triggerComputerMoveIfNeeded(final UUID gameId, final String difficulty) {
    logger.info("triggerComputerMoveIfNeeded called for game {}, difficulty={}", gameId, difficulty);
    
    CompletableFuture.runAsync(() -> {
      try {
        // Add a small delay to ensure the previous move transaction is committed
        Thread.sleep(100);
        
        final boolean isComputerTurn = gameService.isComputerTurn(gameId);
        logger.info("Is computer turn for game {}: {}", gameId, isComputerTurn);
        
        if (isComputerTurn) {
          final String effectiveDifficulty = difficulty != null && !difficulty.isEmpty() 
              ? difficulty 
              : "intermediate";
          logger.info("Generating computer move for game {} with difficulty {}", 
              gameId, effectiveDifficulty);
          
          // Small delay to simulate thinking
          Thread.sleep(300);
          
          final MakeMoveResponse computerMove = 
              gameService.makeComputerMove(gameId, effectiveDifficulty);
          
          if (computerMove != null) {
            logger.info("Computer move generated: {}, sending to WebSocket...", 
                computerMove.algebraicNotation());
            messagingTemplate.convertAndSend(
                "/topic/game/" + gameId + "/moves", computerMove);
            logger.info("Computer move sent successfully to /topic/game/{}/moves", gameId);
          } else {
            logger.warn("Computer move was null for game {}", gameId);
          }
        } else {
          logger.info("Not computer's turn for game {}, skipping computer move", gameId);
        }
      } catch (InterruptedException e) {
        logger.error("Computer move thread interrupted for game {}", gameId, e);
        Thread.currentThread().interrupt();
      } catch (Exception e) {
        logger.error("Error generating computer move for game {}", gameId, e);
      }
    });
  }
}

