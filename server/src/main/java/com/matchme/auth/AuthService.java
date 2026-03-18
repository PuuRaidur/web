package com.matchme.auth;

import com.matchme.auth.dto.LoginRequest;
import com.matchme.auth.dto.RegisterRequest;

import com.matchme.user.User;
import com.matchme.user.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// Marks this class as a Spring service.
@Service
public class AuthService {

    // Dependencies used for DB access, password hashing, and JWTs.
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Constructor injection for dependencies.
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // Method to register a new user.
    public String register(RegisterRequest request) {

        // Checks if email already exists; if yes, throws error.
        if (userRepository.findByEmail(request.email).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Creates a new user, hashes the password with bcrypt, saves to DB.
        User user = new User();
        user.setEmail(request.email);
        user.setPasswordHash(passwordEncoder.encode(request.password));
        userRepository.save(user);

        // Returns a JWT for the new user.
        return jwtService.generateToken(user.getId());
    }

    // Method to authenticate a user.
    public String login(LoginRequest request){

        // Finds the user by email or throws if not found.
        User user = userRepository.findByEmail(request.email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        // Compares raw password to stored hash; throws on mismatch.
        if (!passwordEncoder.matches(request.password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Returns JWT if login is valid.
        return jwtService.generateToken(user.getId());
    }
}
