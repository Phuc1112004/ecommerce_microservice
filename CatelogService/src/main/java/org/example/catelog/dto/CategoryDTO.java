package org.example.catelog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class CategoryDTO {
    private Long categoryId;
    @NotBlank(message = "Tên category không được để trống")
    private String categoryName;
    private String description;
    private Long parentId;
    private String parentName;

    private List<CategoryDTO> children;
}
