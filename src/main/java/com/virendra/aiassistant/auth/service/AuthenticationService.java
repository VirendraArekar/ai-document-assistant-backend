package com.virendra.aiassistant.auth.service;

import com.virendra.aiassistant.auth.dto.AuthResponse;
import com.virendra.aiassistant.auth.dto.LoginRequest;
import com.virendra.aiassistant.auth.dto.RegisterRequest;
import com.virendra.aiassistant.auth.entity.Role;
import com.virendra.aiassistant.auth.entity.User;
import com.virendra.aiassistant.auth.repository.UserRepository;
import com.virendra.aiassistant.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .enabled(true)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(
                new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        java.util.List.of()
                )
        );

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(

                new UsernamePasswordAuthenticationToken(

                        request.getEmail(),

                        request.getPassword()

                )

        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(

                new org.springframework.security.core.userdetails.User(

                        user.getEmail(),

                        user.getPassword(),

                        java.util.List.of()

                )

        );

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}