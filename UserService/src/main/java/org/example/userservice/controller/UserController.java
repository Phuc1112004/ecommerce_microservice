package org.example.userservice.controller;


import org.example.userservice.dto.UserResponseDTO;
import org.example.userservice.entity.Users;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    // ✅ Chỉ ADMIN mới được truy cập
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ Chỉ CUSTOMER mới được truy cập
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/profile")
    public Users getProfile(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/{userId}/info")
    public ResponseEntity<UserResponseDTO> getUserInfo(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }
}
