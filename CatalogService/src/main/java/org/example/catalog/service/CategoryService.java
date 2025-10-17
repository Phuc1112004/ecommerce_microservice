package org.example.catalog.service;

import org.example.catalog.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {
    CategoryDTO createCategory(CategoryDTO request);
    List<CategoryDTO> getAllCategory();
    CategoryDTO getCategoryById(Long id);
    CategoryDTO updateCategory(Long id, CategoryDTO request);
    boolean deleteCategory(Long id);
}
