package org.example.userservice.service;

import org.example.userservice.dto.*;

import java.util.Map;

public interface UserService {
    RegisterResponse registerUser(RegisterRequest registerRequest);

    LoginResponse loginUser(LoginRequest loginRequest);

    Map<String, String> refresh( Map<String, String> request);
    UserResponseDTO getUserById(Long userId);

}
