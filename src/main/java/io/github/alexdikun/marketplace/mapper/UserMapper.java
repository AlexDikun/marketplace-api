package io.github.alexdikun.marketplace.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.github.alexdikun.marketplace.entities.UserEntity;
import io.github.alexdikun.marketplace.request.UserRequest;
import io.github.alexdikun.marketplace.response.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "keycloakId", ignore = true)
    @Mapping(target = "login", ignore = true)
    UserEntity toUserEntity(UserRequest userRequest);

    UserResponse toUserResponse(UserEntity userEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserRequest dto, @MappingTarget UserEntity userEntity);

    List<UserResponse> toListUserResponse(List<UserEntity> usersEntities);

}
