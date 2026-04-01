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

import io.github.alexdikun.marketplace.entities.UserEntity;
import io.github.alexdikun.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CurrentUserService {
    
    private final UserRepository userRepository;

    public UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new AuthenticationCredentialsNotFoundException("Пользователь не авторизирован!");
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

    public List<String> getRoles(Authentication authentication) {
        if (authentication == null) {
            throw new AuthenticationCredentialsNotFoundException("Пользователь не авторизирован!");
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        List<String> roles = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .filter(role -> role.equals("ROLE_USER"))
            .collect(Collectors.toList());

        return roles;
    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

}
