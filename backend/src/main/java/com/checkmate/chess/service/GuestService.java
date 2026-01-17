package com.checkmate.chess.service;

import com.checkmate.chess.model.User;
import com.checkmate.chess.repository.UserRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GuestService {

  private final UserRepository userRepository;

  public GuestService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  public User createGuestUser(String username) {
    String guestUsername = username != null ? username : "Guest-" + UUID.randomUUID().toString().substring(0, 8);

    while (userRepository.existsByUsername(guestUsername)) {
      guestUsername = "Guest-" + UUID.randomUUID().toString().substring(0, 8);
    }

    User guest = User.createGuest(guestUsername);
    return userRepository.save(guest);
  }
}

