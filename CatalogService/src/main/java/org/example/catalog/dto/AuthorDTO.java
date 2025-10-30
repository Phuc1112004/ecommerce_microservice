package org.example.catalog.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Data
@Getter
@Setter
public class AuthorDTO {
    private Long authorId;
    @NotBlank(message = "Tên tác giả không được để trống")
    private String authorName;
    private String biography;
    private String authorImageUrl;
    @JsonIgnore
    private MultipartFile authorImage;
}
