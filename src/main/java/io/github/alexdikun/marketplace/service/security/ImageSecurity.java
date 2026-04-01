package io.github.alexdikun.marketplace.service.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import io.github.alexdikun.marketplace.entities.ImageEntity;
import io.github.alexdikun.marketplace.exceptions.NotFoundException;
import io.github.alexdikun.marketplace.repository.ImageRepository;
import io.github.alexdikun.marketplace.service.CurrentUserService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ImageSecurity {

    private final ImageRepository imageRepository;
    private final CurrentUserService currentUserService;

    public boolean isOwner(Long imageId) {
        ImageEntity imageEntity = imageRepository.findById(imageId)
            .orElseThrow(() -> new NotFoundException("Изображение не найдено!"));

        if (!imageEntity.getAdvert().getUser().getId().equals(currentUserService.getCurrentUser().getId())) {
            throw new AccessDeniedException("Пользователь не является автором объявления и его изображений!");
        }

        return true;
    }
    
}
