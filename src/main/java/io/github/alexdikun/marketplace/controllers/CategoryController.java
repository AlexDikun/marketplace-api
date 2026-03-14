package io.github.alexdikun.marketplace.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.alexdikun.marketplace.request.CategoryRequest;
import io.github.alexdikun.marketplace.response.CategoryResponse;
import io.github.alexdikun.marketplace.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name= "Category", description = "API категорий")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Создание категории по модели")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Категория добавлена"),
        @ApiResponse(responseCode = "400", description = "Неверно переданные данные"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<CategoryResponse> createCategory(
        @Parameter(description = "Модель для создания данных")
        @RequestBody CategoryRequest request) {
        return new ResponseEntity<>(categoryService.createCategory(request), HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    @Operation(summary = "Получение категории по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Категория прочитана"),
        @ApiResponse(responseCode = "404", description = "Категория не найдена"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable Long id) {
        return new ResponseEntity<>(categoryService.getCategoryById(id), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Вернуть все категории")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список категорий прочитан"),
        @ApiResponse(responseCode = "404", description = "Никакие категории не найдены"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return new ResponseEntity<>(categoryService.getAllCategories(), HttpStatus.OK);
    }

    @PutMapping("{id}")
    @Operation(summary = "Изменение категории по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Категория изменена"),
        @ApiResponse(responseCode = "400", description = "Неверно переданные данные"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @RequestBody CategoryRequest request) {
        return new ResponseEntity<>(categoryService.updateCategoryById(id, request), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Удаление категории по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Категория удалена"),
        @ApiResponse(responseCode = "404", description = "Категория не найдена"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        return new ResponseEntity<>( HttpStatus.NO_CONTENT);
    }
    
}
