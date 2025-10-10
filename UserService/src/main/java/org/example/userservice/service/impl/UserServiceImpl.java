package org.example.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.UserInfoDTO;
import org.example.userservice.dto.*;
import org.example.userservice.entity.Users;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.security.JwtUtil;
import org.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;



    @Override
    public RegisterResponse registerUser(RegisterRequest request) {
        if (userRepository.findByUserName(request.getUserName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }


        Users user = new Users();
        user.setUserName(request.getUserName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole("CUSTOMER"); // mặc định role CUSTOMER
        user.setCreateAt(LocalDateTime.now());

        Users savedUser = userRepository.save(user);

        RegisterResponse.UserData userData = new RegisterResponse.UserData(
                savedUser.getUserId(),
                savedUser.getUserName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getRole()
        );

        RegisterResponse response = new RegisterResponse(
                LocalDateTime.now(),
                HttpStatus.CREATED.value(),
                "Đăng ký thành công",
                userData
        );
        return response;
    }

    @Override
    public LoginResponse loginUser(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserName(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // ✅ Lấy thông tin user từ DB để có userId
        Users user = userRepository.findByUserName(request.getUserName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtUtil.generateAccessToken(userDetails, user.getUserId());
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        LoginResponse response = new LoginResponse(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Đăng nhập thành công",
                accessToken,
                refreshToken
        );
        return response;
    }

    public Map<String, String> refresh(Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null) {
            throw new RuntimeException() ;
//            ResponseEntity.badRequest().body(Map.of("error", "Refresh token is required"));
        }

        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new RuntimeException();
//            return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired refresh token"));
        }

        String username = jwtUtil.extractUsername(refreshToken);
        Users user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUserName())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();

        // ✅ Truyền thêm userId để giữ consistency
        String newAccessToken = jwtUtil.generateAccessToken(userDetails, user.getUserId());
        return Map.of("accessToken", newAccessToken);
    }

    public UserResponseDTO getUserById(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        return new UserResponseDTO(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );
    }

}
