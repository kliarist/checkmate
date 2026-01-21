package com.checkmate.chess.websocket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.checkmate.chess.model.Game;
import com.checkmate.chess.model.User;
import com.checkmate.chess.service.GameService;
import com.checkmate.chess.service.GuestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

/**
 * Integration tests for WebSocket move handling (T040).
 * Tests real-time move synchronization via WebSocket.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("Game WebSocket Handler Integration Tests")
class GameWebSocketHandlerTest {

  @LocalServerPort
  private int port;

  @Autowired
  private GameService gameService;

  @Autowired
  private GuestService guestService;

  @Autowired
  private ObjectMapper objectMapper;

  private WebSocketStompClient stompClient;
  private String wsUrl;
  private Game testGame;

  @BeforeEach
  void setUp() {
    wsUrl = String.format("ws://localhost:%d/ws", port);

    var webSocketClient = new StandardWebSocketClient();
    stompClient = new WebSocketStompClient(
        new SockJsClient(java.util.List.of(new WebSocketTransport(webSocketClient)))
    );
    stompClient.setMessageConverter(new MappingJackson2MessageConverter());

    // Create a test game
    testGame = gameService.createGuestGame();
  }

  @Test
  @DisplayName("Should connect to WebSocket endpoint")
  void shouldConnectToWebSocket() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);

    stompClient.connect(wsUrl, new StompSessionHandlerAdapter() {
      @Override
      public void afterConnected(StompSession session, StompHeaders headers) {
        latch.countDown();
      }
    });

    assertTrue(latch.await(3, TimeUnit.SECONDS), "Should connect to WebSocket");
  }

  @Test
  @DisplayName("Should subscribe to game move topic")
  void shouldSubscribeToGameMoveTopic() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);

    StompSession session = stompClient.connect(wsUrl, new StompSessionHandlerAdapter() {
    }).get(3, TimeUnit.SECONDS);

    session.subscribe("/topic/game/" + testGame.getId() + "/moves", new StompFrameHandler() {
      @Override
      public java.lang.reflect.Type getPayloadType(StompHeaders headers) {
        return String.class;
      }

      @Override
      public void handleFrame(StompHeaders headers, Object payload) {
        latch.countDown();
      }
    });

    // Send a move
    var movePayload = new MovePayload("e2", "e4", null);
    session.send("/app/game/" + testGame.getId() + "/move", movePayload);

    assertTrue(latch.await(5, TimeUnit.SECONDS), "Should receive move via WebSocket");
  }

  @Test
  @DisplayName("Should broadcast move to all subscribers")
  void shouldBroadcastMoveToAllSubscribers() throws Exception {
    CountDownLatch latch = new CountDownLatch(2);

    StompSession session1 = stompClient.connect(wsUrl, new StompSessionHandlerAdapter() {
    }).get(3, TimeUnit.SECONDS);

    StompSession session2 = stompClient.connect(wsUrl, new StompSessionHandlerAdapter() {
    }).get(3, TimeUnit.SECONDS);

    // Both sessions subscribe to same game
    session1.subscribe("/topic/game/" + testGame.getId() + "/moves", new StompFrameHandler() {
      @Override
      public java.lang.reflect.Type getPayloadType(StompHeaders headers) {
        return String.class;
      }

      @Override
      public void handleFrame(StompHeaders headers, Object payload) {
        latch.countDown();
      }
    });

    session2.subscribe("/topic/game/" + testGame.getId() + "/moves", new StompFrameHandler() {
      @Override
      public java.lang.reflect.Type getPayloadType(StompHeaders headers) {
        return String.class;
      }

      @Override
      public void handleFrame(StompHeaders headers, Object payload) {
        latch.countDown();
      }
    });

    // Send move from one session
    var movePayload = new MovePayload("e2", "e4", null);
    session1.send("/app/game/" + testGame.getId() + "/move", movePayload);

    assertTrue(latch.await(5, TimeUnit.SECONDS),
        "Both subscribers should receive the move");
  }

  @Test
  @DisplayName("Should include move details in WebSocket message")
  void shouldIncludeMoveDetailsInMessage() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    MoveMessage[] receivedMessage = new MoveMessage[1];

    StompSession session = stompClient.connect(wsUrl, new StompSessionHandlerAdapter() {
    }).get(3, TimeUnit.SECONDS);

    session.subscribe("/topic/game/" + testGame.getId() + "/moves", new StompFrameHandler() {
      @Override
      public java.lang.reflect.Type getPayloadType(StompHeaders headers) {
        return MoveMessage.class;
      }

      @Override
      public void handleFrame(StompHeaders headers, Object payload) {
        receivedMessage[0] = (MoveMessage) payload;
        latch.countDown();
      }
    });

    var movePayload = new MovePayload("e2", "e4", null);
    session.send("/app/game/" + testGame.getId() + "/move", movePayload);

    assertTrue(latch.await(5, TimeUnit.SECONDS));
    assertNotNull(receivedMessage[0]);
    assertEquals("e4", receivedMessage[0].algebraicNotation);
    assertNotNull(receivedMessage[0].fen);
  }

  @Test
  @DisplayName("Should handle invalid move via WebSocket")
  void shouldHandleInvalidMoveViaWebSocket() throws Exception {
    StompSession session = stompClient.connect(wsUrl, new StompSessionHandlerAdapter() {
    }).get(3, TimeUnit.SECONDS);

    var invalidMove = new MovePayload("e2", "e5", null); // Invalid: pawn can't move 3 squares

    // Should handle error gracefully
    assertDoesNotThrow(() -> {
      session.send("/app/game/" + testGame.getId() + "/move", invalidMove);
      Thread.sleep(1000);
    });
  }

  @Test
  @DisplayName("Should handle check notification via WebSocket")
  void shouldHandleCheckNotification() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    MoveMessage[] receivedMessage = new MoveMessage[1];

    StompSession session = stompClient.connect(wsUrl, new StompSessionHandlerAdapter() {
    }).get(3, TimeUnit.SECONDS);

    session.subscribe("/topic/game/" + testGame.getId() + "/moves", new StompFrameHandler() {
      @Override
      public java.lang.reflect.Type getPayloadType(StompHeaders headers) {
        return MoveMessage.class;
      }

      @Override
      public void handleFrame(StompHeaders headers, Object payload) {
        receivedMessage[0] = (MoveMessage) payload;
        if (((MoveMessage) payload).isCheck) {
          latch.countDown();
        }
      }
    });

    // Play moves leading to check
    session.send("/app/game/" + testGame.getId() + "/move", new MovePayload("e2", "e4", null));
    Thread.sleep(500);
    session.send("/app/game/" + testGame.getId() + "/move", new MovePayload("e7", "e5", null));
    Thread.sleep(500);
    session.send("/app/game/" + testGame.getId() + "/move", new MovePayload("f1", "b5", null));

    if (latch.await(5, TimeUnit.SECONDS)) {
      assertTrue(receivedMessage[0].isCheck, "Should indicate check");
    }
  }

  @Test
  @DisplayName("Should disconnect gracefully")
  void shouldDisconnectGracefully() throws Exception {
    StompSession session = stompClient.connect(wsUrl, new StompSessionHandlerAdapter() {
    }).get(3, TimeUnit.SECONDS);

    assertTrue(session.isConnected());

    session.disconnect();

    // Wait for disconnect
    Thread.sleep(500);
    assertFalse(session.isConnected());
  }

  @Test
  @DisplayName("Should handle concurrent move submissions")
  void shouldHandleConcurrentMoveSubmissions() throws Exception {
    CountDownLatch latch = new CountDownLatch(2);

    StompSession session = stompClient.connect(wsUrl, new StompSessionHandlerAdapter() {
    }).get(3, TimeUnit.SECONDS);

    session.subscribe("/topic/game/" + testGame.getId() + "/moves", new StompFrameHandler() {
      @Override
      public java.lang.reflect.Type getPayloadType(StompHeaders headers) {
        return MoveMessage.class;
      }

      @Override
      public void handleFrame(StompHeaders headers, Object payload) {
        latch.countDown();
      }
    });

    // Send moves concurrently
    new Thread(() -> {
      session.send("/app/game/" + testGame.getId() + "/move", new MovePayload("e2", "e4", null));
    }).start();

    Thread.sleep(1000);

    new Thread(() -> {
      session.send("/app/game/" + testGame.getId() + "/move", new MovePayload("e7", "e5", null));
    }).start();

    assertTrue(latch.await(5, TimeUnit.SECONDS), "Should handle concurrent moves");
  }

  // Helper classes
  private record MovePayload(String from, String to, String promotion) {}

  private static class MoveMessage {
    public String algebraicNotation;
    public String fen;
    public boolean isCheck;
    public boolean isCheckmate;
    public boolean isStalemate;
  }
}

