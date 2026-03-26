package io.github.alexdikun.marketplace.service.security;

import org.springframework.stereotype.Component;

import io.github.alexdikun.marketplace.entities.CommentEntity;
import io.github.alexdikun.marketplace.exceptions.NotFoundException;
import io.github.alexdikun.marketplace.repository.CommentRepository;
import io.github.alexdikun.marketplace.service.CurrentUserService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommentSecurity {

    private final CommentRepository commentRepository;
    private final CurrentUserService currentUserService;

    public boolean isOwner(Long commentId) {
        CommentEntity commentEntity = commentRepository.findById(commentId)
            .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        return commentEntity.getUser().getId()
            .equals(currentUserService.getCurrentUser().getId());

    }
    
}
