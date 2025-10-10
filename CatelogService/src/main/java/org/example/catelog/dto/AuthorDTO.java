package org.example.catelog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AuthorDTO {
    private Long authorId;
    @NotBlank(message = "Tên tác giả không được để trống")
    private String authorName;
    private String biography;
}
