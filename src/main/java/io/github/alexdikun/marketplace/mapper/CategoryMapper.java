package io.github.alexdikun.marketplace.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.github.alexdikun.marketplace.entities.CategoryEntity;
import io.github.alexdikun.marketplace.request.CategoryRequest;
import io.github.alexdikun.marketplace.response.CategoryResponse;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentCategory", ignore = true)
    CategoryEntity toCategoryEntity(CategoryRequest categoryRequest);

    @Mapping(target = "parentId", source = "parentCategory.id")
    CategoryResponse toCategoryResponse(CategoryEntity categoryEntity);

    @Mapping(target = "parentCategory", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCategoryFromDto(CategoryRequest categoryRequest, @MappingTarget CategoryEntity categoryEntity);

    List<CategoryResponse> toCategoryResponseList(List<CategoryEntity> entities);
}
