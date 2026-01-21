package com.checkmate.chess.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.checkmate.chess.model.User;
import com.checkmate.chess.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Guest Service Tests")
class GuestServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private GuestService guestService;

  @BeforeEach
  void setUp() {
    when(userRepository.existsByUsername(anyString())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
      final User user = invocation.getArgument(0);
      user.setId(java.util.UUID.randomUUID());
      user.onCreate();
      return user;
    });
  }

  @Test
  @DisplayName("Should create guest user with generated username")
  void shouldCreateGuestUserWithGeneratedUsername() {
    final User created = guestService.createGuestUser(null);

    assertThat(created).isNotNull();
    assertThat(created.getUsername()).startsWith("Guest-");
    assertThat(created.getIsGuest()).isTrue();
    assertThat(created.getEmail()).endsWith("@guest.local");
    assertThat(created.getPasswordHash()).isEmpty();

    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("Should create guest user with provided username")
  void shouldCreateGuestUserWithProvidedUsername() {
    final String customUsername = "CustomGuest123";
    final User created = guestService.createGuestUser(customUsername);

    assertThat(created).isNotNull();
    assertThat(created.getUsername()).isEqualTo(customUsername);
    assertThat(created.getIsGuest()).isTrue();
  }

  @Test
  @DisplayName("Should generate unique username if exists")
  void shouldGenerateUniqueUsernameIfExists() {
    when(userRepository.existsByUsername(anyString()))
        .thenReturn(true)
        .thenReturn(true)
        .thenReturn(false);

    final User created = guestService.createGuestUser(null);

    assertThat(created).isNotNull();
    assertThat(created.getUsername()).startsWith("Guest-");
  }

  @Test
  @DisplayName("Should set guest flag to true")
  void shouldSetGuestFlagToTrue() {
    final User created = guestService.createGuestUser(null);

    assertThat(created.getIsGuest()).isTrue();
  }

  @Test
  @DisplayName("Should set default ELO rating")
  void shouldSetDefaultEloRating() {
    final User created = guestService.createGuestUser(null);

    assertThat(created.getEloRating()).isEqualTo(1500);
  }

  @Test
  @DisplayName("Should initialize game statistics to zero")
  void shouldInitializeGameStatisticsToZero() {
    final User created = guestService.createGuestUser(null);

    assertThat(created.getGamesPlayed()).isEqualTo(0);
    assertThat(created.getWins()).isEqualTo(0);
    assertThat(created.getLosses()).isEqualTo(0);
    assertThat(created.getDraws()).isEqualTo(0);
  }

  @Test
  @DisplayName("Should set timestamp on creation")
  void shouldSetTimestampOnCreation() {
    final User created = guestService.createGuestUser(null);

    assertThat(created.getCreatedAt()).isNotNull();
    assertThat(created.getCreatedAt()).isBeforeOrEqualTo(java.time.LocalDateTime.now());
  }

  @Test
  @DisplayName("Should generate different usernames for multiple guests")
  void shouldGenerateDifferentUsernamesForMultipleGuests() {
    final User firstGuest = guestService.createGuestUser(null);
    final User secondGuest = guestService.createGuestUser(null);

    assertThat(firstGuest.getUsername()).isNotEqualTo(secondGuest.getUsername());
  }

  @Test
  @DisplayName("Should have email matching username pattern")
  void shouldHaveEmailMatchingUsernamePattern() {
    final User created = guestService.createGuestUser(null);

    assertThat(created.getEmail()).isEqualTo(created.getUsername() + "@guest.local");
  }

  @Test
  @DisplayName("Should have empty password hash")
  void shouldHaveEmptyPasswordHash() {
    final User created = guestService.createGuestUser(null);

    assertThat(created.getPasswordHash()).isEmpty();
  }
}

