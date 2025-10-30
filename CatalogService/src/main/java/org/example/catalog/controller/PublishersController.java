package org.example.catalog.controller;


import lombok.RequiredArgsConstructor;
import org.example.catalog.entity.Publisher;
import org.example.catalog.service.CloudinaryService;
import org.example.catalog.service.PublishersService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/catalog/publishers")
@RequiredArgsConstructor
public class PublishersController {

    private final PublishersService publishersService;
    private final CloudinaryService cloudinaryService;

    // =================== GET ALL ===================
    @GetMapping
    public ResponseEntity<List<Publisher>> getAllPublishers() {
        List<Publisher> publishers = publishersService.findAll();
        return ResponseEntity.ok(publishers);
    }

    // =================== GET BY ID ===================
    @GetMapping("/{id}")
    public ResponseEntity<Publisher> getPublisherById(@PathVariable Long id) {
        Publisher publisher = publishersService.findById(id);
        return ResponseEntity.ok(publisher);
    }

    // =================== CREATE ===================
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Publisher> createPublisher(
            @RequestParam("publisherName") String publisherName,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "website", required = false) String website,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "foundedYear", required = false) Integer foundedYear,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "publisherImage", required = false) MultipartFile publisherImage
    ) throws IOException {

        Publisher publisher = new Publisher();
        publisher.setPublisherName(publisherName);
        publisher.setAddress(address);
        publisher.setPhone(phone);
        publisher.setEmail(email);
        publisher.setWebsite(website);
        publisher.setCountry(country);
        publisher.setFoundedYear(foundedYear);
        publisher.setDescription(description);

        if (publisherImage != null && !publisherImage.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(publisherImage);
            System.out.println("✅ Uploaded image URL: " + imageUrl);
            publisher.setPublisherImage(imageUrl);
        } else {
            System.out.println("⚠️ No image uploaded!");
        }

        Publisher savedPublisher = publishersService.save(publisher);
        return ResponseEntity.ok(savedPublisher);
    }

    // =================== UPDATE ===================
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Publisher> updatePublisher(
            @PathVariable Long id,
            @RequestParam("publisherName") String publisherName,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "website", required = false) String website,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "foundedYear", required = false) Integer foundedYear,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "publisherImage", required = false) MultipartFile publisherImage
    ) throws IOException {

        Publisher existingPublisher = publishersService.findById(id);
        existingPublisher.setPublisherName(publisherName);
        existingPublisher.setAddress(address);
        existingPublisher.setPhone(phone);
        existingPublisher.setEmail(email);
        existingPublisher.setWebsite(website);
        existingPublisher.setCountry(country);
        existingPublisher.setFoundedYear(foundedYear);
        existingPublisher.setDescription(description);

        if (publisherImage != null && !publisherImage.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(publisherImage);
            existingPublisher.setPublisherImage(imageUrl);
        }

        Publisher updatedPublisher = publishersService.update(existingPublisher);
        return ResponseEntity.ok(updatedPublisher);
    }

    // =================== DELETE ===================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deletePublisher(@PathVariable Long id) {
        publishersService.delete(id);
        return ResponseEntity.ok("✅ Publisher deleted successfully!");
    }
}
