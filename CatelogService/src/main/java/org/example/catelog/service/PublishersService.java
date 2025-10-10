package org.example.catelog.service;

import org.example.catelog.entity.Publisher;

import java.util.List;

public interface PublishersService {
    int save(Publisher publisher);
    List<Publisher> findAll();
    Publisher findById(Long id);
    int update(Publisher publisher);
    int delete(Long id);
}
