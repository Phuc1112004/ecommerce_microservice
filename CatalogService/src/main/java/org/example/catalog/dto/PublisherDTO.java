package org.example.catalog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

@Data
@Getter
@Setter
public class PublisherDTO {
    private Long publisherId;
    @NotBlank(message = "Tên nhà xuất bản không được để trống")
    private String publisherName;
    private String address;
    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Số điện thoại không hợp lệ")
    private String phone;
    @Email(message = "Email không hợp lệ")
    private String email;
    @URL(message = "Website không hợp lệ")
    private String website;
    private String country;
    @PastOrPresent(message = "Năm thành lập phải nhỏ hơn hoặc bằng ngày hiện tại")
    private LocalDate foundedYear;
    private String description;
}
