package com.example.chatbot.controller;

import com.example.chatbot.entity.User;
import com.example.chatbot.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        User saved = authService.register(user);
        return ResponseEntity.ok(Map.of("message", "Registration successful!", "userId", saved.getId()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        String token = authService.login(email, password);
        Long userId = authService.getUserByEmail(email).getId();
        return ResponseEntity.ok(Map.of("token", token, "userId", userId, "message", "Login successful!"));
    }
}