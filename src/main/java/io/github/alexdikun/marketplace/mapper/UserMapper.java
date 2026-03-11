package io.github.alexdikun.marketplace.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.github.alexdikun.marketplace.entities.UserEntity;
import io.github.alexdikun.marketplace.request.UserRequest;
import io.github.alexdikun.marketplace.response.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "password", ignore = true)
    UserEntity toEntity(UserRequest request);

    @Mapping(target = "role", source = "role.name")
    UserResponse toResponse(UserEntity entity);

}
