package org.example.userservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, max = 50, message = "Username từ 3 đến 50 ký tự")
    @Column(name = "username")
    private String userName;
    @NotBlank(message = "Password không được để trống")
    @Size(min = 6, max = 100, message = "Password từ 6 đến 100 ký tự")
    private String password;
    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;
    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Số điện thoại không hợp lệ")
    private String phone;
    @NotBlank(message = "Role không được để trống")
    @Pattern(regexp = "^(ADMIN|CUSTOMER)$", message = "Role phải là 'ROLE_ADMIN' hoặc 'CUSTOMER'")
    private String role;

    // Thời điểm tạo user
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createAt;

//    @OneToMany(mappedBy = "user")
//    private List<Cart> carts;


//    @OneToMany(mappedBy = "users")
//    private List<Orders> orders;
//
//    @OneToMany(mappedBy = "users")
//    private List<Review> reviews;

}
