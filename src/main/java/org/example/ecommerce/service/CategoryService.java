package org.example.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.example.ecommerce.entity.Category;
import org.example.ecommerce.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow();
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category category) {
        Category category1 = categoryRepository.findById(id).orElseThrow();
        category1.setName(category.getName());
        return categoryRepository.save(category1);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

}
