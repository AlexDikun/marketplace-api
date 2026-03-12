package io.github.alexdikun.marketplace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.github.alexdikun.marketplace.entities.CategoryEntity;
import io.github.alexdikun.marketplace.mapper.CategoryMapper;
import io.github.alexdikun.marketplace.request.CategoryRequest;
import io.github.alexdikun.marketplace.response.CategoryResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;
    
    public CategoryResponse createCategory(CategoryRequest request) {
        System.out.println("Cоздаем категорию!");

        CategoryEntity categoryEntity = categoryMapper.toCategoryEntity(request);
        return categoryMapper.toCategoryResponse(categoryEntity);
    }

    public CategoryResponse getCategoryById(Long id) {
        System.out.println("Получаем категорию по id: " + id);

        return CategoryResponse.builder()
            .id(id)
            .name("Название категории")
            .parentId(null)
            .build();
    }

    public List<CategoryResponse> getAllCategories() {
        System.out.println("Получаем список всех категорий");

        return List.of(
            CategoryResponse.builder().id(1L).name("Название категории 1").parentId(null).build(),
            CategoryResponse.builder().id(2L).name("Название категории 2").parentId(null).build()
        );
    }

    public CategoryResponse updateCategoryById(Long id, CategoryRequest request) {
        System.out.println("Изменение категории с id: " + id);
        
        CategoryEntity categoryEntity = categoryMapper.toCategoryEntity(request);
        CategoryEntity parentCategory = null;
        return categoryMapper.toCategoryResponse(categoryEntity);
    }

    public String deleteCategoryById(Long id) {
        System.out.println("Удаляем категорию с id: " + id);
        return "Категория с id: " + id + " удалена!";
    }

    
}
