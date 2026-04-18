package io.github.alexdikun.marketplace.service;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.alexdikun.marketplace.entities.UserEntity;
import io.github.alexdikun.marketplace.exceptions.NotFoundException;
import io.github.alexdikun.marketplace.mapper.UserMapper;
import io.github.alexdikun.marketplace.repository.UserRepository;
import io.github.alexdikun.marketplace.response.UserResponse;
import io.github.alexdikun.marketplace.utils.TestFactoryData;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = TestFactoryData.createUser();
    }

    @Test
    void getUserShouldReturnMappedResponseWhenUserExists() {
        UserResponse expectedUserResponse = UserResponse
            .builder()
            .email(userEntity.getEmail())
            .displayName(userEntity.getDisplayName())
            .messengerLinks(userEntity.getMessengerLinks())
            .createdAt(userEntity.getCreatedAt())
            .build();

        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
        when(userMapper.toUserResponse(userEntity)).thenReturn(expectedUserResponse);

        UserResponse actual = userService.getUser(userEntity.getId());

        assertThat(actual).isSameAs(expectedUserResponse);
        verify(userRepository).findById(userEntity.getId());
        verify(userMapper).toUserResponse(userEntity);
    }

    @Test
    void getUserShouldReturnExceptionWhenUserDoesNotExists() {
        Long id = 27L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.getUser(id);
        });

        assertThat(exception.getMessage()).isEqualTo("Пользователь не найден");
        verify(userRepository).findById(id);
        verify(userMapper, never()).toUserResponse(org.mockito.ArgumentMatchers.any(UserEntity.class));
    }

    @Test
    void getAllUsersShouldReturnListOfUserResponses() {
        List<UserEntity> userEntities = List.of(
            TestFactoryData.createUser(),
            TestFactoryData.createUser()
        );

        List<UserResponse> expectedUserResponses = List.of(
            UserResponse.builder().displayName(userEntities.get(0).getDisplayName())
                .email(userEntities.get(0).getEmail()).build(),
            UserResponse.builder().displayName(userEntities.get(1).getDisplayName())
                .email(userEntities.get(1).getEmail()).build()
        );

        when(userRepository.findAll()).thenReturn(userEntities);
        when(userMapper.toListUserResponse(userEntities)).thenReturn(expectedUserResponses);

        List<UserResponse> actualUserResponses = userService.getAllUsers();

        assertThat(actualUserResponses).isEqualTo(expectedUserResponses);
        assertThat(actualUserResponses).hasSize(2);

        verify(userRepository).findAll();
        verify(userMapper).toListUserResponse(userEntities);
    }

    @Test
    void getAllUsersShouldReturnEmptyListWhenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(List.of());
        when(userMapper.toListUserResponse(List.of())).thenReturn(List.of());

        List<UserResponse> actual = userService.getAllUsers();

        assertThat(actual).isEmpty();
        verify(userRepository).findAll();
        verify(userMapper).toListUserResponse(List.of());
    }

}
