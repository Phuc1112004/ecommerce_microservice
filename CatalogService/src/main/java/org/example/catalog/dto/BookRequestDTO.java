package org.example.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class BookRequestDTO {
    @NotBlank(message = "Tiêu đề sách không được để trống")
    private String title;
    @NotNull(message = "Tác giả không được để trống")
    private Long authorId;
    @NotNull(message = "Nhà xuất bản không được để trống")
    private Long publisherId;
    @NotNull(message = "Thể loại không được để trống")
    private Long categoryId;
    @PositiveOrZero(message = "Giá nhập phải >= 0")
    @NotNull(message = "Giá nhập không được để trống")
    private Long importPrice;
    @PositiveOrZero(message = "Giá thị trường phải >= 0")
    @NotNull(message = "Giá thị trường không được để trống")
    private Long marketPrice;
    @PositiveOrZero(message = "Giá bán phải >= 0")
    @NotNull(message = "Giá bán không được để trống")
    private Long salePrice;
    @PositiveOrZero(message = "Số lượng kho phải >= 0")
    @NotNull(message = "Số lượng kho không được để trống")
    private Integer stockQuantity;
    private String description;
    private String imageUrl;

    private Long bookId;
}
