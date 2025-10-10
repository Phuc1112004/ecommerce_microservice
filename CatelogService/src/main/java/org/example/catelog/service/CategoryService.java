package org.example.catelog.service;

import org.example.catelog.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {
    CategoryDTO createCategory(CategoryDTO request);
    List<CategoryDTO> getAllCategory();
    CategoryDTO getCategoryById(Long id);
    CategoryDTO updateCategory(Long id, CategoryDTO request);
    boolean deleteCategory(Long id);
}
