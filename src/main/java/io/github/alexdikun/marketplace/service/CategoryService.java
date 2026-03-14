package io.github.alexdikun.marketplace.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.alexdikun.marketplace.entities.CategoryEntity;
import io.github.alexdikun.marketplace.mapper.CategoryMapper;
import io.github.alexdikun.marketplace.repository.CategoryRepository;
import io.github.alexdikun.marketplace.request.CategoryRequest;
import io.github.alexdikun.marketplace.response.CategoryResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    
    @Transactional
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        System.out.println("Cоздаем категорию!");
        
        CategoryEntity categoryEntity = categoryMapper.toCategoryEntity(categoryRequest);

        if (categoryRequest.getParentId() != null) {
            CategoryEntity parentCategory = categoryRepository.findById(categoryRequest.getParentId())
                .orElseThrow(() -> new RuntimeException("Родительская категория не найдена"));
            
            categoryEntity.setParentCategory(parentCategory);
        }

        CategoryEntity savedCategory = categoryRepository.save(categoryEntity);

        return categoryMapper.toCategoryResponse(savedCategory);
    }

    public CategoryResponse getCategoryById(Long id) {
        System.out.println("Получаем категорию по id: " + id);

        CategoryEntity categoryEntity = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Категория не найдена"));
        return categoryMapper.toCategoryResponse(categoryEntity);
    }

    public List<CategoryResponse> getAllCategories() {
        System.out.println("Получаем список всех категорий");

        List<CategoryEntity> allCategories = categoryRepository.findAll();
        return categoryMapper.toCategoryResponseList(allCategories);
    }

    @Transactional
    public CategoryResponse updateCategoryById(Long id, CategoryRequest categoryRequest) {
        System.out.println("Изменение категории с id: " + id);
        
        CategoryEntity categoryEntity = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        if (categoryRequest.getParentId() != null && categoryRequest.getParentId().equals(id)) {
            throw new RuntimeException("Категория не может быть родителем самой себя");
        }

        if (categoryRequest.getParentId() != null) {
            CategoryEntity parentCategory = categoryRepository.findById(categoryRequest.getParentId())
                .orElseThrow(() -> new RuntimeException("Родительская категория не найдена"));
            
            categoryEntity.setParentCategory(parentCategory);
        } else {
            categoryEntity.setParentCategory(null);
        }

        categoryMapper.updateCategoryFromDto(categoryRequest, categoryEntity);
        return categoryMapper.toCategoryResponse(categoryEntity);
    }

    @Transactional
    public void deleteCategoryById(Long id) {
        System.out.println("Удаляем категорию с id: " + id);
        
        CategoryEntity categoryEntity = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Категория не найдена"));
        
        categoryRepository.delete(categoryEntity);
    }

    
}
