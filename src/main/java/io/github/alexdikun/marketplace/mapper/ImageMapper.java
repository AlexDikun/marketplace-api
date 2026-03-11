package io.github.alexdikun.marketplace.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.github.alexdikun.marketplace.entities.ImageEntity;
import io.github.alexdikun.marketplace.request.ImageRequest;
import io.github.alexdikun.marketplace.response.ImageResponse;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "advert", ignore = true)
    ImageEntity toImageEntity(ImageRequest imageRequest);

    ImageResponse toImageResponse(ImageEntity imageEntity);
}
