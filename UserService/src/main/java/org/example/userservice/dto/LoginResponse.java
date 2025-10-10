package org.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private String accessToken;
    private String refreshToken;
}

