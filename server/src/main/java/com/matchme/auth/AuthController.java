package com.matchme.auth;

import com.matchme.auth.dto.AuthResponse;
import com.matchme.auth.dto.LoginRequest;
import com.matchme.auth.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Public endpoint: create user and return JWT
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResult result = authService.register(request);
        return ResponseEntity.ok(new AuthResponse(result.userId, result.token));
    }

    // Public endpoint: verify credentials and return JWT
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResult result = authService.login(request);
        return ResponseEntity.ok(new AuthResponse(result.userId, result.token));
    }
}
