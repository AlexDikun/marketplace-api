package io.github.alexdikun.marketplace.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import io.github.alexdikun.marketplace.request.CategoryRequest;
import io.github.alexdikun.marketplace.response.CategoryResponse;

@Service
public class CategoryService {
    
    public CategoryResponse createCategory(CategoryRequest request) {
        System.out.println("Cоздаем категорию!");

        return CategoryResponse.builder()
            .id(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE)
            .name(request.getName())
            .parentId(request.getParentId())
            .build();
    }

    public CategoryResponse getCategoryById(Long id) {
        System.out.println("Получаем категорию по id: " + id);

        return CategoryResponse.builder()
            .id(id)
            .name("Название категории")
            .parentId(null)
            .build();
    }

    public CategoryResponse updateCategoryByid(Long id, CategoryRequest request) {
        System.out.println("Изменение категории с id: " + id);

        return CategoryResponse.builder()
            .id(id)
            .name(request.getName())
            .parentId(request.getParentId()) 
            .build();
    }

    public String deleteCategoryById(Long id) {
        System.out.println("Удаляем категорию с id: " + id);
        return "Категория с id:" + id + "удалена!";
    }

    
}
