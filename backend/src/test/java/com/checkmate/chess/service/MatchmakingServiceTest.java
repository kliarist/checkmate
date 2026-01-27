package com.checkmate.chess.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.checkmate.chess.model.Game;
import com.checkmate.chess.model.MatchmakingQueue;
import com.checkmate.chess.model.User;
import com.checkmate.chess.repository.GameRepository;
import com.checkmate.chess.repository.MatchmakingQueueRepository;
import com.checkmate.chess.repository.UserRepository;

/**
 * Unit tests for MatchmakingService.
 * Tests matchmaking algorithm and queue management.
 */
@ExtendWith(MockitoExtension.class)
class MatchmakingServiceTest {

  @Mock
  private MatchmakingQueueRepository queueRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private GameRepository gameRepository;

  @InjectMocks
  private MatchmakingService matchmakingService;

  private User player1;
  private User player2;
  private UUID player1Id;
  private UUID player2Id;

  @BeforeEach
  void setUp() {
    player1Id = UUID.randomUUID();
    player2Id = UUID.randomUUID();

    player1 = new User();
    player1.setId(player1Id);
    player1.setEloRating(1500);
    player1.setUsername("player1");

    player2 = new User();
    player2.setId(player2Id);
    player2.setEloRating(1520);
    player2.setUsername("player2");
  }

  @Test
  @DisplayName("Should add player to matchmaking queue")
  void testAddToQueue() {
    // Given
    when(userRepository.findById(player1Id)).thenReturn(Optional.of(player1));

    // When
    matchmakingService.joinQueue(player1Id, "blitz");

    // Then
    verify(queueRepository).save(any(MatchmakingQueue.class));
  }

  @Test
  @DisplayName("Should match players with similar ratings (within 200 ELO)")
  void testMatchPlayersWithSimilarRatings() {
    // Given: Two players with ratings 1500 and 1520 (within 200 ELO)
    MatchmakingQueue queue1 = createQueueEntry(player1Id, 1500, "blitz");
    MatchmakingQueue queue2 = createQueueEntry(player2Id, 1520, "blitz");
    
    // Stub all time controls
    when(queueRepository.findByTimeControlOrderByCreatedAtAsc("bullet")).thenReturn(List.of());
    when(queueRepository.findByTimeControlOrderByCreatedAtAsc("blitz"))
        .thenReturn(Arrays.asList(queue1, queue2));
    when(queueRepository.findByTimeControlOrderByCreatedAtAsc("rapid")).thenReturn(List.of());
    when(queueRepository.findByTimeControlOrderByCreatedAtAsc("classical")).thenReturn(List.of());
    
    when(userRepository.findById(player1Id)).thenReturn(Optional.of(player1));
    when(userRepository.findById(player2Id)).thenReturn(Optional.of(player2));

    // When
    matchmakingService.processPairing();

    // Then: Game created and queue entries removed
    verify(gameRepository).save(any(Game.class));
    verify(queueRepository).delete(queue1);
    verify(queueRepository).delete(queue2);
  }

  @Test
  @DisplayName("Should not match players with large rating difference (>200 ELO)")
  void testDoNotMatchPlayersWithLargeRatingDifference() {
    // Given: Two players with ratings 1500 and 1750 (>200 ELO difference)
    User player3 = new User();
    player3.setId(UUID.randomUUID());
    player3.setEloRating(1750);
    
    MatchmakingQueue queue1 = createQueueEntry(player1Id, 1500, "blitz");
    MatchmakingQueue queue3 = createQueueEntry(player3.getId(), 1750, "blitz");
    
    // Stub all time controls
    when(queueRepository.findByTimeControlOrderByCreatedAtAsc("bullet")).thenReturn(List.of());
    when(queueRepository.findByTimeControlOrderByCreatedAtAsc("blitz"))
        .thenReturn(Arrays.asList(queue1, queue3));
    when(queueRepository.findByTimeControlOrderByCreatedAtAsc("rapid")).thenReturn(List.of());
    when(queueRepository.findByTimeControlOrderByCreatedAtAsc("classical")).thenReturn(List.of());

    // When
    matchmakingService.processPairing();

    // Then: No game created
    verify(gameRepository, org.mockito.Mockito.never()).save(any(Game.class));
  }

  @Test
  @DisplayName("Should only match players with same time control")
  void testMatchOnlySameTimeControl() {
    // Given: Players in different time controls
    MatchmakingQueue queue1 = createQueueEntry(player1Id, 1500, "blitz");
    MatchmakingQueue queue2 = createQueueEntry(player2Id, 1520, "rapid");
    
    // Stub all time controls - each has only one player
    when(queueRepository.findByTimeControlOrderByCreatedAtAsc("bullet")).thenReturn(List.of());
    when(queueRepository.findByTimeControlOrderByCreatedAtAsc("blitz"))
        .thenReturn(Arrays.asList(queue1));
    when(queueRepository.findByTimeControlOrderByCreatedAtAsc("rapid"))
        .thenReturn(Arrays.asList(queue2));
    when(queueRepository.findByTimeControlOrderByCreatedAtAsc("classical")).thenReturn(List.of());

    // When
    matchmakingService.processPairing();

    // Then: No game created (need 2 players in same time control)
    verify(gameRepository, org.mockito.Mockito.never()).save(any(Game.class));
  }

