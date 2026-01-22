package com.checkmate.chess.service;

import java.util.Optional;
import com.checkmate.chess.model.User;
import com.checkmate.chess.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  @Nonnull
  public UserDetails loadUserByUsername(@Nonnull final String username) throws UsernameNotFoundException {
    Optional<User> userOptional = userRepository.findByEmail(username);

    if (userOptional.isEmpty()) {
      userOptional = userRepository.findByUsername(username);
    }

    final User user = userOptional
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    final boolean isGuest = Boolean.TRUE.equals(user.getIsGuest());
    final String role = isGuest ? "ROLE_GUEST" : "ROLE_USER";

    return org.springframework.security.core.userdetails.User.builder()
        .username(isGuest ? user.getUsername() : user.getEmail())
        .password(user.getPasswordHash())
        .authorities(role)
        .accountExpired(false)
        .accountLocked(false)
        .credentialsExpired(false)
        .disabled(false)
        .build();
  }
}

