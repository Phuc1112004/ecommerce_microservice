package org.example.catelog.service;

import org.example.catelog.dto.AuthorDTO;

import java.util.List;

public interface AuthorService {
    AuthorDTO createAuthor(AuthorDTO request);
    List<AuthorDTO> getAllAuthor();
    AuthorDTO getAuthorById(Long id);
    AuthorDTO updateAuthor(Long id, AuthorDTO request);
    boolean deleteAuthor(Long id);
}
