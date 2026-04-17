package io.github.alexdikun.marketplace.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is; 

import java.util.Arrays;
import java.util.List;

import org.hibernate.query.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.alexdikun.marketplace.entities.CategoryEntity;
import io.github.alexdikun.marketplace.entities.UserEntity;
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
}
