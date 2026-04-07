package io.github.alexdikun.marketplace.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.alexdikun.marketplace.entities.UserEntity;
import io.github.alexdikun.marketplace.mapper.UserMapper;
import io.github.alexdikun.marketplace.repository.UserRepository;
import io.github.alexdikun.marketplace.request.UserRequest;
import io.github.alexdikun.marketplace.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrentUserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserEntity getCurrentUser() {
        log.info("Получаем авторизованного пользователя!");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.debug("Пользователь не авторизирован!");
            throw new AuthenticationCredentialsNotFoundException("Пользователь не авторизирован!");
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();

        String keycloakId = jwt.getSubject();
        String login = jwt.getClaim("preferred_username");
        String email = jwt.getClaim("email");

        return userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> {
                    log.debug("Пользователь впервые посетил мой маркетплейс! Регистрируем его в моей БД...");
                    UserEntity userEntity = new UserEntity();
                    userEntity.setKeycloakId(keycloakId);
                    userEntity.setLogin(login);
                    userEntity.setEmail(email);
                    return userRepository.save(userEntity);
                });
    }

    public List<String> getRoles(Authentication authentication) {
        log.info("Получаем роли авторизованного пользователя!");

        if (authentication == null) {
            log.debug("Пользователь не авторизирован!");
            throw new AuthenticationCredentialsNotFoundException("Пользователь не авторизирован!");
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        List<String> roles = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .filter(role -> role.equals("ROLE_USER"))
            .collect(Collectors.toList());

        return roles;
    }

    @Transactional
    public UserResponse updateUser(UserRequest userRequest) {
        log.info("Обновление пользователя с id: " + getCurrentUser().getId());

        UserEntity userEntity = getCurrentUser();

        userMapper.updateUserFromDto(userRequest, userEntity);
        return userMapper.toUserResponse(userEntity);
    }

    @Transactional
    public void deleteUser() {
        log.info("Удаляем пользователя с id: " + getCurrentUser().getId());

        UserEntity userEntity = getCurrentUser();
        userRepository.delete(userEntity);
    }

}
