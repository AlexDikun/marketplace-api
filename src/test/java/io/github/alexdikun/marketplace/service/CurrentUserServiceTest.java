package io.github.alexdikun.marketplace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.context.SecurityContext;

import io.github.alexdikun.marketplace.entities.UserEntity;
import io.github.alexdikun.marketplace.mapper.UserMapper;
import io.github.alexdikun.marketplace.repository.UserRepository;
import io.github.alexdikun.marketplace.request.UserRequest;
import io.github.alexdikun.marketplace.response.UserResponse;
import io.github.alexdikun.marketplace.utils.TestFactoryData;

@ExtendWith(MockitoExtension.class)
class CurrentUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private CurrentUserService currentUserService;

    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserShouldReturnExistingUser() {
        UserEntity existingUser = TestFactoryData.createUser();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);

        when(jwt.getSubject()).thenReturn("id-123");
        when(jwt.getClaim("preferred_username")).thenReturn("user");
        when(jwt.getClaim("email")).thenReturn("user@test.com");

        when(userRepository.findByKeycloakId("id-123"))
            .thenReturn(Optional.of(existingUser));

        UserEntity result = currentUserService.getCurrentUser();

        assertThat(result).isEqualTo(existingUser);
        verify(userRepository, never()).save(any());
    }

    @Test
    void getCurrentUserShouldCreateUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);

        when(jwt.getSubject()).thenReturn("id-456");
        when(jwt.getClaim("preferred_username")).thenReturn("newuser");
        when(jwt.getClaim("email")).thenReturn("new@test.com");

        when(userRepository.findByKeycloakId("id-456"))
            .thenReturn(Optional.empty());

        when(userRepository.save(any(UserEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        UserEntity result = currentUserService.getCurrentUser();

        assertThat(result.getKeycloakId()).isEqualTo("id-456");
        assertThat(result.getLogin()).isEqualTo("newuser");
        assertThat(result.getEmail()).isEqualTo("new@test.com");
    }

    @Test
    void getCurrentUserShouldThrowWhenNoAuth() {
        when(securityContext.getAuthentication()).thenReturn(null);

        AuthenticationCredentialsNotFoundException exception = assertThrows(
            AuthenticationCredentialsNotFoundException.class, () -> {
                currentUserService.getCurrentUser();
        });

        assertThat(exception.getMessage()).isEqualTo("Пользователь не авторизирован!");
    }

    @Test
    void getRolesShouldReturnUserRole() {
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        doReturn(authorities).when(authentication).getAuthorities();

        List<String> roles = currentUserService.getRoles(authentication);

        assertThat(roles).containsExactly("ROLE_USER");
    }

    @Test
    void getRolesShouldReturnEmptyList() {
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        doReturn(authorities).when(authentication).getAuthorities();

        List<String> roles = currentUserService.getRoles(authentication);

        assertThat(roles).isEmpty();
    }

    @Test
    void getRolesShouldThrowWhenNull() {
        AuthenticationCredentialsNotFoundException exception = assertThrows(
            AuthenticationCredentialsNotFoundException.class, () -> {
                currentUserService.getRoles(null);
        });

        assertThat(exception.getMessage()).isEqualTo("Пользователь не авторизирован!");
    }

    @Test
    void updateUserShouldUpdateSuccessfully() {
        UserRequest request = TestFactoryData.createUserRequest();

        UserEntity user = TestFactoryData.createUser();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);

        when(jwt.getSubject()).thenReturn(user.getKeycloakId());
        when(jwt.getClaim("preferred_username")).thenReturn(user.getLogin());
        when(jwt.getClaim("email")).thenReturn(user.getEmail());

        when(userRepository.findByKeycloakId(user.getKeycloakId()))
            .thenReturn(Optional.of(user));

        UserResponse response = UserResponse.builder().build();
        when(userMapper.toUserResponse(user)).thenReturn(response);

        UserResponse result = currentUserService.updateUser(request);

        verify(userMapper).updateUserFromDto(request, user);
        assertThat(result).isEqualTo(response);
    }

    @Test
    void updateUserShouldThrowWhenUnauthenticated() {
        UserRequest updateUserRequest = TestFactoryData.createUserRequest();

        AuthenticationCredentialsNotFoundException exception = assertThrows(
            AuthenticationCredentialsNotFoundException.class, () -> {
                currentUserService.updateUser(updateUserRequest);
        });

        assertThat(exception.getMessage()).isEqualTo("Пользователь не авторизирован!");
    }

    @Test
    void deleteUserShouldDeleteSuccessfully() {
        UserEntity user = TestFactoryData.createUser();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);

        when(jwt.getSubject()).thenReturn(user.getKeycloakId());
        when(jwt.getClaim("preferred_username")).thenReturn(user.getLogin());
        when(jwt.getClaim("email")).thenReturn(user.getEmail());

        when(userRepository.findByKeycloakId(user.getKeycloakId()))
            .thenReturn(Optional.of(user));

        currentUserService.deleteUser();

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUserShouldThrowWhenUnauthenticated() {
        AuthenticationCredentialsNotFoundException exception = assertThrows(
            AuthenticationCredentialsNotFoundException.class, () -> {
                currentUserService.deleteUser();
        });

        assertThat(exception.getMessage()).isEqualTo("Пользователь не авторизирован!");
    }

}
