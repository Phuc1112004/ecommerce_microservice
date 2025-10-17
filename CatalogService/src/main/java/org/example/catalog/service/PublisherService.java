package org.example.catalog.service;

import org.example.catalog.dto.PublisherDTO;
import org.example.catalog.entity.Publisher;

import java.util.List;

public interface PublisherService {
    PublisherDTO createPublisher(PublisherDTO request);
    List<PublisherDTO> getAllPublisher();
    PublisherDTO getPublisherById(Long id);
    PublisherDTO updatePublisher(Long id, PublisherDTO request);
    boolean deletePublisher(Long id);
    List<PublisherDTO> getPublisherByNameDTO(String name);
    void addPublisher(Publisher publisher);
}
