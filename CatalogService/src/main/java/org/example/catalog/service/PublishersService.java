package org.example.catalog.service;

import org.example.catalog.dto.PublisherDTO;
import org.example.catalog.entity.Publisher;

import java.util.List;

public interface PublishersService {
    Publisher save(Publisher publisher);
    List<Publisher> findAll();
    Publisher findById(Long id);
    Publisher update(Publisher publisher);
    int delete(Long id);
}
