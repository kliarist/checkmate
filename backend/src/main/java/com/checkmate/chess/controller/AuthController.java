package com.checkmate.chess.controller;

import com.checkmate.chess.dto.LoginRequest;
import com.checkmate.chess.dto.RegisterRequest;
import com.checkmate.chess.dto.AuthResponse;
import com.checkmate.chess.dto.ErrorResponse;
import com.checkmate.chess.model.User;
import com.checkmate.chess.security.JwtUtils;
import com.checkmate.chess.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    public AuthController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(
                request.getEmail(),
                request.getUsername(),
                request.getPassword()
            );

            String token = jwtUtils.generateToken(user.getEmail());

            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setUserId(user.getId());
            response.setEmail(user.getEmail());
            response.setUsername(user.getUsername());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            ErrorResponse error = new ErrorResponse();
            error.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Optional<User> userOpt = userService.login(request.getEmail(), request.getPassword());

        if (userOpt.isEmpty()) {
            ErrorResponse error = new ErrorResponse();
            error.setMessage("Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        User user = userOpt.get();
        String token = jwtUtils.generateToken(user.getEmail());

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setUsername(user.getUsername());

        return ResponseEntity.ok(response);
    }
}
