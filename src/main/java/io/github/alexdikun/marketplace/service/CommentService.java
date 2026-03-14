package io.github.alexdikun.marketplace.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.alexdikun.marketplace.entities.AdvertEntity;
import io.github.alexdikun.marketplace.entities.CommentEntity;
import io.github.alexdikun.marketplace.entities.UserEntity;
import io.github.alexdikun.marketplace.mapper.CommentMapper;
import io.github.alexdikun.marketplace.repository.AdvertRepository;
import io.github.alexdikun.marketplace.repository.CommentRepository;
import io.github.alexdikun.marketplace.repository.UserRepository;
import io.github.alexdikun.marketplace.request.CommentRequest;
import io.github.alexdikun.marketplace.response.CommentResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final AdvertRepository advertRepository;

    @Transactional
    public CommentResponse createComment(Long advertId, CommentRequest commentRequest) {
        System.out.println("Cоздаем комментарий к объявлению!");

        CommentEntity commentEntity = commentMapper.toCommentEntity(commentRequest);

        UserEntity author = userRepository.findById(commentRequest.getUserId())
            .orElseThrow(() -> new RuntimeException("Автор комментария не найден"));
        
        AdvertEntity advert = advertRepository.findById(commentRequest.getAdvertId())
                .orElseThrow(() -> new RuntimeException("Объявление не найдено"));

        if (commentRequest.getParentId() != null) {
            CommentEntity parentComment = commentRepository.findById(commentRequest.getParentId())
                .orElseThrow(() -> new RuntimeException("Родительский комментарий не найден!"));
            
            commentEntity.setParentComment(parentComment);
        }

        commentEntity.setUser(author);
        commentEntity.setAdvert(advert);

        CommentEntity savedComment = commentRepository.save(commentEntity);

        return commentMapper.toCommentResponse(savedComment);
    }

    public CommentResponse getCommentById(Long id) {
        System.out.println("Получаем комментарий по id: " + id);

        CommentEntity commentEntity = commentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Комментарий не найден"));
        return commentMapper.toCommentResponse(commentEntity);
    }

    public List<CommentResponse> getAllComments(Long advertId) {
        System.out.println("Получаем список всех комментариев в объявлении!");

        List<CommentEntity> allComments = commentRepository.findByAdvertId(advertId);
        return commentMapper.toCommentResponseList(allComments);

    }

    @Transactional
    public CommentResponse updateCommentById(Long id, CommentRequest commentRequest) {
        System.out.println("Изменение комментария с id: " + id);

        CommentEntity commentEntity = commentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Комментарий не найден"));

        commentMapper.updateCommentFromDto(commentRequest, commentEntity);

        if (commentRequest.getUserId() != null) {
            UserEntity author = userRepository.findById(commentRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("Автор комментария не найден"));

            commentEntity.setUser(author);
        }
        if (commentRequest.getAdvertId() != null) {
            AdvertEntity advert = advertRepository.findById(commentRequest.getAdvertId())
                    .orElseThrow(() -> new RuntimeException("Объявление не найдено"));

            commentEntity.setAdvert(advert);
        }

        if (commentRequest.getParentId() != null && commentRequest.getParentId().equals(id)) {
            throw new RuntimeException("Комментарий не может быть родителем самого себя");
        }

        if (commentRequest.getParentId() != null) {
            CommentEntity parentComment = commentRepository.findById(commentRequest.getParentId())
                .orElseThrow(() -> new RuntimeException("Родительский комментарий не найден"));
            
            commentEntity.setParentComment(parentComment);
        } else {
            commentEntity.setParentComment(null);
        }

        return commentMapper.toCommentResponse(commentEntity);

    }

    @Transactional
    public void deleteCommentById(Long id) {
        System.out.println("Удаляем комментарий с id: " + id);

        CommentEntity commentEntity = commentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Комментарий не найден"));
        commentRepository.delete(commentEntity);
    }
}
