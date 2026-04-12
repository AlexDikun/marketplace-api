package io.github.alexdikun.marketplace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import io.github.alexdikun.marketplace.entities.CategoryEntity;
import io.github.alexdikun.marketplace.exceptions.ConflictException;
import io.github.alexdikun.marketplace.exceptions.NotFoundException;
import io.github.alexdikun.marketplace.mapper.CategoryMapper;
import io.github.alexdikun.marketplace.repository.CategoryRepository;
import io.github.alexdikun.marketplace.request.CategoryRequest;
import io.github.alexdikun.marketplace.response.CategoryResponse;
import io.github.alexdikun.marketplace.utils.TestFactoryData;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private CategoryEntity parentCategory;
    private CategoryEntity category;
    private CategoryRequest categoryRequest;

    @BeforeEach
    void setUp() {
        parentCategory = TestFactoryData.createCategory(null);
        category = TestFactoryData.createCategory(parentCategory);
        categoryRequest = TestFactoryData.createCategoryRequest(parentCategory);
    }

    @Test
    void createCategoryShouldCreateSuccessfullyWithoutParent() {
        categoryRequest.setParentId(null); 

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(categoryRequest.getName());

        when(categoryMapper.toCategoryEntity(categoryRequest)).thenReturn(categoryEntity);
        when(categoryRepository.save(any(CategoryEntity.class))).thenAnswer(invocation -> {
            CategoryEntity saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(categoryMapper.toCategoryResponse(any(CategoryEntity.class)))
            .thenReturn(CategoryResponse.builder().id(1L).name(categoryRequest.getName()).build());

        CategoryResponse response = categoryService.createCategory(categoryRequest);

        verify(categoryMapper).toCategoryEntity(categoryRequest);
        verify(categoryRepository).save(any(CategoryEntity.class));
        verify(categoryMapper).toCategoryResponse(any(CategoryEntity.class));

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(categoryRequest.getName());
        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    void createCategoryShouldCreateWithParent() {
        categoryRequest.setParentId(parentCategory.getId());

        parentCategory.setId(parentCategory.getId());
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(categoryRequest.getName());

        when(categoryMapper.toCategoryEntity(categoryRequest)).thenReturn(categoryEntity);
        when(categoryRepository.findById(parentCategory.getId())).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(CategoryEntity.class))).thenAnswer(invocation -> {
            CategoryEntity saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(categoryMapper.toCategoryResponse(any(CategoryEntity.class)))
            .thenReturn(CategoryResponse.builder()
                        .id(1L).name(categoryRequest.getName()).parentId(parentCategory.getId()).build());

        CategoryResponse response = categoryService.createCategory(categoryRequest);

        verify(categoryRepository).findById(parentCategory.getId());
        assertThat(response.getParentId()).isEqualTo(parentCategory.getId());
    }

    @Test
    void createCategoryShouldThrowNotFoundWhenParentNotFound() {
        Long parentId = 999L;
        categoryRequest.setParentId(parentId);

        CategoryEntity categoryEntity = new CategoryEntity();
        when(categoryMapper.toCategoryEntity(categoryRequest)).thenReturn(categoryEntity);
        when(categoryRepository.findById(parentId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            categoryService.createCategory(categoryRequest);
        });

        assertThat(exception.getMessage()).isEqualTo("Родительская категория не найдена");
        verify(categoryRepository).findById(parentId);
        verify(categoryMapper, never()).toCategoryResponse(org.mockito.ArgumentMatchers.any(CategoryEntity.class));
    }

    @Test
    void getCategoryShouldReturnCategory() {
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toCategoryResponse(category))
            .thenReturn(CategoryResponse.builder().id(categoryId).name("Название категории").build());

        CategoryResponse response = categoryService.getCategory(categoryId);

        verify(categoryRepository).findById(categoryId);
        verify(categoryMapper).toCategoryResponse(category);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(categoryId);
    }

    @Test
    void getCategoryShouldThrowNotFound() {
        Long categoryId = 999L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            categoryService.getCategory(categoryId);
        });

        assertThat(exception.getMessage()).isEqualTo("Категория не найдена");
        verify(categoryRepository).findById(categoryId);
        verify(categoryMapper, never()).toCategoryResponse(org.mockito.ArgumentMatchers.any(CategoryEntity.class));
    }

    @Test
    void getAllCategoriesShouldReturnPage() {
        int page = 0;
        int size = 10;

        List<CategoryEntity> categories = List.of(
            TestFactoryData.createCategory(null),
            TestFactoryData.createCategory(null)
        );
        Page<CategoryEntity> categoryPage = new PageImpl<>(categories, PageRequest.of(page, size), categories.size());

        when(categoryRepository.findAll(PageRequest.of(page, size))).thenReturn(categoryPage);
        when(categoryMapper.toCategoryResponse(any(CategoryEntity.class)))
            .thenAnswer(invocation -> {
                CategoryEntity entity = invocation.getArgument(0);
                return CategoryResponse.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .build();
            });

        Page<CategoryResponse> result = categoryService.getAllCategories(page, size);

        verify(categoryRepository).findAll(PageRequest.of(page, size));
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void updateCategoryShouldUpdateSuccessfully() {
        Long categoryId = 1L;
        CategoryRequest updateRequest = new CategoryRequest();
        updateRequest.setName("Обновлённое название");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameAndIdNot(updateRequest.getName(), categoryId)).thenReturn(false);
        when(categoryMapper.toCategoryResponse(category))
            .thenReturn(CategoryResponse.builder().id(categoryId).name(updateRequest.getName()).build());

        CategoryResponse response = categoryService.updateCategory(categoryId, updateRequest);

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).existsByNameAndIdNot(updateRequest.getName(), categoryId);
        verify(categoryMapper).updateCategoryFromDto(updateRequest, category);
        verify(categoryMapper).toCategoryResponse(category);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Обновлённое название");
    }

    @Test
    void updateCategoryShouldThrowConflictWhenNameExists() {
        Long categoryId = 1L;
        CategoryRequest request = new CategoryRequest();
        request.setName("Существующее название");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameAndIdNot(request.getName(), categoryId)).thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            categoryService.updateCategory(categoryId, request);
        });

        assertThat(exception.getMessage()).isEqualTo("Такое название категории уже существует!");
        verify(categoryRepository).findById(categoryId);
        verify(categoryMapper, never()).toCategoryResponse(org.mockito.ArgumentMatchers.any(CategoryEntity.class));
    }

    @Test
    void updateCategoryShouldThrowConflictWhenCategoryIsParentOfItself() {
        Long categoryId = 1L;
        CategoryRequest request = new CategoryRequest();
        request.setParentId(categoryId);

        CategoryEntity categoryEntity = TestFactoryData.createCategory(null);
        categoryEntity.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            categoryService.updateCategory(categoryId, request);
        });

        assertThat(exception.getMessage()).isEqualTo("Категория не может быть родителем самой себя");
        verify(categoryRepository).findById(categoryId);
        verify(categoryMapper, never()).toCategoryResponse(org.mockito.ArgumentMatchers.any(CategoryEntity.class));
    }

    @Test
    void updateCategoryShouldThrowNotFoundWhenParentNotFound() {
        Long categoryId = 1L;
        Long parentId = 999L;
        CategoryRequest request = new CategoryRequest();
        request.setParentId(parentId);

        CategoryEntity categoryEntity = TestFactoryData.createCategory(null);
        categoryEntity.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));
        when(categoryRepository.findById(parentId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            categoryService.updateCategory(categoryId, request);
        });

        assertThat(exception.getMessage()).isEqualTo("Родительская категория не найдена");
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).findById(parentId);
        verify(categoryMapper, never()).toCategoryResponse(org.mockito.ArgumentMatchers.any(CategoryEntity.class));
    }

    @Test
    void deleteCategoryShouldDeleteSuccessfully() {
        Long categoryId = 1L;
        category.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategoryShouldThrowNotFound() {
        Long categoryId = 999L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            categoryService.deleteCategory(categoryId);
        });

        assertThat(exception.getMessage()).isEqualTo("Категория не найдена");
        verify(categoryRepository).findById(categoryId);
    }

}
