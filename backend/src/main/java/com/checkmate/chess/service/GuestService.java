package com.checkmate.chess.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.chess.exception.ResourceNotFoundException;
import com.checkmate.chess.model.User;
import com.checkmate.chess.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuestService {

  private final UserRepository userRepository;

  @Transactional
  public User createGuestUser(final String username) {
    String guestUsername = username != null
        ? username
        : "Guest-" + UUID.randomUUID().toString().substring(0, 8);

    while (userRepository.existsByUsername(guestUsername)) {
      guestUsername = "Guest-" + UUID.randomUUID().toString().substring(0, 8);
    }

    final User guest = User.createGuest(guestUsername);
    return userRepository.save(guest);
  }

  public User findById(final UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
  }
}

