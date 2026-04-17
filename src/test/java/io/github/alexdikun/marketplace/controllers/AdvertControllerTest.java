package io.github.alexdikun.marketplace.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is; 
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

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
import io.github.alexdikun.marketplace.exceptions.BadRequestException;
import io.github.alexdikun.marketplace.exceptions.NotFoundException;
import io.github.alexdikun.marketplace.request.AdvertRequest;
import io.github.alexdikun.marketplace.response.AdvertResponse;
import io.github.alexdikun.marketplace.service.AdvertService;
import io.github.alexdikun.marketplace.service.CommentService;
import io.github.alexdikun.marketplace.service.ImageService;
import io.github.alexdikun.marketplace.service.security.AdvertSecurity;
import io.github.alexdikun.marketplace.utils.TestFactoryData;

@WebMvcTest(AdvertController.class)
public class AdvertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdvertService advertService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private ImageService imageService;

    @MockBean
    private AdvertSecurity advertSecurity;

    @Test
    void createAdvert_ShouldCreateSuccessfully() throws Exception {
        CategoryEntity categoryEntity = TestFactoryData.createCategory(null);

        AdvertRequest advertRequest = TestFactoryData.createAdvertRequest(categoryEntity);
        AdvertResponse advertResponse = TestFactoryData.createAdvertResponse(advertRequest, 2L);

        when(advertService.createAdvert(advertRequest)).thenReturn(advertResponse);

        mockMvc.perform(post("/api/v1/adverts")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(advertRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value(advertResponse.getTitle()))
            .andExpect(jsonPath("$.cost").value(advertResponse.getCost()));

        verify(advertService).createAdvert(advertRequest);
    }

    @Test
    void createAdvert_ShouldReturn400WhenValidationFails() throws Exception {
        AdvertRequest invalidRequest = new AdvertRequest();
        invalidRequest.setTitle("");

        mockMvc.perform(post("/api/v1/adverts")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());

        verify(advertService, never()).createAdvert(any());
    }


    @Test
    void getAdvert_ShouldReturnAdvert() throws Exception {
        CategoryEntity categoryEntity = TestFactoryData.createCategory(null);
        Long id = 5L;
        AdvertRequest fakeDataForHttpGet = TestFactoryData.createAdvertRequest(categoryEntity);

        AdvertResponse advertResponse = TestFactoryData.createAdvertResponse(fakeDataForHttpGet, 4L);

        when(advertService.getAdvert(id)).thenReturn(advertResponse);

        mockMvc.perform(get("/api/v1/adverts/{id}", 5L)
            .with(jwt())) 
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(advertResponse.getTitle()));

        verify(advertService).getAdvert(id);
    }

    @Test
    void getAdvert_ShouldReturn404WhenNotFound() throws Exception {
        Long nonExistentId = 999L;

        when(advertService.getAdvert(nonExistentId))
            .thenThrow(new NotFoundException("Объявление не найдено"));

        mockMvc.perform(get("/api/v1/adverts/{id}", nonExistentId)
            .with(jwt())) 
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errors[0]", is("Объявление не найдено")));

        verify(advertService).getAdvert(nonExistentId);
    }

    @Test
    void searchAdverts_Success() throws Exception {
        String query = "приора";
        int page = 0;
        int size = 10;

        AdvertResponse advertResponse = AdvertResponse.builder()
            .id(1L)
            .title("приора на низкой подвеске")
            .build();

        List<AdvertResponse> content = Arrays.asList(advertResponse);
        Page<AdvertResponse> pageResponse = new PageImpl<>(content);

        when(advertService.searchAdverts(query, page, size)).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/adverts/search")
                .with(jwt())
                .param("query", query)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].id").value(1L))
            .andExpect(jsonPath("$.content[0].title").value("приора на низкой подвеске"));
    }

    @Test
    void searchAdverts_EmptyQuery() throws Exception {
        String query = "";

        when(advertService.searchAdverts(query, 0, 10))
            .thenThrow(new BadRequestException("Query cannot be empty"));

        mockMvc.perform(get("/api/v1/adverts/search")
                .with(jwt())
                .param("query", query))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateAdvert_ShouldReturn200_WhenUserIsOwner() throws Exception {
        Long advertId = 1L;
        AdvertRequest updateAdvertRequest = new AdvertRequest();
        updateAdvertRequest.setTitle("Обновлено");

        AdvertResponse response = AdvertResponse.builder()
            .id(advertId)
            .title("Обновлено")
            .build();

        when(advertSecurity.isOwner(advertId)).thenReturn(true);
        when(advertService.updateAdvert(eq(advertId), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/adverts/{id}", advertId)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAdvertRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(advertId));

        verify(advertService).updateAdvert(eq(advertId), any());
    }

    @Test
    void updateAdvertShouldReturn400WhenValidationError() throws Exception {
        Long advertId = 1L;

        AdvertRequest invalidRequest = new AdvertRequest();
        invalidRequest.setCost(new BigDecimal("-100.50"));

        when(advertSecurity.isOwner(advertId)).thenReturn(true);

        mockMvc.perform(put("/api/v1/adverts/{id}", advertId)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateAdvert_ShouldReturn403_WhenUserIsNotOwner() throws Exception {
        Long advertId = 1L;
        AdvertRequest advertRequest = new AdvertRequest();

        when(advertSecurity.isOwner(advertId)).thenReturn(false);

        mockMvc.perform(put("/api/v1/adverts/{id}", advertId)
                .with(user("test").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(advertRequest)))
            .andExpect(status().isForbidden());

        verify(advertService, never()).updateAdvert(any(), any());
    }

    @Test
    void deleteAdvert_ShouldReturn200_WhenUserIsOwnerOrAdmin() throws Exception {
        Long advertId = 34L;

        when(advertSecurity.isOwner(advertId)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/adverts/{id}", advertId)
                .with(jwt()))
            .andExpect(status().isNoContent());

        verify(advertService).deleteAdvert(advertId);
    }

    @Test
    void deleteAdvert_ShouldReturn404WhenNotFound() throws Exception {
        Long nonExistentId = 78L;

        doThrow(new NotFoundException("Объявление не найдено"))
            .when(advertService).deleteAdvert(nonExistentId);

        mockMvc.perform(delete("/api/v1/adverts/{id}", nonExistentId)
            .with(jwt())) 
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errors[0]", is("Объявление не найдено")));

        verify(advertService).deleteAdvert(nonExistentId);
    }


    @Test
    void deleteAdvert_ShouldReturn403_WhenUserIsNotOwner() throws Exception {
        Long advertId = 6L;

        when(advertSecurity.isOwner(advertId)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/adverts/{id}", advertId)
                .with(user("test").roles("USER")))
            .andExpect(status().isForbidden());

        verify(advertService, never()).updateAdvert(any(), any());
    }

}
