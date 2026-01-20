package com.checkmate.chess.service;

import com.checkmate.chess.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  @NonNull
  public UserDetails loadUserByUsername(@NonNull final String username) throws UsernameNotFoundException {
    // Try to find by email first (for regular users)
    var userOptional = userRepository.findByEmail(username);

    // If not found by email, try username (for guest users)
    if (userOptional.isEmpty()) {
      userOptional = userRepository.findByUsername(username);
    }

    final var user = userOptional
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    // Determine the role based on whether the user is a guest
    final var isGuest = Boolean.TRUE.equals(user.getIsGuest());
    final var role = isGuest ? "ROLE_GUEST" : "ROLE_USER";

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

