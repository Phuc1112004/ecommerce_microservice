package org.example.catelog.entity;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "books")
public class Books {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;
    private String title;
    private Long importPrice;
    private Long marketPrice;
    private Long salePrice;
    private Integer stockQuantity;
    private String description;
    private String imageUrl;
    private LocalDateTime createdAt;
    private Long bookNewId;     //  sách mới nhất của bản chỉnh sửa

    private Long authorId;

    private Long publisherId;

    private Long categoryId;

}
