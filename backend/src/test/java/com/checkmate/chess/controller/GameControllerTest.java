package com.checkmate.chess.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.checkmate.chess.dto.CreateGuestGameRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Game Controller Integration Tests")
class GameControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("Should create guest game successfully")
  void shouldCreateGuestGameSuccessfully() throws Exception {
    final var request = new CreateGuestGameRequest("TestGuest");

    final var result = mockMvc.perform(post("/api/games/guest")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Game created successfully"))
        .andExpect(jsonPath("$.data.gameId").exists())
        .andExpect(jsonPath("$.data.whitePlayerId").exists())
        .andExpect(jsonPath("$.data.blackPlayerId").exists())
        .andReturn();

    final String responseBody = result.getResponse().getContentAsString();
    assertThat(responseBody).contains("gameId");
  }

  @Test
  @DisplayName("Should create guest game with null username")
  void shouldCreateGuestGameWithNullUsername() throws Exception {
    final var request = new CreateGuestGameRequest(null);

    mockMvc.perform(post("/api/games/guest")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.gameId").exists());
  }

  @Test
  @DisplayName("Should get game by ID")
  void shouldGetGameById() throws Exception {
    final var createRequest = new CreateGuestGameRequest("TestGuest");
    final var createResult = mockMvc.perform(post("/api/games/guest")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isOk())
        .andReturn();

    final String createResponse = createResult.getResponse().getContentAsString();
    final String gameId = objectMapper.readTree(createResponse)
        .get("data")
        .get("gameId")
        .asText();

    mockMvc.perform(get("/api/games/" + gameId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.gameId").value(gameId))
        .andExpect(jsonPath("$.data.fen").exists())
        .andExpect(jsonPath("$.data.status").exists());
  }

  @Test
  @DisplayName("Should return 404 for non-existent game")
  void shouldReturn404ForNonExistentGame() throws Exception {
    final String nonExistentId = "00000000-0000-0000-0000-000000000000";

    mockMvc.perform(get("/api/games/" + nonExistentId))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should resign game successfully")
  void shouldResignGameSuccessfully() throws Exception {
    final var createRequest = new CreateGuestGameRequest("TestGuest");
    final var createResult = mockMvc.perform(post("/api/games/guest")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isOk())
        .andReturn();

    final String createResponse = createResult.getResponse().getContentAsString();
    final var jsonResponse = objectMapper.readTree(createResponse).get("data");
    final String gameId = jsonResponse.get("gameId").asText();
    final String whitePlayerId = jsonResponse.get("whitePlayerId").asText();

    mockMvc.perform(post("/api/games/" + gameId + "/resign")
            .param("playerId", whitePlayerId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Game resigned"));
  }

  @Test
  @DisplayName("Should return initial FEN position for new game")
  void shouldReturnInitialFenPositionForNewGame() throws Exception {
    final var request = new CreateGuestGameRequest("TestGuest");
    final var createResult = mockMvc.perform(post("/api/games/guest")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andReturn();

    final String gameId = objectMapper.readTree(createResult.getResponse().getContentAsString())
        .get("data")
        .get("gameId")
        .asText();

    mockMvc.perform(get("/api/games/" + gameId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.fen")
            .value("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
  }
}

