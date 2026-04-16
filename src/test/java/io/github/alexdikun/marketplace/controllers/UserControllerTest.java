package io.github.alexdikun.marketplace.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.alexdikun.marketplace.request.UserRequest;
import io.github.alexdikun.marketplace.response.UserResponse;
import io.github.alexdikun.marketplace.service.CurrentUserService;
import io.github.alexdikun.marketplace.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CurrentUserService currentUserService;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser
    void getAllUsers_ShouldReturnUsersList() throws Exception {
        UserResponse user1 = UserResponse.builder()
                                .displayName("User1")
                                .email("test1@mail.ru")
                                .build();

        UserResponse user2 = UserResponse.builder()
                                .displayName("User2")
                                .email("test2@mail.ru")
                                .build();

        List<UserResponse> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].email").value("test1@mail.ru"))
            .andExpect(jsonPath("$[0].displayName").value("User1"))
            .andExpect(jsonPath("$[1].email").value("test2@mail.ru"))
            .andExpect(jsonPath("$[1].displayName").value("User2"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser
    void getUser_ShouldReturnUser() throws Exception {
        Long userId = 1L;
        String email = "bigTasty@mail.ru";
        String displayName = "Anon";
        UserResponse userResponse = UserResponse.builder()
                                        .email("bigTasty@mail.ru")
                                        .displayName("Anon")
                                        .build();

        when(userService.getUser(userId)).thenReturn(userResponse);

        mockMvc.perform(get("/api/v1/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.displayName").value(displayName));

        verify(userService).getUser(userId);
    }

    @Test
    void updateUser_ShouldUpdateSuccessfully() throws Exception {
        String displayName = "JojoRef";  

        Map<String, Object> messengerLinks = new HashMap<>();
        messengerLinks.put("telegram", "8-(800)-555-35-35");
        messengerLinks.put("max", "max.ru/url");

        UserRequest userRequest = new UserRequest();
        userRequest.setDisplayName(displayName);
        userRequest.setMessengerLinks(messengerLinks);

        UserResponse updatedUser = UserResponse.builder()
                                .displayName("JojoRef")
                                .messengerLinks(messengerLinks)
                                .build();

        when(currentUserService.updateUser(any(UserRequest.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/v1/users/me")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))) 
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.displayName").value(displayName))
            .andExpect(jsonPath("$.messengerLinks").value(messengerLinks));

        verify(currentUserService).updateUser(any(UserRequest.class));
    }

    @Test
    void deleteUser_ShouldDeleteSuccessfully() throws Exception {
        doNothing().when(currentUserService).deleteUser();

        mockMvc.perform(delete("/api/v1/users/me")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
        )
        .andExpect(status().isNoContent());

        verify(currentUserService).deleteUser();
    }

    @Test
    @WithMockUser
    void getRoles_ShouldReturnRoles() throws Exception {
        List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");
        when(currentUserService.getRoles(any())).thenReturn(roles);

        mockMvc.perform(get("/api/v1/users/roles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").value("ROLE_USER"))
            .andExpect(jsonPath("$[1]").value("ROLE_ADMIN"));

        verify(currentUserService, times(1)).getRoles(any());
    }
}