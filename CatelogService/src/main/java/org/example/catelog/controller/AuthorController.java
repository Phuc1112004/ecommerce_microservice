package org.example.catelog.controller;

import org.example.catelog.dto.AuthorDTO;
import org.example.catelog.service.AuthorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catelog/author")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    // ---------------- CREATE ----------------
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // chỉ ADMIN mới được tạo
    public ResponseEntity<AuthorDTO> createAuthor(@RequestBody AuthorDTO request) {
        AuthorDTO createAuthor = authorService.createAuthor(request);
        return ResponseEntity.ok(createAuthor);
    }

    // ---------------- READ ----------------
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')") // cả ADMIN và CUSTOMER đều được xem
    public ResponseEntity<List<AuthorDTO>> getAllAuthors() {
        return ResponseEntity.ok(authorService.getAllAuthor());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable Long id) {
        AuthorDTO author = authorService.getAuthorById(id);
        if (author == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(author);
    }

    // ---------------- UPDATE ----------------
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // chỉ ADMIN
    public ResponseEntity<AuthorDTO> updateAuthors(@PathVariable Long id,
                                                   @RequestBody AuthorDTO request) {
        AuthorDTO updatedAuthor = authorService.updateAuthor(id, request);
        if (updatedAuthor == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updatedAuthor);
    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // chỉ ADMIN
    public ResponseEntity<Void> deleteAuthors(@PathVariable Long id) {
        boolean deleted = authorService.deleteAuthor(id);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build(); // 204
    }
}
