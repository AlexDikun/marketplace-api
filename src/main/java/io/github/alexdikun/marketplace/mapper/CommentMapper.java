package io.github.alexdikun.marketplace.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.github.alexdikun.marketplace.entities.CommentEntity;
import io.github.alexdikun.marketplace.request.CommentRequest;
import io.github.alexdikun.marketplace.response.CommentResponse;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentComment", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "advert", ignore = true)
    CommentEntity toCommentEntity(CommentRequest commentRequest);

    @Mapping(target = "parentId", source = "parentComment.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "advertId", source = "advert.id")
    CommentResponse toCommentResponse(CommentEntity commentEntity);

    List<CommentResponse> toCommentResponseList(List<CommentEntity> entities);
}