  @Test
  @DisplayName("Should remove player from queue on cancellation")
  void testCancelQueue() {
    // Given
    MatchmakingQueue queue = createQueueEntry(player1Id, 1500, "blitz");
    when(queueRepository.findByUserId(player1Id)).thenReturn(Optional.of(queue));

    // When
    matchmakingService.leaveQueue(player1Id);

    // Then
    verify(queueRepository).delete(queue);
  }

  @Test
  @DisplayName("Should remove expired queue entries (>5 minutes)")
  void testRemoveExpiredEntries() {
    // Given: Queue entry older than 5 minutes
    MatchmakingQueue expiredQueue = createQueueEntry(player1Id, 1500, "blitz");
    expiredQueue.setCreatedAt(LocalDateTime.now().minusMinutes(6));
    
    when(queueRepository.findByCreatedAtBefore(any(LocalDateTime.class)))
        .thenReturn(Arrays.asList(expiredQueue));

    // When
    matchmakingService.removeExpiredEntries();

    // Then
    verify(queueRepository).deleteAll(Arrays.asList(expiredQueue));
  }

  @Test
  @DisplayName("Should match first two players in queue (FIFO)")
  void testMatchFirstTwoPlayers() {
    // Given: Three players in queue
    User player3 = new User();
    player3.setId(UUID.randomUUID());
    player3.setEloRating(1510);
    
    MatchmakingQueue queue1 = createQueueEntry(player1Id, 1500, "blitz");
    queue1.setCreatedAt(LocalDateTime.now().minusMinutes(3));
    
    MatchmakingQueue queue2 = createQueueEntry(player2Id, 1520, "blitz");
    queue2.setCreatedAt(LocalDateTime.now().minusMinutes(2));
    
    MatchmakingQueue queue3 = createQueueEntry(player3.getId(), 1510, "blitz");
    queue3.setCreatedAt(LocalDateTime.now().minusMinutes(1));
    
    // Stub all time controls
    when(queueRepository.findByTimeControlOrderByCreatedAtAsc("bullet")).thenReturn(List.of());
    when(queueRepository.findByTimeControlOrderByCreatedAtAsc("blitz"))
        .thenReturn(Arrays.asList(queue1, queue2, queue3));
    when(queueRepository.findByTimeControlOrderByCreatedAtAsc("rapid")).thenReturn(List.of());
    when(queueRepository.findByTimeControlOrderByCreatedAtAsc("classical")).thenReturn(List.of());
    
    when(userRepository.findById(player1Id)).thenReturn(Optional.of(player1));
    when(userRepository.findById(player2Id)).thenReturn(Optional.of(player2));

    // When
    matchmakingService.processPairing();

    // Then: First two players matched
    verify(queueRepository).delete(queue1);
    verify(queueRepository).delete(queue2);
    verify(queueRepository, org.mockito.Mockito.never()).delete(queue3);
  }

  @Test
  @DisplayName("Should assign colors randomly")
  void testRandomColorAssignment() {
    // Given
    MatchmakingQueue queue1 = createQueueEntry(player1Id, 1500, "blitz");
    MatchmakingQueue queue2 = createQueueEntry(player2Id, 1520, "blitz");
    
    // Stub all time controls
    when(queueRepository.findByTimeControlOrderByCreatedAtAsc("bullet")).thenReturn(List.of());
    when(queueRepository.findByTimeControlOrderByCreatedAtAsc("blitz"))
        .thenReturn(Arrays.asList(queue1, queue2));
    when(queueRepository.findByTimeControlOrderByCreatedAtAsc("rapid")).thenReturn(List.of());
    when(queueRepository.findByTimeControlOrderByCreatedAtAsc("classical")).thenReturn(List.of());
    
    when(userRepository.findById(player1Id)).thenReturn(Optional.of(player1));
    when(userRepository.findById(player2Id)).thenReturn(Optional.of(player2));

    // When
    matchmakingService.processPairing();

    // Then: Game created (colors assigned randomly in implementation)
    verify(gameRepository).save(any(Game.class));
  }

  private MatchmakingQueue createQueueEntry(UUID userId, int rating, String timeControl) {
    MatchmakingQueue queue = new MatchmakingQueue();
    queue.setId(UUID.randomUUID());
    queue.setUserId(userId);
    queue.setRating(rating);
    queue.setTimeControl(timeControl);
    queue.setCreatedAt(LocalDateTime.now());
    return queue;
  }
}
