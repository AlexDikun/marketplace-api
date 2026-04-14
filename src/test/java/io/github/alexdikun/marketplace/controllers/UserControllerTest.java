package io.github.alexdikun.marketplace.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import io.github.alexdikun.marketplace.entities.UserEntity;
import io.github.alexdikun.marketplace.request.UserRequest;
import io.github.alexdikun.marketplace.response.UserResponse;
import io.github.alexdikun.marketplace.service.CurrentUserService;
import io.github.alexdikun.marketplace.service.UserService;
import io.github.alexdikun.marketplace.utils.TestFactoryData;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
            .andExpect(jsonPath("$[0].email").value(1))
            .andExpect(jsonPath("$[0].displayName").value("User1"))
            .andExpect(jsonPath("$[1].email").value(2))
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
    @WithMockUser
    void updateUser_ShouldUpdateSuccessfully() throws Exception {
        String displayName = "JojoRef";  

        Map<String, Object> messengerLinks = new HashMap<>();
        messengerLinks.put("telegram", "8-(800)-555-35-35");
        messengerLinks.put("max", "max.ru/url");

        UserRequest userRequest = new UserRequest();
        userRequest.setDisplayName(displayName);
        userRequest.setMessengerLinks(messengerLinks);

        UserResponse updatedUser = UserResponse.builder()
                                .displayName("User1")
                                .messengerLinks(messengerLinks)
                                .build();

        when(currentUserService.updateUser(any(UserRequest.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/v1/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Updated Name\", \"email\": \"updated@email.com\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Updated Name"))
            .andExpect(jsonPath("$.email").value("updated@email.com"));

        verify(currentUserService).updateUser(any(UserRequest.class));
    }

    @Test
    @WithMockUser
    void deleteUser_ShouldDeleteSuccessfully() throws Exception {

        UserEntity testUser = TestFactoryData.createUser();

        when(currentUserService.getCurrentUser()).thenReturn(testUser);

        mockMvc.perform(delete("/api/v1/users/me")
            .with(user("testuser@example.com").roles("USER"))) // не работает
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