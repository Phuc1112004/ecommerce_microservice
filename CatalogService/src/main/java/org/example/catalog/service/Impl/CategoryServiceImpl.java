package org.example.catalog.service.Impl;



import lombok.RequiredArgsConstructor;
import org.example.catalog.dto.CategoryDTO;
import org.example.catalog.entity.Category;
import org.example.catalog.repository.CategoryRepository;
import org.example.catalog.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;


    // ---------------- CREATE ----------------
    public CategoryDTO createCategory(CategoryDTO request) {
        Category category = new Category();
        category.setCategoryName(request.getCategoryName() == null ? null :request.getCategoryName().trim());
        category.setDescription(request.getDescription() == null ? null :request.getDescription().trim());
//        Category category = categoryRepository.findById(request.getCategoryId().orElseThrow(() -> new RuntimeException("Category not found"));

        if (request.getParentId() != null) {
            categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent not found"));
            category.setParentId(request.getParentId());
        } else {
            category.setParentId(null);
        }

        Category saved = categoryRepository.save(category);
        return convertToDTO(saved);
    }

    // ---------------- READ ----------------
    public List<CategoryDTO> getAllCategory() {
        return categoryRepository.findByParentIdIsNull()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    // ---------------- FIND BY ID ----------------
    public CategoryDTO getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    // ---------------- UPDATE ----------------
    public CategoryDTO updateCategory(Long id, CategoryDTO request) {
        return categoryRepository.findById(id)
                .map(category ->{
                    category.setCategoryName(request.getCategoryName());
                    category.setDescription(request.getDescription());

                    if (request.getParentId() != null) {
                        categoryRepository.findById(request.getParentId())
                                .orElseThrow(() -> new RuntimeException("Parent not found"));
                        category.setParentId(request.getParentId());
                    } else {
                        category.setParentId(null);
                    }

                    Category updated = categoryRepository.save(category);
                    return convertToDTO(updated);
                }) .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    // ---------------- DELETE ----------------
    public boolean deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) return false;
        categoryRepository.deleteById(id);
        return true;
    }

    // ---------------- CONVERT ----------------
    private CategoryDTO convertToDTO(Category category){
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId(category.getCategoryId());
        dto.setCategoryName(category.getCategoryName());
        dto.setDescription(category.getDescription());

        if (category.getParentId() != null) {
            dto.setParentId(category.getParentId());
            // Nếu muốn hiển thị luôn parentName thì phải query thêm từ repository
            categoryRepository.findById(category.getParentId())
                    .ifPresent(parent -> dto.setParentName(parent.getCategoryName()));
        }

        // children
        List<Category> children = categoryRepository.findByParentId(category.getCategoryId());
        if (children != null && !children.isEmpty()) {
            List<CategoryDTO> childDTOs = children
                    .stream()
                    .map(this::convertToDTO) // đệ quy
                    .toList();
            dto.setChildren(childDTOs);
        }
        return dto;
    }
}
