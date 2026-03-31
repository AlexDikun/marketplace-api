package io.github.alexdikun.marketplace.service;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import io.github.alexdikun.marketplace.entities.UserEntity;
import io.github.alexdikun.marketplace.exceptions.UnauthorizedException;
import io.github.alexdikun.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CurrentUserService {
    
    private final UserRepository userRepository;

    public UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new UnauthorizedException("Пользователь не авторизирован!");
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();

        String keycloakId = jwt.getSubject();
        String login = jwt.getClaim("preferred_username");
        String email = jwt.getClaim("email");

        return userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setKeycloakId(keycloakId);
                    userEntity.setLogin(login);
                    userEntity.setEmail(email);
                    return userRepository.save(userEntity);
                });
    }

    // надо потом перепроверить код. Вроде, нигде не использую. Если так - удалить
    public String getCurrentUsername() { 
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getClaim("preferred_username");
    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

}
