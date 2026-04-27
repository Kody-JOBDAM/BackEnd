package com.example.kody.jobdam.user.controller;

import com.example.kody.jobdam.user.dto.AuthResponse;
import com.example.kody.jobdam.user.dto.LoginRequest;
import com.example.kody.jobdam.user.dto.SignupRequest;
import com.example.kody.jobdam.user.dto.UserResponse;
import com.example.kody.jobdam.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    @GetMapping("/test")
    public String test() {
        return "auth ok";
    }
}