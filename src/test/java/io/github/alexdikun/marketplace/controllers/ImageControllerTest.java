package io.github.alexdikun.marketplace.controllers;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import io.github.alexdikun.marketplace.exceptions.NotFoundException;
import io.github.alexdikun.marketplace.response.ImageResponse;
import io.github.alexdikun.marketplace.service.ImageService;
import io.github.alexdikun.marketplace.service.security.ImageSecurity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
public class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageService imageService;

    @MockBean
    private ImageSecurity imageSecurity;

    @Test
    void getImage_ShouldReturn200WhenSuccess() throws Exception {
        Long imageId = 1L;

        ImageResponse expectedImageResponse = ImageResponse.builder()
            .id(imageId)
            .url("/images/test.jpg")
            .build();

        when(imageService.getImage(imageId)).thenReturn(expectedImageResponse);

        mockMvc.perform(get("/api/v1/image/{id}", imageId)
            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(imageId))
            .andExpect(jsonPath("$.url").value("/images/test.jpg"));
    }

    @Test
    void getImage_ShouldReturn404WheNotFound() throws Exception {
        Long nonExistentId = 999L;

        doThrow(new NotFoundException("Изображение не найдено"))
            .when(imageService).getImage(nonExistentId);

        mockMvc.perform(get("/api/v1/image/{id}", nonExistentId)
            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
            .andExpect(status().isNotFound());
    }

    @Test
    void getImage_ShouldReturn500WhenServiceError() throws Exception {
        Long imageId = 1L;

        doThrow(new RuntimeException("Internal server error"))
            .when(imageService).getImage(imageId);

        mockMvc.perform(get("/api/v1/image/{id}", imageId)
            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteImage_ShouldReturn404WhenSuccess() throws Exception {
        Long imageId = 1L;

        when(imageSecurity.isOwner(imageId)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/image/{id}", imageId)
            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
            .andExpect(status().isNoContent());

        verify(imageService).deleteImage(imageId);
    }

    @Test
    void deleteImage_ShouldReturn403WhenOwnerForbidden() throws Exception {
        Long imageId = 1L;

        when(imageSecurity.isOwner(imageId)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/image/{id}", imageId))
            .andExpect(status().isForbidden());
    }

    @Test
    void deleteImage_AdminCanDelete() throws Exception {
        Long imageId = 1L;

        mockMvc.perform(delete("/api/v1/image/{id}", imageId)
            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
            .andExpect(status().isNoContent());

        verify(imageService).deleteImage(imageId);
    }

    @Test
    void deleteImage_ShouldReturn404WhenNotFound() throws Exception {
        Long nonExistentId = 999L;

        when(imageSecurity.isOwner(nonExistentId)).thenReturn(true);
        doThrow(new NotFoundException("Изображение не найдено"))
            .when(imageService).deleteImage(nonExistentId);

        mockMvc.perform(delete("/api/v1/image/{id}", nonExistentId)
            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteImage_ShouldReturn500WhenServiceError() throws Exception {
        Long imageId = 1L;

        when(imageSecurity.isOwner(imageId)).thenReturn(true);
        doThrow(new RuntimeException("Internal server error"))
            .when(imageService).deleteImage(imageId);

        mockMvc.perform(delete("/api/v1/image/{id}", imageId)
            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteImage_ShouldReturn403WhenAuthorizeError() throws Exception {
        mockMvc.perform(delete("/api/v1/image/{id}", 1L))
            .andExpect(status().isForbidden());
    }
        
}
