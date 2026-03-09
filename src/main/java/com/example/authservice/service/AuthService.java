package com.example.authservice.service;


import com.example.authservice.dto.AuthRequest;
import com.example.authservice.dto.AuthResponse;
import com.example.authservice.entity.User;
import com.example.authservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Login Authentication
    public AuthResponse authenticate(AuthRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        log.info("User authenticated successfully: {}", request.getUsername());
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        log.info("User authenticated successfully: {}", user.getUsername());
        String token = jwtService.generateToken(request.getUsername());

        return new AuthResponse(token);
    }

    // Register new user
    public String register(AuthRequest request) {

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();
        if(user.getId() == null || user.getUsername() == null) {
            throw new IllegalArgumentException("Please fill mandatory fields");
        }
        userRepository.save(user);
        log.info("User registered successfully: {}", request.getUsername());

        return "User registered successfully";
    }
}
