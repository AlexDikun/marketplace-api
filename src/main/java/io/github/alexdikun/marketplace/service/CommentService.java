package io.github.alexdikun.marketplace.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.alexdikun.marketplace.entities.AdvertEntity;
import io.github.alexdikun.marketplace.entities.CommentEntity;
import io.github.alexdikun.marketplace.entities.UserEntity;
import io.github.alexdikun.marketplace.exceptions.ConflictException;
import io.github.alexdikun.marketplace.exceptions.NotFoundException;
import io.github.alexdikun.marketplace.mapper.CommentMapper;
import io.github.alexdikun.marketplace.repository.AdvertRepository;
import io.github.alexdikun.marketplace.repository.CommentRepository;
import io.github.alexdikun.marketplace.request.CommentRequest;
import io.github.alexdikun.marketplace.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CurrentUserService currentUserService;
    private final AdvertRepository advertRepository;

    @Transactional
    public CommentResponse createComment(Long advertId, CommentRequest commentRequest) {
        log.info("Cоздаем комментарий к объявлению. advertId = {}", advertId);

        CommentEntity commentEntity = commentMapper.toCommentEntity(commentRequest);

        UserEntity author = currentUserService.getCurrentUser();
        
        AdvertEntity advert = advertRepository.findById(advertId)
            .orElseThrow(() -> { 
                log.warn("Объявление не найдено. advertId = {}", advertId);
                return new NotFoundException("Объявление не найдено");
            });

        if (commentRequest.getParentId() != null) {
            CommentEntity parentComment = commentRepository.findById(commentRequest.getParentId())
                .orElseThrow(() -> {
                    log.warn("Родительский комментарий не найден. parentId = {}", commentRequest.getParentId());
                    return new NotFoundException("Родительский комментарий не найден!");
                });
                if (!parentComment.getAdvert().getId().equals(advertId)) {
                    log.warn("Родительский комментарий принадлежит другому объявлению");
                    throw new ConflictException("Родительский комментарий принадлежит другому объявлению");
                }   
            
            commentEntity.setParentComment(parentComment);
        }

        commentEntity.setUser(author);
        commentEntity.setAdvert(advert);

        CommentEntity savedComment = commentRepository.save(commentEntity);
        log.info("Комментарий добавлен. commentId = {}", savedComment.getId());
        return commentMapper.toCommentResponse(savedComment);
    }

    public CommentResponse getComment(Long id) {
        log.info("Получаем комментарий. commentId = {} ", id);

        CommentEntity commentEntity = commentRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Комментарий не найден. commentId = {}", id);
                return new NotFoundException("Комментарий не найден");
        });

        return commentMapper.toCommentResponse(commentEntity);
    }

    public Page<CommentResponse> getAllComments(Long advertId, int page, int size) {
        log.info("Получаем список всех комментариев в объявлении!");

        Pageable pageable = PageRequest.of(page, size);

        Page<CommentEntity> commentPage = commentRepository.findByAdvertId(advertId, pageable);

        log.info("Распечатано комментариев на текущей странице = {} ", 
            commentPage.getNumberOfElements());

        return commentPage.map(commentMapper::toCommentResponse);
    }

    @Transactional
    public CommentResponse updateComment(Long id, CommentRequest commentRequest) {
        log.info("Изменение комментария. commentId = {}", id);

        CommentEntity commentEntity = commentRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Комментарий не найден. commentId = {}", id);
                return new NotFoundException("Комментарий не найден");
        });

        if (commentRequest.getParentId() != null && commentRequest.getParentId().equals(id)) {
            log.warn("Комментарий не может быть родителем самого себя", commentRequest.getParentId());
            throw new ConflictException("Комментарий не может быть родителем самого себя");
        }

        if (commentRequest.getParentId() != null) {
            CommentEntity parentComment = commentRepository.findById(commentRequest.getParentId())
                .orElseThrow(() -> {
                    log.warn("Родительский комментарий не найден. parentId = {}", 
                        commentRequest.getParentId());
                    return new NotFoundException("Родительский комментарий не найден");
                });
            if (!parentComment.getAdvert().getId().equals(commentEntity.getAdvert().getId())) {
                    throw new ConflictException("Родительский комментарий принадлежит другому объявлению");
            }   
            commentEntity.setParentComment(parentComment);
        } else {
            commentEntity.setParentComment(null);
        }

        commentMapper.updateCommentFromDto(commentRequest, commentEntity);
        log.info("Комментарий обновлен. commentId = {}", commentEntity.getId());
        return commentMapper.toCommentResponse(commentEntity);
    }

    @Transactional
    public void deleteComment(Long id) {
        log.info("Удаляем комментарий. id = {}", id);

       CommentEntity commentEntity = commentRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Комментарий не найден. commentId = {}", id);
                return new NotFoundException("Комментарий не найден");
        });

        commentRepository.delete(commentEntity);
    }
}
