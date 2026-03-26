package io.github.alexdikun.marketplace.service.security;

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

        return imageEntity.getAdvert().getUser().getId()
            .equals(currentUserService.getCurrentUser().getId());
    }
    
}
