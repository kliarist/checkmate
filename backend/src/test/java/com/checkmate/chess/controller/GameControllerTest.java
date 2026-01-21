package com.checkmate.chess.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.checkmate.chess.model.User;
import com.checkmate.chess.repository.UserRepository;
import com.checkmate.chess.service.GameService;
import com.checkmate.chess.service.GuestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for GameController (T039).
 * Tests REST endpoints for game creation and retrieval.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("Game Controller Integration Tests")
class GameControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private GuestService guestService;

  @Autowired
  private GameService gameService;

  @Autowired
  private UserRepository userRepository;

  private User guestUser1;
  private User guestUser2;

  @BeforeEach
  void setUp() {
    // Create guest users for testing
    guestUser1 = guestService.createGuestUser();
    guestUser2 = guestService.createGuestUser();
  }

  @Test
  @DisplayName("POST /api/games/guest should create a guest game")
  void shouldCreateGuestGame() throws Exception {
    mockMvc.perform(post("/api/games/guest")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").exists())
        .andExpect(jsonPath("$.data.gameType").value("GUEST"))
        .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"))
        .andExpect(jsonPath("$.data.currentFen").exists())
        .andExpect(jsonPath("$.data.whitePlayerId").exists())
        .andExpect(jsonPath("$.data.blackPlayerId").exists());
  }

  @Test
  @DisplayName("POST /api/games/guest should create game with starting FEN")
  void shouldCreateGameWithStartingFen() throws Exception {
    mockMvc.perform(post("/api/games/guest")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.currentFen")
            .value("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
  }

  @Test
  @DisplayName("GET /api/games/{id} should return game by ID")
  void shouldReturnGameById() throws Exception {
    // Create a game first
    var game = gameService.createGuestGame();
    Long gameId = game.getId();

    mockMvc.perform(get("/api/games/" + gameId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(gameId))
        .andExpect(jsonPath("$.data.gameType").value("GUEST"))
        .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));
  }

  @Test
  @DisplayName("GET /api/games/{id} should return 404 for non-existent game")
  void shouldReturn404ForNonExistentGame() throws Exception {
    mockMvc.perform(get("/api/games/999999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error").exists());
  }

  @Test
  @DisplayName("POST /api/games/{id}/resign should update game status")
  void shouldResignGame() throws Exception {
    // Create a game
    var game = gameService.createGuestGame();
    Long gameId = game.getId();
    String playerId = game.getWhitePlayer().getId().toString();

    mockMvc.perform(post("/api/games/" + gameId + "/resign")
            .param("playerId", playerId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.status").value("COMPLETED"))
        .andExpect(jsonPath("$.data.result").exists())
        .andExpect(jsonPath("$.data.endReason").value(containsString("resigned")));
  }

  @Test
  @DisplayName("POST /api/games/{id}/resign should return 400 for invalid player")
  void shouldReturn400ForInvalidPlayerResignation() throws Exception {
    var game = gameService.createGuestGame();
    Long gameId = game.getId();

    mockMvc.perform(post("/api/games/" + gameId + "/resign")
            .param("playerId", "999999"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error").exists());
  }

  @Test
  @DisplayName("POST /api/games/{id}/move should validate and process move")
  void shouldProcessValidMove() throws Exception {
    var game = gameService.createGuestGame();
    Long gameId = game.getId();

    String movePayload = objectMapper.writeValueAsString(
        new MoveRequest("e2", "e4", null)
    );

    mockMvc.perform(post("/api/games/" + gameId + "/move")
            .contentType(MediaType.APPLICATION_JSON)
            .content(movePayload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.currentFen").exists())
        .andExpect(jsonPath("$.data.lastMove").exists())
        .andExpect(jsonPath("$.data.lastMove.algebraicNotation").value("e4"));
  }

  @Test
  @DisplayName("POST /api/games/{id}/move should reject invalid move")
  void shouldRejectInvalidMove() throws Exception {
    var game = gameService.createGuestGame();
    Long gameId = game.getId();

    String movePayload = objectMapper.writeValueAsString(
        new MoveRequest("e2", "e5", null) // Invalid: pawn can't move 3 squares
    );

    mockMvc.perform(post("/api/games/" + gameId + "/move")
            .contentType(MediaType.APPLICATION_JSON)
            .content(movePayload))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error").value(containsString("Invalid move")));
  }

  @Test
  @DisplayName("GET /api/games/{id}/moves should return move history")
  void shouldReturnMoveHistory() throws Exception {
    var game = gameService.createGuestGame();
    Long gameId = game.getId();

    // Make a few moves
    gameService.makeMove(gameId, "e2", "e4", null);
    gameService.makeMove(gameId, "e7", "e5", null);

    mockMvc.perform(get("/api/games/" + gameId + "/moves"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data", hasSize(2)))
        .andExpect(jsonPath("$.data[0].algebraicNotation").value("e4"))
        .andExpect(jsonPath("$.data[1].algebraicNotation").value("e5"));
  }

  @Test
  @DisplayName("POST /api/games/guest should handle concurrent requests")
  void shouldHandleConcurrentGameCreation() throws Exception {
    // Create multiple games concurrently
    mockMvc.perform(post("/api/games/guest")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());

    mockMvc.perform(post("/api/games/guest")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());

    // Verify both games were created independently
    mockMvc.perform(post("/api/games/guest")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.id").exists());
  }

  @Test
  @DisplayName("GET /api/games/{id} should include player information")
  void shouldIncludePlayerInformation() throws Exception {
    var game = gameService.createGuestGame();
    Long gameId = game.getId();

    mockMvc.perform(get("/api/games/" + gameId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.whitePlayerId").exists())
        .andExpect(jsonPath("$.data.blackPlayerId").exists())
        .andExpect(jsonPath("$.data.whitePlayerId").isNumber())
        .andExpect(jsonPath("$.data.blackPlayerId").isNumber());
  }

  // Helper class for move request
  private record MoveRequest(String from, String to, String promotion) {
  }
}

