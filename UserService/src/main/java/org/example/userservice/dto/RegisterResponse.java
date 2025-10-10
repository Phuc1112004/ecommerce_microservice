package org.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class RegisterResponse {
    private LocalDateTime timestamp; // thời gian phản hồi
    private int status;              // HTTP status code
    private String message;          // thông điệp
    private UserData data;           // thông tin user sau khi đăng ký

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserData {
        private Long id;
        private String username;
        private String email;
        private String phone;
        private String role;
    }
}

