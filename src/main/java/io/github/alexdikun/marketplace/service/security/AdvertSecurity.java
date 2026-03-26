package io.github.alexdikun.marketplace.service.security;

import org.springframework.stereotype.Component;

import io.github.alexdikun.marketplace.entities.AdvertEntity;
import io.github.alexdikun.marketplace.exceptions.NotFoundException;
import io.github.alexdikun.marketplace.repository.AdvertRepository;
import io.github.alexdikun.marketplace.service.CurrentUserService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdvertSecurity {

    private final AdvertRepository advertRepository;

    private final CurrentUserService currentUserService;

    public boolean isOwner(Long advertId) {
        AdvertEntity advertEntity = advertRepository.findById(advertId)
            .orElseThrow(() -> new NotFoundException("Объявление не найдено"));

        return advertEntity.getUser().getId()
            .equals(currentUserService.getCurrentUser().getId());

    }
    
}
