package io.github.alexdikun.marketplace.service.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import io.github.alexdikun.marketplace.entities.CommentEntity;
import io.github.alexdikun.marketplace.exceptions.NotFoundException;
import io.github.alexdikun.marketplace.repository.CommentRepository;
import io.github.alexdikun.marketplace.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentSecurity {

    private final CommentRepository commentRepository;
    private final CurrentUserService currentUserService;

    public boolean isOwner(Long commentId) {
        log.info("Проверка полномочий пользователя на операцию c комментарием. commentId = {}", commentId);

        CommentEntity commentEntity = commentRepository.findById(commentId)
            .orElseThrow(() -> {
                log.warn("Комментарий не найден. commentId = {}", commentId);
                return new NotFoundException("Комментарий не найден");
        });

        if (!commentEntity.getUser().getId().equals(currentUserService.getCurrentUser().getId())) {
            log.warn("Пользователь не является автором комментария! userId = {}",
                currentUserService.getCurrentUser().getId());

            throw new AccessDeniedException("Пользователь не является автором комментария!");
        }

        return true;
    }
    
}
