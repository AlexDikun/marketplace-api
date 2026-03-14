package io.github.alexdikun.marketplace.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.alexdikun.marketplace.request.UserRequest;
import io.github.alexdikun.marketplace.response.UserResponse;
import io.github.alexdikun.marketplace.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "API пользователей")
public class UserController {
    
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Получение списка всех пользователей!")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список всех пользователей получен"),
        @ApiResponse(responseCode = "404", description = "Пользователи отсутствуют"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    @Operation(summary = "Получение пользователя по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь получен"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @PutMapping("{id}")
    @Operation(summary = "Обновление пользователя по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь обновлен!"),
        @ApiResponse(responseCode = "400", description = "Неверно переданные данные"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, 
        @Parameter(description = "Модель для создания данных") @RequestBody UserRequest userRequest
    ) {
        return new ResponseEntity<>(userService.updateUserById(id, userRequest), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Удаление пользователя по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь удален"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
