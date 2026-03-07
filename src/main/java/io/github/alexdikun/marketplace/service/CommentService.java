package io.github.alexdikun.marketplace.service;

import java.util.UUID;
import org.springframework.stereotype.Service;

import io.github.alexdikun.marketplace.request.CommentRequest;
import io.github.alexdikun.marketplace.response.CommentResponse;

@Service
public class CommentService {

    public CommentResponse createComment(Long advertId, CommentRequest commentRequest) {
        System.out.println("Cоздаем комментарий к объявлению!");

        return CommentResponse.builder()
            .id(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE)
            .content(commentRequest.getContent())
            .userId(commentRequest.getUserId())
            .advertId(advertId)
            .build();
    }

    public CommentResponse getCommentById(Long id) {
        System.out.println("Получаем комментарий по id: " + id);

        return CommentResponse.builder()
            .id(id)
            .content("Текст комментария")
            .userId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE)
            .advertId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE)
            .build();
    }

    public CommentResponse updateCommentById(Long id, CommentRequest commentRequest) {
        System.out.println("Изменение комментария с id: " + id);

        return CommentResponse.builder()
            .id(id)
            .content(commentRequest.getContent())
            .userId(commentRequest.getUserId())
            .advertId(commentRequest.getAdvertId())
            .build();
    }

    public String deleteCommentById(Long id) {
        System.out.println("Удаляем комментарий с id: " + id);
        return "Комментарий с id:" + id + "удален!";
    }
}
