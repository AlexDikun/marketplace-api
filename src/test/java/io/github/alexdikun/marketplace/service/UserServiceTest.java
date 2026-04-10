package io.github.alexdikun.marketplace.service;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserShouldReturnMappedResponseWhenUserExists() {
        Long id = 6L;
        String email = "test@.ru";
        String displayName = "Glad Valakas";
        Map<String, Object> messengerLinks = new HashMap<>();
        messengerLinks.put("max", "max.ru/glad_valakas");
        messengerLinks.put("vk", "vk.com/glad_valakas");
        Instant date = Instant.now();

        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setEmail(email);
        userEntity.setDisplayName(displayName);
        userEntity.setMessengerLinks(messengerLinks);
        userEntity.setCreatedAt(date);

        UserResponse expectedUserResponse = UserResponse
            .builder()
            .email(email)
            .displayName(displayName)
            .messengerLinks(messengerLinks)
            .createdAt(date)
            .build();

        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));
        when(userMapper.toUserResponse(userEntity)).thenReturn(expectedUserResponse);

        UserResponse actual = userService.getUser(id);

        assertThat(actual).isSameAs(expectedUserResponse);
        verify(userRepository).findById(id);
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
            createUser("Джон Сноу", "john@email.com"),
            createUser("Дейенерис Таргариен", "dany@email.com")
        );

        List<UserResponse> expectedUserResponses = List.of(
            UserResponse.builder().displayName("Джон Сноу").email("john@email.com").build(),
            UserResponse.builder().displayName("Дейнерис Таргариен").email("dany@email.com").build()
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

    private UserEntity createUser(String displayName, String email) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setDisplayName(displayName);
        return userEntity;
    }

}
