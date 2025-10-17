package org.example.catalog.service.Impl;


import org.example.catalog.entity.Publisher;
import org.example.catalog.repository.PublishersRepository;
import org.example.catalog.service.PublishersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// sử dụng namedJDBC
@Service
public class PublishersServiceImpl implements PublishersService {
    @Autowired
    private PublishersRepository publishersRepository;

    public int save(Publisher publisher) {
        return publishersRepository.save(publisher);
    }

    public List<Publisher> findAll() {
        return publishersRepository.findAll();
    }

    public Publisher findById(Long id) {
        return publishersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found with id " + id));
    }

    public int update(Publisher publisher) {
        return publishersRepository.update(publisher);
    }

    public int delete(Long id) {
        return publishersRepository.delete(id);
    }
}
