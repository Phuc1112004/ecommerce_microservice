package org.example.common.dto;

import lombok.Data;

@Data
public class UserInfoDTO {
    private Long userId;
    private String userName;
    private String email;
    private String phone;
}

