package io.github.alexdikun.marketplace.controllers;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.alexdikun.marketplace.entities.CategoryEntity;
import io.github.alexdikun.marketplace.exceptions.ConflictException;
import io.github.alexdikun.marketplace.exceptions.NotFoundException;
import io.github.alexdikun.marketplace.request.CategoryRequest;
import io.github.alexdikun.marketplace.response.CategoryResponse;
import io.github.alexdikun.marketplace.service.CategoryService;
import io.github.alexdikun.marketplace.utils.TestFactoryData;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCategory_Success() throws Exception {
        CategoryEntity parentCategory = TestFactoryData.createCategory(null);
        CategoryRequest categoryRequest = TestFactoryData.createCategoryRequest(parentCategory);
        CategoryResponse expectedCategoryResponse = CategoryResponse.builder().id(1L).name(categoryRequest.getName()).build();

        when(categoryService.createCategory(categoryRequest)).thenReturn(expectedCategoryResponse);

        mockMvc.perform(post("/api/v1/categories")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value(categoryRequest.getName()));
    }

    @Test
    void createCategory_ValidationError() throws Exception {
        CategoryRequest invalidRequest = new CategoryRequest();
        invalidRequest.setName("");

        mockMvc.perform(post("/api/v1/categories")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createCategory_ShouldReturn403WhenAuthoritiesError() throws Exception {
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName("some data");

        mockMvc.perform(post("/api/v1/categories")
                .with(user("test").roles("USER"))
                .content(objectMapper.writeValueAsString(categoryRequest)))
            .andExpect(status().isForbidden());
    }

    @Test
    void getCategory_Success() throws Exception {
        Long categoryId = 1L;
        CategoryResponse expectedCategoryResponse = CategoryResponse.builder()
            .id(categoryId)
            .name("Test Category")
            .build();

        when(categoryService.getCategory(categoryId)).thenReturn(expectedCategoryResponse);

        mockMvc.perform(get("/api/v1/categories/{id}", categoryId)
            .with(jwt()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(categoryId))
            .andExpect(jsonPath("$.name").value("Test Category"));
    }

    @Test
    void getCategory_NotFound() throws Exception {
        Long nonExistentId = 999L;

        when(categoryService.getCategory(nonExistentId))
            .thenThrow(new NotFoundException("Категория не найдена"));

        mockMvc.perform(get("/api/v1/categories/{id}", nonExistentId)
            .with(jwt())) 
            .andExpect(status().isNotFound());
    }

    @Test
    void getAllCategories_Success() throws Exception {
        int page = 0;
        int size = 10;

        CategoryResponse response1 = CategoryResponse.builder()
            .id(1L)
            .name("Category 1")
            .build();

        CategoryResponse response2 = CategoryResponse.builder()
            .id(2L)
            .name("Category 2")
            .build();


        List<CategoryResponse> content = Arrays.asList(response1, response2);
        Page<CategoryResponse> pageResponse = new PageImpl<>(content);

        when(categoryService.getAllCategories(page, size)).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/categories")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .with(jwt())) 
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].id").value(1L))
            .andExpect(jsonPath("$.content[1].name").value("Category 2"));
    }

    @Test
    void updateCategory_Success() throws Exception {
        Long categoryId = 1L;
        CategoryRequest updateRequest = TestFactoryData.createCategoryRequest(null);
        updateRequest.setName("Updated Name");

         CategoryResponse updatedResponse = CategoryResponse.builder()
            .id(categoryId)
            .name(updateRequest.getName())
            .build();

        when(categoryService.updateCategory(categoryId, updateRequest))
            .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/categories/{id}", categoryId)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(categoryId))
            .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void updateCategory_Conflict() throws Exception {
        Long categoryId = 1L;
        CategoryRequest conflictingRequest = new CategoryRequest();
        conflictingRequest.setName("Existing Name");

        when(categoryService.updateCategory(categoryId, conflictingRequest))
            .thenThrow(new ConflictException("Такое название категории уже существует!"));

        mockMvc.perform(put("/api/v1/categories/{id}", categoryId)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(conflictingRequest)))
            .andExpect(status().isConflict());
    }


    @Test
    void updateCategory_ShouldReturn403WhenAuthoritiesError() throws Exception {
        Long categoryId = 1L;
        CategoryRequest conflictingRequest = new CategoryRequest();
        conflictingRequest.setName("Existing Name");

        when(categoryService.updateCategory(categoryId, conflictingRequest))
            .thenThrow(new ConflictException("Такое название категории уже существует!"));

        mockMvc.perform(put("/api/v1/categories/{id}", categoryId)
                .with(user("test").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(conflictingRequest)))
            .andExpect(status().isForbidden());
    }

    @Test
    void deleteCategory_Success() throws Exception {
        Long categoryId = 1L;

        mockMvc.perform(delete("/api/v1/categories/{id}", categoryId)
            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
            .andExpect(status().isNoContent());

        verify(categoryService).deleteCategory(categoryId);
    }

    @Test
    void deleteCategory_NotFound() throws Exception {
        Long nonExistentId = 999L;

        doThrow(new NotFoundException("Категория не найдена"))
            .when(categoryService).deleteCategory(nonExistentId);

        mockMvc.perform(delete("/api/v1/categories/{id}", nonExistentId)
            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteCategory_ShouldReturn403WhenAuthoritiesError() throws Exception {
        Long nonExistentId = 999L;

        doThrow(new NotFoundException("Категория не найдена"))
            .when(categoryService).deleteCategory(nonExistentId);

        mockMvc.perform(delete("/api/v1/categories/{id}", nonExistentId)
            .with(user("test").roles("USER")))
            .andExpect(status().isForbidden());
    }
}
