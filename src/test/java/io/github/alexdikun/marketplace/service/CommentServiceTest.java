package io.github.alexdikun.marketplace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import io.github.alexdikun.marketplace.entities.AdvertEntity;
import io.github.alexdikun.marketplace.entities.CategoryEntity;
import io.github.alexdikun.marketplace.entities.CommentEntity;
import io.github.alexdikun.marketplace.entities.UserEntity;
import io.github.alexdikun.marketplace.exceptions.ConflictException;
import io.github.alexdikun.marketplace.exceptions.NotFoundException;
import io.github.alexdikun.marketplace.mapper.CommentMapper;
import io.github.alexdikun.marketplace.repository.AdvertRepository;
import io.github.alexdikun.marketplace.repository.CommentRepository;
import io.github.alexdikun.marketplace.request.CommentRequest;
import io.github.alexdikun.marketplace.response.CommentResponse;
import io.github.alexdikun.marketplace.utils.TestFactoryData;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    
        @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private AdvertRepository advertRepository;

    @InjectMocks
    private CommentService commentService;

    private UserEntity currentUser;
    private CategoryEntity category;
    private AdvertEntity advert;
    private CommentEntity comment;
    private CommentRequest commentRequest;

    @BeforeEach
    void setUp() {
        currentUser = TestFactoryData.createUser();
        category = TestFactoryData.createCategory(null);
        advert = TestFactoryData.createAdvert(currentUser, null);
        comment = TestFactoryData.createComment(currentUser, advert, null);
        commentRequest = TestFactoryData.createCommentRequest(null);
    }

    @Test
    void createCommentShouldCreateSuccessfully() {
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setContent(commentRequest.getContent());

        when(commentMapper.toCommentEntity(commentRequest)).thenReturn(commentEntity);
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(advertRepository.findById(advert.getId())).thenReturn(Optional.of(advert));
        when(commentRepository.save(any(CommentEntity.class))).thenAnswer(invocation -> {
            CommentEntity saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(commentMapper.toCommentResponse(any(CommentEntity.class)))
            .thenReturn(CommentResponse.builder().id(1L).content(commentRequest.getContent()).build());

        CommentResponse response = commentService.createComment(advert.getId(), commentRequest);

        verify(commentMapper).toCommentEntity(commentRequest);
        verify(currentUserService).getCurrentUser();
        verify(advertRepository).findById(advert.getId());
        verify(commentRepository).save(any(CommentEntity.class));
        verify(commentMapper).toCommentResponse(any(CommentEntity.class));

        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo(commentRequest.getContent());
        assertThat(response.getId()).isEqualTo(1L);
    }


    @Test
    void createCommentShouldThrowNotFoundWhenAdvertNotFound() {
        Long advertId = 999L;
        when(advertRepository.findById(advertId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            commentService.createComment(advertId, commentRequest);
        });

        assertThat(exception.getMessage()).isEqualTo("Объявление не найдено");
        verify(advertRepository).findById(advertId);   
    }


    @Test
    void createCommentShouldThrowNotFoundWhenParentCommentNotFound() {
        Long advertId = 1L;
        commentRequest.setParentId(999L);

        when(commentMapper.toCommentEntity(commentRequest)).thenReturn(new CommentEntity());
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(advertRepository.findById(advertId)).thenReturn(Optional.of(advert));
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            commentService.createComment(advertId, commentRequest);
        });

        assertThat(exception.getMessage()).isEqualTo("Родительский комментарий не найден!");
        verify(advertRepository).findById(advertId);
        verify(commentRepository).findById(999L);     
    }

    @Test
    void getCommentShouldReturnComment() {
        Long commentId = 1L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentMapper.toCommentResponse(comment))
            .thenReturn(CommentResponse.builder().id(commentId).content("Текст комментария").build());

        CommentResponse response = commentService.getComment(commentId);

        verify(commentRepository).findById(commentId);
        verify(commentMapper).toCommentResponse(comment);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(commentId);
    }

    @Test
    void getCommentShouldThrowNotFound() {
        Long commentId = 999L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            commentService.getComment(commentId);
        });

        assertThat(exception.getMessage()).isEqualTo("Комментарий не найден");
        verify(commentRepository).findById(commentId); 
    }

    @Test
    void getAllCommentsShouldReturnPage() {
        Long advertId = 1L;
        int page = 0;
        int size = 10;

        List<CommentEntity> comments = List.of(
            TestFactoryData.createComment(currentUser, advert, null),
            TestFactoryData.createComment(currentUser, advert, null)
        );
        Page<CommentEntity> commentPage = new PageImpl<>(comments, PageRequest.of(page, size), comments.size());

        when(commentRepository.findByAdvertId(advertId, PageRequest.of(page, size)))
            .thenReturn(commentPage);
        when(commentMapper.toCommentResponse(any(CommentEntity.class)))
            .thenAnswer(invocation -> {
                CommentEntity entity = invocation.getArgument(0);
                return CommentResponse.builder()
                    .id(entity.getId())
                    .content(entity.getContent())
                    .build();
            });

        Page<CommentResponse> result = commentService.getAllComments(advertId, page, size);

        verify(commentRepository).findByAdvertId(advertId, PageRequest.of(page, size));
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
    }


    @Test
    void updateCommentShouldUpdateSuccessfully() {
        Long commentId = 1L;
        CommentRequest updateRequest = new CommentRequest();
        updateRequest.setContent("Обновлённый текст");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentMapper.toCommentResponse(comment))
            .thenReturn(CommentResponse.builder().id(commentId).content(updateRequest.getContent()).build());

        CommentResponse response = commentService.updateComment(commentId, updateRequest);

        verify(commentRepository).findById(commentId);
        verify(commentMapper).updateCommentFromDto(updateRequest, comment);
        verify(commentMapper).toCommentResponse(comment);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo("Обновлённый текст");
    }

    @Test
    void updateCommentShouldThrowConflictWhenCommentIsParentOfItself() {
        Long commentId = 1L;
        CommentRequest request = new CommentRequest();
        request.setParentId(commentId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            commentService.updateComment(commentId, request);
        });

        assertThat(exception.getMessage()).isEqualTo("Комментарий не может быть родителем самого себя");
        verify(commentRepository).findById(commentId); 
    }

    @Test
    void deleteCommentShouldDeleteSuccessfully() {
        Long commentId = 1L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.deleteComment(commentId);

        verify(commentRepository).findById(commentId);
        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteCommentShouldThrowNotFound() {
        Long commentId = 999L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            commentService.getComment(commentId);
        });

        assertThat(exception.getMessage()).isEqualTo("Комментарий не найден");
        verify(commentRepository).findById(commentId); 
    }


}

