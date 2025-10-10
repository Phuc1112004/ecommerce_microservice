package org.example.catelog.repository;

import org.example.catelog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIdIsNull();
    List<Category> findByParentId(Long parentId);

}
