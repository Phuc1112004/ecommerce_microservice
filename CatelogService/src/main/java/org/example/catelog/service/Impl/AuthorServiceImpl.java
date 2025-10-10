package org.example.catelog.service.Impl;


import org.example.catelog.dto.AuthorDTO;
import org.example.catelog.entity.Author;
import org.example.catelog.repository.AuthorRepository;
import org.example.catelog.service.AuthorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    // ---------------- CREATE ----------------
    public AuthorDTO createAuthor(AuthorDTO request) {
        Author author = new Author();
        author.setAuthorName(request.getAuthorName());
        author.setBiography(request.getBiography());

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
        return dto;
    }
}
