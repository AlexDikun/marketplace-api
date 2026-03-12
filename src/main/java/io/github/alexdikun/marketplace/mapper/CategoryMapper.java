package io.github.alexdikun.marketplace.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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

    List<CategoryResponse> toCategoryResponseList(List<CategoryEntity> entities);
}
