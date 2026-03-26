package io.github.alexdikun.marketplace.service.security;

import org.springframework.stereotype.Component;

import io.github.alexdikun.marketplace.exceptions.NotFoundException;
import io.github.alexdikun.marketplace.repository.UserRepository;
import io.github.alexdikun.marketplace.service.CurrentUserService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserSecurity {
    
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public boolean isOwner(Long userId) {
        if (userRepository.existsById(userId)) {
            return currentUserService.getCurrentUser().getId().equals(userId);
        } else {
            throw new NotFoundException("Пользователь не найден!");
        }
    }
    
}
