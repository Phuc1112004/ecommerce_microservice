package org.example.catalog.service.Impl;


import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import org.example.catalog.dto.AuthorDTO;
import org.example.catalog.entity.Author;
import org.example.catalog.repository.AuthorRepository;
import org.example.catalog.service.AuthorService;
import org.example.catalog.service.CloudinaryService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final CloudinaryService cloudinaryService;


    // ---------------- CREATE ----------------
    public AuthorDTO createAuthor(AuthorDTO request) {
        Author author = new Author();
        // Trim input để bỏ khoảng trắng đầu/cuối
        String name = request.getAuthorName() == null ? null : request.getAuthorName().trim();
        String bio = request.getBiography() == null ? null : request.getBiography().trim();

        author.setAuthorName(name);
        author.setBiography(bio);

        MultipartFile file = request.getAuthorImage();
        if (file != null && !file.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(file);
            author.setAuthorImage(imageUrl);
        }


        Author saved = authorRepository.save(author);
        return convertToDTO(saved);
    }


    // ---------------- READ ----------------
    public List<AuthorDTO> getAllAuthor() {
        return authorRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ---------------- FIND BY ID ----------------
    public AuthorDTO getAuthorById(Long id) {
        return authorRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    // ---------------- UPDATE ----------------
    public AuthorDTO updateAuthor(Long id, AuthorDTO request) {
        return authorRepository.findById(id)
                .map(author ->{
                    author.setAuthorName(request.getAuthorName());
                    author.setBiography(request.getBiography());

                    // 👉 Nếu có ảnh mới thì upload và thay ảnh cũ
                    MultipartFile file = request.getAuthorImage();
                    if (file != null && !file.isEmpty()) {
                        try {
                            String imageUrl = cloudinaryService.uploadFile(file);
                            author.setAuthorImage(imageUrl);
                        } catch (IOException e) {
                            throw new RuntimeException("Lỗi khi upload ảnh mới", e);
                        }
                    }

                    Author updated = authorRepository.save(author);
                    return convertToDTO(updated);
                }).orElse(null);
    }



    // ---------------- DELETE ----------------
    public boolean deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) return false;
        authorRepository.deleteById(id);
        return true;
    }


    // ---------------- CONVERT ----------------  những dữ liệu sinh ra khi create
    private AuthorDTO convertToDTO(Author author){
        AuthorDTO dto = new AuthorDTO();
        dto.setAuthorId(author.getAuthorId());
        dto.setAuthorName(author.getAuthorName());
        dto.setBiography(author.getBiography());
        dto.setAuthorImageUrl(author.getAuthorImage());
        return dto;
    }
}
