package org.example.catalog.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.catalog.dto.PublisherDTO;
import org.example.catalog.entity.Publisher;
import org.example.catalog.service.PublisherService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/catalog/publisher")
public class PublisherController {

    private final PublisherService publisherService;


    // dùng store procedure demo
    // API: Lấy publisher theo tên
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<List<PublisherDTO>> searchPublishers(@RequestParam String name) {
        List<PublisherDTO> dtos = publisherService.getPublisherByNameDTO(name);
        return ResponseEntity.ok(dtos);
    }
    // API: Thêm publisher mới
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addPublisher(@RequestBody @Valid Publisher publisher) {
        publisherService.addPublisher(publisher);
        return ResponseEntity.ok("Publisher added successfully!");
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PublisherDTO> createPublishers(@RequestBody @Valid PublisherDTO request) {
        PublisherDTO createPublisher = publisherService.createPublisher(request);
        return ResponseEntity.ok(createPublisher);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<List<PublisherDTO>> getAllPublishers() {
        return ResponseEntity.ok(publisherService.getAllPublisher());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<PublisherDTO> getPublisherById(@PathVariable Long id) {
        PublisherDTO publisher = publisherService.getPublisherById(id);
        if (publisher == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(publisher);
    }

    // ---------------- UPDATE ----------------
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PublisherDTO> updatePublishers(@PathVariable Long id,
                                                   @RequestBody @Valid PublisherDTO request) {
        PublisherDTO updatedPublisher = publisherService.updatePublisher(id, request);
        if (updatedPublisher == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updatedPublisher);
    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAuthors(@PathVariable Long id) {
        boolean deleted = publisherService.deletePublisher(id);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build(); // 204
    }
}
