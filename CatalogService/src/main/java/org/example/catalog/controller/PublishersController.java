package org.example.catalog.controller;

import jakarta.validation.Valid;
import org.example.catalog.entity.Publisher;
import org.example.catalog.service.PublishersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/publishers")
public class PublishersController {

    @Autowired
    private PublishersService publishersService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> create(@RequestBody @Valid Publisher publisher) {
        publishersService.save(publisher);
        return ResponseEntity.ok("Publisher created successfully!");
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<List<Publisher>> getAll() {
        return ResponseEntity.ok(publishersService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Publisher> getById(@PathVariable Long id) {
        return ResponseEntity.ok(publishersService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody @Valid Publisher publisher) {
        publisher.setPublisherId(id);
        publishersService.update(publisher);
        return ResponseEntity.ok("Publisher updated successfully!");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        publishersService.delete(id);
        return ResponseEntity.ok("Publisher deleted successfully!");
    }
}

