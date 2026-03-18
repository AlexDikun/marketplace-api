package io.github.alexdikun.marketplace.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.github.alexdikun.marketplace.entities.AdvertEntity;
import io.github.alexdikun.marketplace.request.AdvertRequest;
import io.github.alexdikun.marketplace.response.AdvertResponse;
    
@Mapper(componentModel = "spring")
public interface AdvertMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    AdvertEntity toAdvertEntity(AdvertRequest advertRequest);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "images", source = "images")
    AdvertResponse toAdvertResponse(AdvertEntity advertEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAdvertFromDto(AdvertRequest advertRequest, @MappingTarget AdvertEntity advertEntity);

    List<AdvertResponse> toListAdvertResponse(List<AdvertEntity> advertEntities);

}
