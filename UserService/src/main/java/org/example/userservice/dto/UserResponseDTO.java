package org.example.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private Long userId;
    private String userName;
    private String email;
    private String phone;
    private String role;
    private LocalDateTime createAt;
    private List<Long> orderIds ;
    private List<Long> reviewIds;

    public UserResponseDTO(Long userId, @NotBlank(message = "Username không được để trống") @Size(min = 3, max = 50, message = "Username từ 3 đến 50 ký tự") String userName, @Email(message = "Email không hợp lệ") @NotBlank(message = "Email không được để trống") String email, @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Số điện thoại không hợp lệ") String phone, @NotBlank(message = "Role không được để trống") @Pattern(regexp = "^(ADMIN|CUSTOMER)$", message = "Role phải là 'ROLE_ADMIN' hoặc 'CUSTOMER'") String role) {
    }
}
