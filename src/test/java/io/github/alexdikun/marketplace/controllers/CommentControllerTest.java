package io.github.alexdikun.marketplace.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.alexdikun.marketplace.exceptions.NotFoundException;
import io.github.alexdikun.marketplace.request.CommentRequest;
import io.github.alexdikun.marketplace.response.CommentResponse;
import io.github.alexdikun.marketplace.service.CommentService;
import io.github.alexdikun.marketplace.service.security.CommentSecurity;
import io.github.alexdikun.marketplace.utils.TestFactoryData;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private CommentSecurity commentSecurity;

    private CommentRequest commentRequest;

    @BeforeEach
    void setUp() {
        CommentRequest commentRequest = TestFactoryData.createCommentRequest(null);
    }
    
    @Test
    void getComment_ShouldReturn200WhenSuccess() throws Exception {
        Long commentId = 12L;

        CommentResponse response = CommentResponse.builder()
                .id(commentId)
                .content(commentRequest.getContent())
                .build();

        when(commentService.getComment(commentId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/comment/{id}", commentId)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId))
                .andExpect(jsonPath("$.content").value(commentRequest.getContent()));
    }

    @Test
    void getComment_ShouldReturn404WhenNotFound() throws Exception {
        Long nonExistentId = 44L;

        doThrow(new NotFoundException("Комментарий не найден"))
            .when(commentService).getComment(nonExistentId);

        mockMvc.perform(get("/api/v1/comment/{id}", nonExistentId)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateComment_ShouldReturn200WhenSuccess() throws Exception {
        Long commentId = 17L;

        CommentResponse response = CommentResponse.builder()
                .id(commentId)
                .content(commentRequest.getContent())
                .build();

        when(commentSecurity.isOwner(commentId)).thenReturn(true);
        when(commentService.updateComment(eq(commentId), any(CommentRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/comment/{id}", commentId)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(commentRequest.getContent()));
    }

    @Test
    void updateComment_shouldReturn404WhenNotFound() throws Exception {
        Long nonExistentId = 667L;

        doThrow(new NotFoundException("Комментарий не найден"))
            .when(commentService).updateComment(nonExistentId, commentRequest);

        mockMvc.perform(put("/api/v1/comment/{id}", nonExistentId)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateComment_ShouldReturn400WhenValidationError() throws Exception {
        Long commentId = 19L;
        CommentRequest invalidRequest = new CommentRequest();
        invalidRequest.setContent(""); 

        mockMvc.perform(put("/api/v1/comment/{id}", commentId)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateComment_ShouldReturn403WhenNotOwner() throws Exception {
        Long commentId = 14L;
        when(commentSecurity.isOwner(commentId)).thenReturn(false);

        mockMvc.perform(put("/api/v1/comment/{id}", commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
            .andExpect(status().isForbidden());
    }

    @Test
    void deleteComment_shouldReturn404WhenSuccess() throws Exception {
        Long commentId = 7L;
        
        when(commentSecurity.isOwner(commentId)).thenReturn(true);
        doNothing().when(commentService).deleteComment(commentId);

        mockMvc.perform(delete("/api/v1/comment/{id}", commentId)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isNoContent());

        verify(commentService).deleteComment(commentId);
    }

    @Test
    void deleteComment_ShouldReturn404404WhenNotFound() throws Exception {
        Long nonExistentId = 667L;

        doThrow(new NotFoundException("Комментарий не найден"))
            .when(commentService).deleteComment(nonExistentId);

        mockMvc.perform(delete("/api/v1/comment/{id}", nonExistentId)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteComment_ShouldReturn403WhenNotOwner() throws Exception {
        Long commentId = 78L;
        when(commentSecurity.isOwner(commentId)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/comment/{id}", commentId))
            .andExpect(status().isForbidden());
    }

}
