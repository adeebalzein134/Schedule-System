package com.example.smartschedule.controller;

import com.example.smartschedule.dto.AuthRequest;
import com.example.smartschedule.dto.AuthResponse;
import com.example.smartschedule.dto.RegisterRequest;
import com.example.smartschedule.entity.User;
import com.example.smartschedule.security.JwtUtil;
import com.example.smartschedule.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest dto) {
        User user = User.builder()
                .username(dto.username())
                .password(dto.password())
                .email(dto.email())
                .role(dto.role())
                .build();
        return ResponseEntity.ok(userService.registerUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        if (authentication.isAuthenticated()) {
            return ResponseEntity.ok(new AuthResponse(jwtUtil.generateToken(request.getUsername())));
        } else {
            throw new RuntimeException("Invalid user request");
        }
    }
}
