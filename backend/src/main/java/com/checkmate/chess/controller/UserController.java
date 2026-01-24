package com.checkmate.chess.controller;

import com.checkmate.chess.dto.ErrorResponse;
import com.checkmate.chess.dto.UserProfileResponse;
import com.checkmate.chess.dto.UserStatsResponse;
import com.checkmate.chess.model.Game;
import com.checkmate.chess.model.User;
import com.checkmate.chess.repository.GameRepository;
import com.checkmate.chess.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final GameRepository gameRepository;

    public UserController(UserService userService, GameRepository gameRepository) {
        this.userService = userService;
        this.gameRepository = gameRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Not authenticated",
                LocalDateTime.now()
            );
            return ResponseEntity.status(401).body(error);
        }

        String email = authentication.getName();
        Optional<User> userOpt = userService.getUserByEmail(email);

        if (userOpt.isEmpty()) {
            ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                "User not found",
                LocalDateTime.now()
            );
            return ResponseEntity.status(404).body(error);
        }

        User user = userOpt.get();
        UserProfileResponse response = new UserProfileResponse();
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setUsername(user.getUsername());
        response.setEloRating(user.getEloRating());
        response.setGamesPlayed(user.getGamesPlayed());
        response.setWins(user.getWins());
        response.setLosses(user.getLosses());
        response.setDraws(user.getDraws());
        response.setCreatedAt(user.getCreatedAt());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/games")
    public ResponseEntity<?> getUserGames(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (authentication == null || authentication.getName() == null) {
            ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Not authenticated",
                LocalDateTime.now()
            );
            return ResponseEntity.status(401).body(error);
        }

        String email = authentication.getName();
        Optional<User> userOpt = userService.getUserByEmail(email);

        if (userOpt.isEmpty()) {
            ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                "User not found",
                LocalDateTime.now()
            );
            return ResponseEntity.status(404).body(error);
        }

        User user = userOpt.get();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Game> games = gameRepository.findByWhitePlayerIdOrBlackPlayerId(
            user.getId(), 
            user.getId(), 
            pageable
        );

        return ResponseEntity.ok(games);
    }

    @GetMapping("/me/stats")
    public ResponseEntity<?> getUserStats(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Not authenticated",
                LocalDateTime.now()
            );
            return ResponseEntity.status(401).body(error);
        }

        String email = authentication.getName();
        Optional<User> userOpt = userService.getUserByEmail(email);

        if (userOpt.isEmpty()) {
            ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                "User not found",
                LocalDateTime.now()
            );
            return ResponseEntity.status(404).body(error);
        }

        User user = userOpt.get();
        UserStatsResponse stats = new UserStatsResponse();
        stats.setGamesPlayed(user.getGamesPlayed());
        stats.setWins(user.getWins());
        stats.setLosses(user.getLosses());
        stats.setDraws(user.getDraws());
        stats.setEloRating(user.getEloRating());
        
        if (user.getGamesPlayed() > 0) {
            double winRate = (double) user.getWins() / user.getGamesPlayed() * 100;
            stats.setWinRate(Math.round(winRate * 10.0) / 10.0);
        } else {
            stats.setWinRate(0.0);
        }

        return ResponseEntity.ok(stats);
    }
}
