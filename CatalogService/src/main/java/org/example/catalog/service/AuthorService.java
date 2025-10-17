package org.example.catalog.service;

import org.example.catalog.dto.AuthorDTO;

import java.util.List;

public interface AuthorService {
    AuthorDTO createAuthor(AuthorDTO request);
    List<AuthorDTO> getAllAuthor();
    AuthorDTO getAuthorById(Long id);
    AuthorDTO updateAuthor(Long id, AuthorDTO request);
    boolean deleteAuthor(Long id);
}
