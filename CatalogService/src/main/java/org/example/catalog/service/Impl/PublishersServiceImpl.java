package org.example.catalog.service.Impl;

import org.example.catalog.entity.Publisher;
import org.example.catalog.repository.PublishersRepository;
import org.example.catalog.service.PublishersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublishersServiceImpl implements PublishersService {

    @Autowired
    private PublishersRepository publishersRepository;

    @Override
    public Publisher save(Publisher publisher) {
        publishersRepository.save(publisher);
        return publisher;
    }

    @Override
    public List<Publisher> findAll() {
        return publishersRepository.findAll();
    }

    @Override
    public Publisher findById(Long id) {
        return publishersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found with id " + id));
    }

    @Override
    public Publisher update(Publisher publisher) {
        publishersRepository.update(publisher);
        return publisher;
    }

    @Override
    public int delete(Long id) {
        return publishersRepository.delete(id);
    }
}
