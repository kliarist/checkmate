package com.checkmate.chess.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.checkmate.chess.dto.CreateGuestGameRequest;
import com.checkmate.chess.dto.CreateGuestGameResponse;
import com.checkmate.chess.dto.GameStateResponse;
import com.checkmate.chess.dto.SuccessResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Game Controller Integration Tests")
class GameControllerTest {

  @Autowired
  private GameController gameController;

  @Test
  @DisplayName("Should create guest game successfully")
  void shouldCreateGuestGameSuccessfully() {
    final CreateGuestGameRequest request = new CreateGuestGameRequest("TestGuest");

    final ResponseEntity<SuccessResponse<CreateGuestGameResponse>> response =
        gameController.createGuestGame(request);

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().message()).isEqualTo("Game created successfully");
    assertThat(response.getBody().data()).isNotNull();
    assertThat(response.getBody().data().gameId()).isNotNull();
    assertThat(response.getBody().data().guestUserId()).isNotNull();
    assertThat(response.getBody().data().color()).isNotNull();
  }

  @Test
  @DisplayName("Should create guest game with null username")
  void shouldCreateGuestGameWithNullUsername() {
    final CreateGuestGameRequest request = new CreateGuestGameRequest(null);

    final ResponseEntity<SuccessResponse<CreateGuestGameResponse>> response =
        gameController.createGuestGame(request);

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().data().gameId()).isNotNull();
  }

  @Test
  @DisplayName("Should get game by ID")
  void shouldGetGameById() {
    final CreateGuestGameRequest createRequest = new CreateGuestGameRequest("TestGuest");
    final ResponseEntity<SuccessResponse<CreateGuestGameResponse>> createResponse =
        gameController.createGuestGame(createRequest);
    final java.util.UUID gameId = createResponse.getBody().data().gameId();

    final ResponseEntity<SuccessResponse<GameStateResponse>> response =
        gameController.getGame(gameId);

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().data().gameId()).isEqualTo(gameId);
    assertThat(response.getBody().data().currentFen()).isNotNull();
    assertThat(response.getBody().data().status()).isNotNull();
  }

  @Test
  @DisplayName("Should resign game successfully")
  void shouldResignGameSuccessfully() {
    final CreateGuestGameRequest createRequest = new CreateGuestGameRequest("TestGuest");
    final ResponseEntity<SuccessResponse<CreateGuestGameResponse>> createResponse =
        gameController.createGuestGame(createRequest);
    final CreateGuestGameResponse data = createResponse.getBody().data();
    final java.util.UUID gameId = data.gameId();
    final java.util.UUID guestUserId = data.guestUserId();

    final ResponseEntity<SuccessResponse<Void>> response =
        gameController.resignGame(gameId, guestUserId);

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().message()).isEqualTo("Game resigned");
  }

  @Test
  @DisplayName("Should return initial FEN position for new game")
  void shouldReturnInitialFenPositionForNewGame() {
    final CreateGuestGameRequest createRequest = new CreateGuestGameRequest("TestGuest");
    final ResponseEntity<SuccessResponse<CreateGuestGameResponse>> createResponse =
        gameController.createGuestGame(createRequest);
    final java.util.UUID gameId = createResponse.getBody().data().gameId();

    final ResponseEntity<SuccessResponse<GameStateResponse>> response =
        gameController.getGame(gameId);

    assertThat(response.getBody().data().currentFen())
        .isEqualTo("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
  }

  @Test
  @DisplayName("Should create multiple games independently")
  void shouldCreateMultipleGamesIndependently() {
    final ResponseEntity<SuccessResponse<CreateGuestGameResponse>> game1 =
        gameController.createGuestGame(new CreateGuestGameRequest("Guest1"));
    final ResponseEntity<SuccessResponse<CreateGuestGameResponse>> game2 =
        gameController.createGuestGame(new CreateGuestGameRequest("Guest2"));

    final java.util.UUID game1Id = game1.getBody().data().gameId();
    final java.util.UUID game2Id = game2.getBody().data().gameId();

    assertThat(game1Id).isNotEqualTo(game2Id);
  }

  @Test
  @DisplayName("Should assign color to guest player")
  void shouldAssignColorToGuestPlayer() {
    final CreateGuestGameRequest request = new CreateGuestGameRequest("TestGuest");

    final ResponseEntity<SuccessResponse<CreateGuestGameResponse>> response =
        gameController.createGuestGame(request);

    final String color = response.getBody().data().color();
    assertThat(color).isIn("white", "black");
  }
}

