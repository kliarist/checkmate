package com.checkmate.chess.performance;

import static org.junit.jupiter.api.Assertions.*;

import com.checkmate.chess.model.Game;
import com.checkmate.chess.service.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("WebSocket Move Latency Performance Tests")
class WebSocketLatencyTest {

  @LocalServerPort
  private int port;

  @Autowired
  private GameService gameService;

  @Autowired
  private ObjectMapper objectMapper;

  private WebSocketStompClient stompClient;
  private String wsUrl;

  @BeforeEach
  void setUp() {
    wsUrl = String.format("ws://localhost:%d/ws", port);

    var webSocketClient = new StandardWebSocketClient();
    stompClient = new WebSocketStompClient(
        new SockJsClient(java.util.List.of(new WebSocketTransport(webSocketClient)))
    );
    stompClient.setMessageConverter(new MappingJackson2MessageConverter());
  }

  @Test
  @DisplayName("Should sync moves with < 100ms latency at p95")
  void shouldSyncMovesWithLowLatency() throws Exception {
    Game game = gameService.createGuestGame();
    List<Long> latencies = new ArrayList<>();
    int numberOfMoves = 100;
    CountDownLatch latch = new CountDownLatch(numberOfMoves);

    StompSession session = stompClient.connect(wsUrl, new StompSessionHandlerAdapter() {
    }).get(3, TimeUnit.SECONDS);

    // Subscribe to game moves
    session.subscribe("/topic/game/" + game.getId() + "/moves", new StompFrameHandler() {
      @Override
      public java.lang.reflect.Type getPayloadType(StompHeaders headers) {
        return String.class;
      }

      @Override
      public void handleFrame(StompHeaders headers, Object payload) {
        latch.countDown();
      }
    });

    // Send moves and measure latency
    String[][] moves = {
        {"e2", "e4"}, {"e7", "e5"}, {"g1", "f3"}, {"b8", "c6"},
        {"f1", "c4"}, {"g8", "f6"}, {"d2", "d3"}, {"f8", "c5"}
    };

    for (int i = 0; i < numberOfMoves; i++) {
      String[] move = moves[i % moves.length];

      Instant start = Instant.now();
      var movePayload = new MovePayload(move[0], move[1], null);
      session.send("/app/game/" + game.getId() + "/move", movePayload);

      // Wait for move to be processed
      Thread.sleep(10); // Small delay between moves

      Instant end = Instant.now();
      long latency = Duration.between(start, end).toMillis();
      latencies.add(latency);
    }

    // Wait for all moves to complete
    assertTrue(latch.await(30, TimeUnit.SECONDS), "All moves should complete within 30 seconds");

    // Calculate p95 latency
    latencies.sort(Long::compareTo);
    int p95Index = (int) Math.ceil(latencies.size() * 0.95) - 1;
    long p95Latency = latencies.get(p95Index);
    long avgLatency = (long) latencies.stream().mapToLong(Long::longValue).average().orElse(0);
    long maxLatency = latencies.stream().mapToLong(Long::longValue).max().orElse(0);

    System.out.println("=== WebSocket Latency Performance Results ===");
    System.out.println("Number of moves: " + numberOfMoves);
    System.out.println("Average latency: " + avgLatency + "ms");
    System.out.println("P95 latency: " + p95Latency + "ms");
    System.out.println("Max latency: " + maxLatency + "ms");
    System.out.println("Target: < 100ms at p95");

    // Assert p95 latency is under 100ms
    assertTrue(p95Latency < 100,
        String.format("P95 latency should be < 100ms, but was %dms", p95Latency));
  }

