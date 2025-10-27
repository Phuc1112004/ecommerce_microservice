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

        String username = request.getUserName() == null ? null : request.getUserName().trim();
        String email = request.getEmail() == null ? null : request.getEmail().trim();
        String phone = request.getPhone() == null ? null : request.getPhone().trim();
        String password = request.getPassword() == null ? null : request.getPassword().trim();

// Check blank trước
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên đăng nhập không được để trống");
        }
        if (password == null || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu không được để trống");
        }
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email không được để trống");
        }
        if (phone == null || phone.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số điện thoại không được để trống");
        }

// Sau đó mới check username tồn tại
        if (userRepository.findByUserName(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }

        // Check trùng
        if (userRepository.findByUserName(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên đăng nhập đã tồn tại");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email đã được sử dụng");
        }



    // Save user với giá trị đã trim
        Users user = new Users();
        user.setUserName(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole("CUSTOMER");
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

        String username = request.getUserName() == null ? null : request.getUserName().trim();
        String password = request.getPassword() == null ? null : request.getPassword().trim();

        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên đăng nhập không được để trống");
        }
        if (password == null || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu không được để trống");
        }


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
