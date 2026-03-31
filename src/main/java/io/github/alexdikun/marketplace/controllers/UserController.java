package io.github.alexdikun.marketplace.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
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
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@Validated
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
    public ResponseEntity<UserResponse> getUser(@PathVariable @Positive Long id) {
        return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
    }

    @PutMapping("/me")
    @Operation(summary = "Обновлениe пользователем собственных данных")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь обновлен!"),
        @ApiResponse(responseCode = "400", description = "Неверно переданные данные"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<UserResponse> updateUser(
        @Parameter(description = "Модель для создания данных") @RequestBody UserRequest userRequest
    ) {
        return new ResponseEntity<>(userService.updateUser(userRequest), HttpStatus.OK);
    }

    @DeleteMapping("/me")
    @Operation(summary = "Пользователь удаляет собственную учетную запись из сервиса")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Пользователь удален"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<Void> deleteUser() {
        userService.deleteUser();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/me")
    @Operation(summary = "Возвращает все данные в JWT из Keyloack. DEV-метод")
    public Map<String, Object> me(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken token)) {
            return Map.of("error", "not JWT auth");
        }

        Jwt jwt = token.getToken();

        Map<String, Object> result = new HashMap<>();
        result.put("username", jwt.getClaim("preferred_username"));
        result.put("email", jwt.getClaim("email"));
        result.put("sub", jwt.getSubject());
        result.put("roles", jwt.getClaim("realm_access"));

        return result;
    }

}