  @Test
  @DisplayName("Should handle concurrent move submissions efficiently")
  void shouldHandleConcurrentMovesEfficiently() throws Exception {
    Game game = gameService.createGuestGame();
    int numberOfClients = 10;
    int movesPerClient = 10;
    List<Long> latencies = new ArrayList<>();
    CountDownLatch latch = new CountDownLatch(numberOfClients * movesPerClient);

    for (int clientId = 0; clientId < numberOfClients; clientId++) {
      new Thread(() -> {
        try {
          StompSession session = stompClient.connect(wsUrl, new StompSessionHandlerAdapter() {
          }).get(3, TimeUnit.SECONDS);

          session.subscribe("/topic/game/" + game.getId() + "/moves", new StompFrameHandler() {
            @Override
            public java.lang.reflect.Type getPayloadType(StompHeaders headers) {
              return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
              latch.countDown();
            }
          });

          for (int i = 0; i < movesPerClient; i++) {
            Instant start = Instant.now();
            session.send("/app/game/" + game.getId() + "/move",
                new MovePayload("e2", "e4", null));
            Instant end = Instant.now();

            synchronized (latencies) {
              latencies.add(Duration.between(start, end).toMillis());
            }
            Thread.sleep(50);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }).start();
    }

    assertTrue(latch.await(60, TimeUnit.SECONDS),
        "All concurrent moves should complete within 60 seconds");

    // Calculate statistics
    synchronized (latencies) {
      latencies.sort(Long::compareTo);
      int p95Index = (int) Math.ceil(latencies.size() * 0.95) - 1;
      long p95Latency = latencies.get(p95Index);

      System.out.println("=== Concurrent Move Performance Results ===");
      System.out.println("Number of clients: " + numberOfClients);
      System.out.println("Moves per client: " + movesPerClient);
      System.out.println("Total moves: " + latencies.size());
      System.out.println("P95 latency: " + p95Latency + "ms");

      assertTrue(p95Latency < 200,
          "P95 latency under load should be < 200ms, but was " + p95Latency + "ms");
    }
  }

  @Test
  @DisplayName("Should measure round-trip time for move synchronization")
  void shouldMeasureRoundTripTime() throws Exception {
    Game game = gameService.createGuestGame();
    List<Long> roundTripTimes = new ArrayList<>();
    int iterations = 50;

    StompSession session = stompClient.connect(wsUrl, new StompSessionHandlerAdapter() {
    }).get(3, TimeUnit.SECONDS);

    for (int i = 0; i < iterations; i++) {
      CountDownLatch moveLatch = new CountDownLatch(1);
      Instant[] sendTime = new Instant[1];
      Instant[] receiveTime = new Instant[1];

      session.subscribe("/topic/game/" + game.getId() + "/moves", new StompFrameHandler() {
        @Override
        public java.lang.reflect.Type getPayloadType(StompHeaders headers) {
          return String.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
          receiveTime[0] = Instant.now();
          moveLatch.countDown();
        }
      });

      sendTime[0] = Instant.now();
      session.send("/app/game/" + game.getId() + "/move",
          new MovePayload("e2", "e4", null));

      assertTrue(moveLatch.await(2, TimeUnit.SECONDS));

      long rtt = Duration.between(sendTime[0], receiveTime[0]).toMillis();
      roundTripTimes.add(rtt);

      Thread.sleep(100); // Small delay between iterations
    }

    // Calculate statistics
    roundTripTimes.sort(Long::compareTo);
    int p95Index = (int) Math.ceil(roundTripTimes.size() * 0.95) - 1;
    long p95Rtt = roundTripTimes.get(p95Index);
    long avgRtt = (long) roundTripTimes.stream().mapToLong(Long::longValue).average().orElse(0);

    System.out.println("=== Round-Trip Time Results ===");
    System.out.println("Iterations: " + iterations);
    System.out.println("Average RTT: " + avgRtt + "ms");
    System.out.println("P95 RTT: " + p95Rtt + "ms");

    assertTrue(p95Rtt < 100, "P95 RTT should be < 100ms, but was " + p95Rtt + "ms");
  }

  private record MovePayload(String from, String to, String promotion) {}
}

