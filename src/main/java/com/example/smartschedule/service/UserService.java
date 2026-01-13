package com.example.smartschedule.service;

import com.example.smartschedule.entity.User;
import com.example.smartschedule.entity.Role;
import com.example.smartschedule.exception.ValidationException;
import com.example.smartschedule.exception.ResourceNotFoundException;
import com.example.smartschedule.repository.UserRepository;
import com.example.smartschedule.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ValidationException("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Default role if not set? Or assume controller handles it.
        if (user.getRole() == null) {
            user.setRole(Role.CUSTOMER);
        }
        return userRepository.save(user);
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ValidationException("User not found"));
    }

    // Admin methods
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User updateUser(Long id, UserUpdateRequest request) {
        User user = getUserById(id);
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (Boolean.TRUE.equals(userRepository.existsByUsername(request.getUsername()))) {
                throw new ValidationException("Username already exists");
            }
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}
