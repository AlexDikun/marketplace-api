package io.github.alexdikun.marketplace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.github.alexdikun.marketplace.enums.Role;
import io.github.alexdikun.marketplace.request.UserRequest;
import io.github.alexdikun.marketplace.response.UserResponse;

@Service
public class UserService {
    
    public List<UserResponse> getAllUsers() {
        System.out.println("Получаем список всех пользователей!");

        return List.of(
            UserResponse.builder()
            .id(1L)
            .name("FakeName")
            .login("ADMIN")
            .role(Role.ROLE_ADMIN)
            .build(),

            UserResponse.builder()
            .id(2L)
            .name("FakeName")
            .login("factoryBot")
            .role(Role.ROLE_USER)
            .build()
        );
    } 
    
    public UserResponse getUserById(Long id) {
        System.out.println("Получаем пользователя по id: " + id);

        return UserResponse.builder()
            .id(id)
            .name("Чье-то имя")
            .login("Чей-то логин")
            .role(Role.ROLE_USER)
            .build();
    }

    public UserResponse updateUserById(Long id, UserRequest userRequest) {
        System.out.println("Обновление пользователя с id: " + id);

        return UserResponse.builder()
            .id(id)
            .name(userRequest.getName())
            .login(userRequest.getLogin())
            .role(userRequest.getRole())
            .build();
    }

    public String deleteUserById(Long id) {
        System.out.println("Удаляем пользователя с id: " + id);
        return "Пользователь с id: " + id + " удален!";
    }
}
