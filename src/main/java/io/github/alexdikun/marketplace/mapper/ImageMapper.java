package io.github.alexdikun.marketplace.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import io.github.alexdikun.marketplace.entities.ImageEntity;
import io.github.alexdikun.marketplace.response.ImageResponse;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    ImageResponse toImageResponse(ImageEntity imageEntity);

    List<ImageResponse> toImageResponseList(List<ImageEntity> imageEntities);
    
}
