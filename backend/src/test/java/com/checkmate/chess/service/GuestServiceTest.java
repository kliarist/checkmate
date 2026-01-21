package com.checkmate.chess.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.checkmate.chess.model.User;
import com.checkmate.chess.repository.UserRepository;
import java.time.LocalDateTime;
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

  private User guestUser;

  @BeforeEach
  void setUp() {
    guestUser = new User();
    guestUser.setId(1L);
    guestUser.setUsername("Guest_123456");
    guestUser.setGuest(true);
    guestUser.setCreatedAt(LocalDateTime.now());
  }

  @Test
  @DisplayName("Should create guest user with unique ID")
  void shouldCreateGuestUserWithUniqueId() {
    when(userRepository.save(any(User.class))).thenReturn(guestUser);

    User created = guestService.createGuestUser();

    assertNotNull(created);
    assertTrue(created.isGuest());
    assertNotNull(created.getUsername());
    assertTrue(created.getUsername().startsWith("Guest_"));
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  @DisplayName("Should generate unique username for each guest")
  void shouldGenerateUniqueUsernameForEachGuest() {
    User guest1 = new User();
    guest1.setId(1L);
    guest1.setUsername("Guest_123");
    guest1.setGuest(true);

    User guest2 = new User();
    guest2.setId(2L);
    guest2.setUsername("Guest_456");
    guest2.setGuest(true);

    when(userRepository.save(any(User.class)))
        .thenReturn(guest1)
        .thenReturn(guest2);

    User firstGuest = guestService.createGuestUser();
    User secondGuest = guestService.createGuestUser();

    assertNotNull(firstGuest);
    assertNotNull(secondGuest);
    assertNotEquals(firstGuest.getUsername(), secondGuest.getUsername());
  }

  @Test
  @DisplayName("Should set guest flag to true")
  void shouldSetGuestFlagToTrue() {
    when(userRepository.save(any(User.class))).thenReturn(guestUser);

    User created = guestService.createGuestUser();

    assertTrue(created.isGuest(), "Guest user should have isGuest flag set to true");
  }

  @Test
  @DisplayName("Should not require email for guest users")
  void shouldNotRequireEmailForGuestUsers() {
    when(userRepository.save(any(User.class))).thenReturn(guestUser);

    User created = guestService.createGuestUser();

    assertNull(created.getEmail(), "Guest user should not have email");
  }

  @Test
  @DisplayName("Should not require password for guest users")
  void shouldNotRequirePasswordForGuestUsers() {
    when(userRepository.save(any(User.class))).thenReturn(guestUser);

    User created = guestService.createGuestUser();

    assertNull(created.getPasswordHash(), "Guest user should not have password");
  }

  @Test
  @DisplayName("Should set default ELO rating for guest users")
  void shouldSetDefaultEloRatingForGuestUsers() {
    guestUser.setEloRating(1200);
    when(userRepository.save(any(User.class))).thenReturn(guestUser);

    User created = guestService.createGuestUser();

    assertEquals(1200, created.getEloRating(), "Guest user should have default ELO rating");
  }

  @Test
  @DisplayName("Should set creation timestamp")
  void shouldSetCreationTimestamp() {
    when(userRepository.save(any(User.class))).thenReturn(guestUser);

    User created = guestService.createGuestUser();

    assertNotNull(created.getCreatedAt());
    assertTrue(created.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
  }

  @Test
  @DisplayName("Should find guest user by ID")
  void shouldFindGuestUserById() {
    when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(guestUser));

    var found = guestService.findGuestById(1L);

    assertTrue(found.isPresent());
    assertEquals(guestUser.getId(), found.get().getId());
    assertTrue(found.get().isGuest());
  }

  @Test
  @DisplayName("Should return empty when guest user not found")
  void shouldReturnEmptyWhenGuestUserNotFound() {
    when(userRepository.findById(999L)).thenReturn(java.util.Optional.empty());

    var found = guestService.findGuestById(999L);

    assertTrue(found.isEmpty());
  }

  @Test
  @DisplayName("Should handle repository save failure")
  void shouldHandleRepositorySaveFailure() {
    when(userRepository.save(any(User.class)))
        .thenThrow(new RuntimeException("Database error"));

    assertThrows(RuntimeException.class, () -> {
      guestService.createGuestUser();
    });
  }

  @Test
  @DisplayName("Should generate username with timestamp suffix")
  void shouldGenerateUsernameWithTimestampSuffix() {
    when(userRepository.save(any(User.class))).thenReturn(guestUser);

    User created = guestService.createGuestUser();

    String username = created.getUsername();
    assertTrue(username.startsWith("Guest_"));
    assertTrue(username.length() > 6, "Username should have timestamp suffix");
  }

  @Test
  @DisplayName("Should initialize guest user with zero games played")
  void shouldInitializeGuestUserWithZeroGamesPlayed() {
    guestUser.setGamesPlayed(0);
    when(userRepository.save(any(User.class))).thenReturn(guestUser);

    User created = guestService.createGuestUser();

    assertEquals(0, created.getGamesPlayed());
  }

  @Test
  @DisplayName("Should initialize guest user with zero wins/losses/draws")
  void shouldInitializeGuestUserWithZeroWinsLossesDraws() {
    guestUser.setWins(0);
    guestUser.setLosses(0);
    guestUser.setDraws(0);
    when(userRepository.save(any(User.class))).thenReturn(guestUser);

    User created = guestService.createGuestUser();

    assertEquals(0, created.getWins());
    assertEquals(0, created.getLosses());
    assertEquals(0, created.getDraws());
  }

  @Test
  @DisplayName("Should allow multiple guest users to be created concurrently")
  void shouldAllowMultipleGuestUsersToBeCreatedConcurrently() {
    User guest1 = new User();
    guest1.setId(1L);
    guest1.setUsername("Guest_1");
    guest1.setGuest(true);

    User guest2 = new User();
    guest2.setId(2L);
    guest2.setUsername("Guest_2");
    guest2.setGuest(true);

    when(userRepository.save(any(User.class)))
        .thenReturn(guest1)
        .thenReturn(guest2);

    User firstGuest = guestService.createGuestUser();
    User secondGuest = guestService.createGuestUser();

    assertNotNull(firstGuest);
    assertNotNull(secondGuest);
    assertNotEquals(firstGuest.getId(), secondGuest.getId());
    verify(userRepository, times(2)).save(any(User.class));
  }
}

