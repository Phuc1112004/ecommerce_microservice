package org.example.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookInfoDTO {
    private Long bookId;
    private String title;
    private Long salePrice;
    private Integer stockQuantity;
}

