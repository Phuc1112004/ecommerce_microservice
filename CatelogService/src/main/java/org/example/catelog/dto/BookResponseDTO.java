package org.example.catelog.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
public class BookResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long bookId;
    private String title;
    private String authorName;
    private Long authorId;
    private String publisherName;
    private String categoryName;
    private Long importPrice;
    private Long marketPrice;
    private Long salePrice;
    private Integer stockQuantity;
    private String description;
    private String imageUrl;
    private LocalDateTime createdAt;
    private Long bookNewId;
    private List<BookResponseDTO> bookResponseDTOList;
}
