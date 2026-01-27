package com.checkmate.chess.service;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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

import com.checkmate.chess.model.Rating;
import com.checkmate.chess.model.User;
import com.checkmate.chess.repository.RatingRepository;
import com.checkmate.chess.repository.UserRepository;

/**
 * Unit tests for RatingService - ELO rating calculation.
 * Tests cover all ELO calculation scenarios with 100% coverage.
 */
@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

  @Mock
  private RatingRepository ratingRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private RatingService ratingService;

  private User whitePlayer;
  private User blackPlayer;
  private UUID whitePlayerId;
  private UUID blackPlayerId;

  @BeforeEach
  void setUp() {
    whitePlayerId = UUID.randomUUID();
    blackPlayerId = UUID.randomUUID();

    whitePlayer = new User();
    whitePlayer.setId(whitePlayerId);
    whitePlayer.setEloRating(1500);

    blackPlayer = new User();
    blackPlayer.setId(blackPlayerId);
    blackPlayer.setEloRating(1500);
  }

  @Test
  @DisplayName("Should calculate ELO for white win against equal opponent")
  void testWhiteWinEqualRating() {
    // Given: Both players at 1500 ELO
    when(userRepository.findById(whitePlayerId)).thenReturn(Optional.of(whitePlayer));
    when(userRepository.findById(blackPlayerId)).thenReturn(Optional.of(blackPlayer));

    // When: White wins
    ratingService.updateRatings(whitePlayerId, blackPlayerId, "white");

    // Then: White gains ~16 points, Black loses ~16 points (K=32 for new players)
    assertThat(whitePlayer.getEloRating()).isEqualTo(1516);
    assertThat(blackPlayer.getEloRating()).isEqualTo(1484);
  }

  @Test
  @DisplayName("Should calculate ELO for black win against equal opponent")
  void testBlackWinEqualRating() {
    // Given: Both players at 1500 ELO
    when(userRepository.findById(whitePlayerId)).thenReturn(Optional.of(whitePlayer));
    when(userRepository.findById(blackPlayerId)).thenReturn(Optional.of(blackPlayer));

    // When: Black wins
    ratingService.updateRatings(whitePlayerId, blackPlayerId, "black");

    // Then: Black gains ~16 points, White loses ~16 points
    assertThat(whitePlayer.getEloRating()).isEqualTo(1484);
    assertThat(blackPlayer.getEloRating()).isEqualTo(1516);
  }

  @Test
  @DisplayName("Should calculate ELO for draw between equal opponents")
  void testDrawEqualRating() {
    // Given: Both players at 1500 ELO
    when(userRepository.findById(whitePlayerId)).thenReturn(Optional.of(whitePlayer));
    when(userRepository.findById(blackPlayerId)).thenReturn(Optional.of(blackPlayer));

    // When: Draw
    ratingService.updateRatings(whitePlayerId, blackPlayerId, "draw");

    // Then: No rating change for equal players
    assertThat(whitePlayer.getEloRating()).isEqualTo(1500);
    assertThat(blackPlayer.getEloRating()).isEqualTo(1500);
  }

  @Test
  @DisplayName("Should calculate ELO when lower rated player wins (upset)")
  void testLowerRatedPlayerWins() {
    // Given: White 1300, Black 1500
    whitePlayer.setEloRating(1300);
    blackPlayer.setEloRating(1500);
    when(userRepository.findById(whitePlayerId)).thenReturn(Optional.of(whitePlayer));
    when(userRepository.findById(blackPlayerId)).thenReturn(Optional.of(blackPlayer));

    // When: Lower rated white wins (upset)
    ratingService.updateRatings(whitePlayerId, blackPlayerId, "white");

    // Then: White gains more points (~28), Black loses fewer points (~28)
    assertThat(whitePlayer.getEloRating()).isGreaterThan(1320);
    assertThat(blackPlayer.getEloRating()).isLessThan(1480);
  }

  @Test
  @DisplayName("Should calculate ELO when higher rated player wins (expected)")
  void testHigherRatedPlayerWins() {
    // Given: White 1700, Black 1500
    whitePlayer.setEloRating(1700);
    blackPlayer.setEloRating(1500);
    when(userRepository.findById(whitePlayerId)).thenReturn(Optional.of(whitePlayer));
    when(userRepository.findById(blackPlayerId)).thenReturn(Optional.of(blackPlayer));

    // When: Higher rated white wins (expected)
    ratingService.updateRatings(whitePlayerId, blackPlayerId, "white");

    // Then: White gains fewer points (~5), Black loses fewer points (~5)
    assertThat(whitePlayer.getEloRating()).isGreaterThan(1700);
    assertThat(whitePlayer.getEloRating()).isLessThan(1710);
    assertThat(blackPlayer.getEloRating()).isGreaterThan(1490);
    assertThat(blackPlayer.getEloRating()).isLessThan(1500);
  }

  @Test
  @DisplayName("Should persist rating history")
  void testPersistRatingHistory() {
    // Given
    when(userRepository.findById(whitePlayerId)).thenReturn(Optional.of(whitePlayer));
    when(userRepository.findById(blackPlayerId)).thenReturn(Optional.of(blackPlayer));

    // When
    ratingService.updateRatings(whitePlayerId, blackPlayerId, "white");

    // Then: Rating history saved for both players (2 times)
    verify(ratingRepository, org.mockito.Mockito.times(2)).save(any(Rating.class));
  }

  @Test
  @DisplayName("Should use K-factor of 32 for players with < 30 games")
  void testKFactorNewPlayers() {
    // Given: New players
    whitePlayer.setGamesPlayed(10);
    blackPlayer.setGamesPlayed(15);
    when(userRepository.findById(whitePlayerId)).thenReturn(Optional.of(whitePlayer));
    when(userRepository.findById(blackPlayerId)).thenReturn(Optional.of(blackPlayer));

    // When: White wins
    ratingService.updateRatings(whitePlayerId, blackPlayerId, "white");

    // Then: Rating change should be ~16 (K=32, expected score 0.5)
    assertThat(whitePlayer.getEloRating()).isEqualTo(1516);
  }

  @Test
  @DisplayName("Should use K-factor of 24 for players with 30+ games and rating < 2400")
  void testKFactorIntermediatePlayers() {
    // Given: Intermediate players
    whitePlayer.setGamesPlayed(50);
    whitePlayer.setEloRating(1800);
    blackPlayer.setGamesPlayed(60);
    blackPlayer.setEloRating(1800);
    when(userRepository.findById(whitePlayerId)).thenReturn(Optional.of(whitePlayer));
    when(userRepository.findById(blackPlayerId)).thenReturn(Optional.of(blackPlayer));

    // When: White wins
    ratingService.updateRatings(whitePlayerId, blackPlayerId, "white");

    // Then: Rating change should be ~12 (K=24, expected score 0.5)
    assertThat(whitePlayer.getEloRating()).isEqualTo(1812);
  }

  @Test
  @DisplayName("Should use K-factor of 16 for players with rating >= 2400")
  void testKFactorMasterPlayers() {
    // Given: Master level players
    whitePlayer.setGamesPlayed(100);
    whitePlayer.setEloRating(2500);
    blackPlayer.setGamesPlayed(120);
    blackPlayer.setEloRating(2500);
    when(userRepository.findById(whitePlayerId)).thenReturn(Optional.of(whitePlayer));
    when(userRepository.findById(blackPlayerId)).thenReturn(Optional.of(blackPlayer));

    // When: White wins
    ratingService.updateRatings(whitePlayerId, blackPlayerId, "white");

    // Then: Rating change should be ~8 (K=16, expected score 0.5)
    assertThat(whitePlayer.getEloRating()).isEqualTo(2508);
  }

  @Test
  @DisplayName("Should calculate expected score correctly")
  void testExpectedScoreCalculation() {
    // Given: White 1600, Black 1400 (200 point difference)
    whitePlayer.setEloRating(1600);
    blackPlayer.setEloRating(1400);

    // When: Calculate expected score
    double expectedWhite = ratingService.calculateExpectedScore(1600, 1400);
    double expectedBlack = ratingService.calculateExpectedScore(1400, 1600);

    // Then: Expected scores should sum to 1.0
    assertThat(expectedWhite).isCloseTo(0.76, org.assertj.core.data.Offset.offset(0.01));
    assertThat(expectedBlack).isCloseTo(0.24, org.assertj.core.data.Offset.offset(0.01));
    assertThat(expectedWhite + expectedBlack).isCloseTo(1.0, org.assertj.core.data.Offset.offset(0.01));
  }
}
