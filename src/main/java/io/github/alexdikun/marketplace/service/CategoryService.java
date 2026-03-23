package io.github.alexdikun.marketplace.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.alexdikun.marketplace.entities.CategoryEntity;
import io.github.alexdikun.marketplace.exceptions.ConflictException;
import io.github.alexdikun.marketplace.exceptions.NotFoundException;
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
                .orElseThrow(() -> new NotFoundException("Родительская категория не найдена"));
            
            categoryEntity.setParentCategory(parentCategory);
        }

        CategoryEntity savedCategory = categoryRepository.save(categoryEntity);

        return categoryMapper.toCategoryResponse(savedCategory);
    }

    public CategoryResponse getCategory(Long id) {
        System.out.println("Получаем категорию по id: " + id);

        CategoryEntity categoryEntity = categoryRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Категория не найдена"));
        return categoryMapper.toCategoryResponse(categoryEntity);
    }

    public Page<CategoryResponse> getAllCategories(int page, int size) {
        System.out.println("Получаем список всех категорий");

        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryEntity> categoryPage = categoryRepository.findAll(pageable);

        return categoryPage.map(categoryMapper::toCategoryResponse);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        System.out.println("Изменение категории с id: " + id);
        
        CategoryEntity categoryEntity = categoryRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Категория не найдена"));

        if (categoryRequest.getName() != null &&
            categoryRepository.existsByNameAndIdNot(categoryRequest.getName(), id)) {

            throw new ConflictException("Такое название категории уже существует!");
        }

        if (categoryRequest.getParentId() != null && categoryRequest.getParentId().equals(id)) {
            throw new ConflictException("Категория не может быть родителем самой себя");
        }

        if (categoryRequest.getParentId() != null) {
            CategoryEntity parentCategory = categoryRepository.findById(categoryRequest.getParentId())
                .orElseThrow(() -> new NotFoundException("Родительская категория не найдена"));
            
            categoryEntity.setParentCategory(parentCategory);
        } else {
            categoryEntity.setParentCategory(null);
        }

        categoryMapper.updateCategoryFromDto(categoryRequest, categoryEntity);
        return categoryMapper.toCategoryResponse(categoryEntity);
    }

    @Transactional
    public void deleteCategory(Long id) {
        System.out.println("Удаляем категорию с id: " + id);
        
        CategoryEntity categoryEntity = categoryRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Категория не найдена"));
        
        categoryRepository.delete(categoryEntity);
    }

    
}
