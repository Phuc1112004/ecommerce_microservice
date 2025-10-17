package org.example.catalog.service;

import org.example.catalog.entity.Publisher;

import java.util.List;

public interface PublishersService {
    int save(Publisher publisher);
    List<Publisher> findAll();
    Publisher findById(Long id);
    int update(Publisher publisher);
    int delete(Long id);
}
