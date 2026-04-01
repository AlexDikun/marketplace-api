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

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CurrentUserService currentUserService;
    private final AdvertRepository advertRepository;

    @Transactional
    public CommentResponse createComment(Long advertId, CommentRequest commentRequest) {
        System.out.println("Cоздаем комментарий к объявлению!");

        CommentEntity commentEntity = commentMapper.toCommentEntity(commentRequest);

        UserEntity author = currentUserService.getCurrentUser();
        
        AdvertEntity advert = advertRepository.findById(advertId)
                .orElseThrow(() -> new NotFoundException("Объявление не найдено"));

        if (commentRequest.getParentId() != null) {
            CommentEntity parentComment = commentRepository.findById(commentRequest.getParentId())
                .orElseThrow(() -> new NotFoundException("Родительский комментарий не найден!"));
                if (!parentComment.getAdvert().getId().equals(advertId)) {
                    throw new ConflictException("Родительский комментарий принадлежит другому объявлению");
                }   
            
            commentEntity.setParentComment(parentComment);
        }


        commentEntity.setUser(author);
        commentEntity.setAdvert(advert);

        CommentEntity savedComment = commentRepository.save(commentEntity);

        return commentMapper.toCommentResponse(savedComment);
    }

    public CommentResponse getComment(Long id) {
        System.out.println("Получаем комментарий по id: " + id);

        CommentEntity commentEntity = commentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        return commentMapper.toCommentResponse(commentEntity);
    }

    public Page<CommentResponse> getAllComments(Long advertId, int page, int size) {
        System.out.println("Получаем список всех комментариев в объявлении!");

        Pageable pageable = PageRequest.of(page, size);

        Page<CommentEntity> commentPage = commentRepository.findByAdvertId(advertId, pageable);
        return commentPage.map(commentMapper::toCommentResponse);
    }

    @Transactional
    public CommentResponse updateComment(Long id, CommentRequest commentRequest) {
        System.out.println("Изменение комментария с id: " + id);

        CommentEntity commentEntity = commentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        if (commentRequest.getParentId() != null && commentRequest.getParentId().equals(id)) {
            throw new ConflictException("Комментарий не может быть родителем самого себя");
        }

        if (commentRequest.getParentId() != null) {
            CommentEntity parentComment = commentRepository.findById(commentRequest.getParentId())
                .orElseThrow(() -> new NotFoundException("Родительский комментарий не найден"));
            if (!parentComment.getAdvert().getId().equals(commentEntity.getAdvert().getId())) {
                    throw new ConflictException("Родительский комментарий принадлежит другому объявлению");
            }   
            commentEntity.setParentComment(parentComment);
        } else {
            commentEntity.setParentComment(null);
        }

        commentMapper.updateCommentFromDto(commentRequest, commentEntity);
        return commentMapper.toCommentResponse(commentEntity);

    }

    @Transactional
    public void deleteComment(Long id) {
        System.out.println("Удаляем комментарий с id: " + id);

        CommentEntity commentEntity = commentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        commentRepository.delete(commentEntity);
    }
}
