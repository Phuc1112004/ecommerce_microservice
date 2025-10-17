package org.example.catalog.service.Impl;


import org.example.catalog.dto.PublisherDTO;
import org.example.catalog.entity.Publisher;
import org.example.catalog.repository.PublisherRepository;
import org.example.catalog.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublisherServiceImpl implements PublisherService {

    @Autowired
    private final PublisherRepository publisherRepository;

    public PublisherServiceImpl(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    // ---------------- CREATE ----------------
    public PublisherDTO createPublisher(PublisherDTO request) {
        Publisher publisher = new Publisher();
        publisher.setPublisherName(request.getPublisherName());
        publisher.setAddress(request.getAddress());
        publisher.setPhone(request.getPhone());
        publisher.setEmail(request.getEmail());
        publisher.setWebsite(request.getWebsite());
        publisher.setCountry(request.getCountry());
        publisher.setFoundedYear(request.getFoundedYear());
        publisher.setDescription(request.getDescription());

        Publisher saved = publisherRepository.save(publisher);
        return convertToDTO(saved);
    }

    // ---------------- READ ----------------
    public List<PublisherDTO> getAllPublisher() {
        return publisherRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    // ---------------- FIND BY ID ----------------
    public PublisherDTO getPublisherById(Long id) {
        return publisherRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    // ---------------- UPDATE ----------------
    public PublisherDTO updatePublisher(Long id, PublisherDTO request) {
        return publisherRepository.findById(id)
                .map(publisher ->{
                    publisher.setPublisherName(request.getPublisherName());
                    publisher.setAddress(request.getAddress());
                    publisher.setPhone(request.getPhone());
                    publisher.setEmail(request.getEmail());
                    publisher.setWebsite(request.getWebsite());
                    publisher.setCountry(request.getCountry());
                    publisher.setFoundedYear(request.getFoundedYear());
                    publisher.setDescription(request.getDescription());

                    Publisher updated = publisherRepository.save(publisher);
                    return convertToDTO(updated);
                }).orElse(null);
    }

    // ---------------- DELETE ----------------
    public boolean deletePublisher(Long id) {
        if (!publisherRepository.existsById(id)) return false;
        publisherRepository.deleteById(id);
        return true;
    }

    // ---------------- CONVERT ----------------
    private PublisherDTO convertToDTO(Publisher publisher){
        PublisherDTO dto = new PublisherDTO();
        dto.setPublisherId(publisher.getPublisherId());
        dto.setPublisherName(publisher.getPublisherName());
        dto.setAddress(publisher.getAddress());
        dto.setPhone(publisher.getPhone());
        dto.setEmail(publisher.getEmail());
        dto.setWebsite(publisher.getWebsite());
        dto.setCountry(publisher.getCountry());
        dto.setFoundedYear(publisher.getFoundedYear());
        dto.setDescription(publisher.getDescription());
        return dto;
    }

// Dùng store Procedure
    // Lấy danh sách publisher theo tên
    @Transactional
    public List<PublisherDTO> getPublisherByNameDTO(String name) {
        return publisherRepository.getPublisherByName(name)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Thêm publisher mới
    @Transactional(readOnly = true)
    public void addPublisher(Publisher publisher) {
        publisherRepository.addPublisher(
                publisher.getPublisherName(),
                publisher.getAddress(),
                publisher.getPhone(),
                publisher.getEmail(),
                publisher.getWebsite(),
                publisher.getCountry(),
                publisher.getFoundedYear(),
                publisher.getDescription()
        );
    }
}
