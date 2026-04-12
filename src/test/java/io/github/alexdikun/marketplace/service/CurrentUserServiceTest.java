package io.github.alexdikun.marketplace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
public class CurrentUserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private CurrentUserService currentUserService;

    private SecurityContext securityContext;
    private Authentication authentication;
    private Jwt jwt;
    private UserEntity existingUser;

    @BeforeEach
    void setUp() {
        securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);
        jwt = mock(Jwt.class);
        existingUser = TestFactoryData.createUser();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void getCurrentUserShouldReturnExistingUser() {
        when(jwt.getSubject()).thenReturn("keycloak-123");
        when(jwt.getClaim("preferred_username")).thenReturn("testuser");
        when(jwt.getClaim("email")).thenReturn("user@test.com");

        when(userRepository.findByKeycloakId("keycloak-123"))
            .thenReturn(Optional.of(existingUser));

        UserEntity result = currentUserService.getCurrentUser();

        verify(userRepository).findByKeycloakId("keycloak-123");
        verify(userRepository, never()).save(any(UserEntity.class));
        assertThat(result).isEqualTo(existingUser);
    }

    @Test
    void getCurrentUserShouldCreateNewUser() {
        String keycloakId = "keycloak-456";
        String login = "newuser";
        String email = "new@test.com";

        when(jwt.getSubject()).thenReturn(keycloakId);
        when(jwt.getClaim("preferred_username")).thenReturn(login);
        when(jwt.getClaim("email")).thenReturn(email);

        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.empty());

        UserEntity savedUser = new UserEntity();
        savedUser.setId(1L);
        savedUser.setKeycloakId(keycloakId);
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        UserEntity result = currentUserService.getCurrentUser();

        verify(userRepository).findByKeycloakId(keycloakId);
        verify(userRepository).save(any(UserEntity.class));

        assertThat(result.getKeycloakId()).isEqualTo(keycloakId);
        assertThat(result.getLogin()).isEqualTo(login);
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getCurrentUserShouldThrowWhenUnauthenticated() {
        when(securityContext.getAuthentication()).thenReturn(null);

        assertThatThrownBy(() -> currentUserService.getCurrentUser())
            .isInstanceOf(AuthenticationCredentialsNotFoundException.class)
            .hasMessage("Пользователь не авторизирован!");
    }

    @Test
    void getRolesShouldReturnUserRole() {
        Collection<? extends GrantedAuthority> authorities = List.<GrantedAuthority>of( 
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        when(authentication.getAuthorities()).thenReturn(any());

        List<String> roles = currentUserService.getRoles(authentication);

        verify(authentication).getAuthorities();
        assertThat(roles).containsExactly("ROLE_USER");
    }

    @Test
    void getRolesShouldReturnEmptyListWhenNoUserRole() {
        Collection<? extends GrantedAuthority> authorities = List.<GrantedAuthority>of(
            new SimpleGrantedAuthority("ROLE_ADMIN"),
            new SimpleGrantedAuthority("ROLE_MODERATOR")
        );

        
        when(authentication.getAuthorities()).thenReturn(any());

        List<String> roles = currentUserService.getRoles(authentication);

        assertThat(roles).isEmpty();
    }

    @Test
    void getRolesShouldThrowWhenUnauthenticated() {
        assertThatThrownBy(() -> currentUserService.getRoles(null))
            .isInstanceOf(AuthenticationCredentialsNotFoundException.class)
            .hasMessage("Пользователь не авторизирован!");
    }

    @Test
    void updateUserShouldUpdateSuccessfully() {
        UserRequest userRequest = new UserRequest();
        userRequest.setDisplayName("Новое имя");

        UserEntity currentUser = TestFactoryData.createUser();
        currentUser.setId(1L);

        UserResponse expectedResponse = UserResponse.builder()
            .displayName("Новое имя")
            .build();

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(userMapper.toUserResponse(currentUser)).thenReturn(expectedResponse);

        UserResponse response = currentUserService.updateUser(userRequest);

        verify(userMapper).updateUserFromDto(userRequest, currentUser);
        verify(userMapper).toUserResponse(currentUser);
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void deleteUserShouldDeleteSuccessfully() {
        UserEntity userToDelete = TestFactoryData.createUser();
        userToDelete.setId(1L);
        when(currentUserService.getCurrentUser()).thenReturn(userToDelete);

        currentUserService.deleteUser();

        verify(currentUserService).getCurrentUser();
        verify(userRepository).delete(userToDelete);
    }

    @Test
    void deleteUserShouldThrowWhenUnauthenticated() {
        when(currentUserService.getCurrentUser()).thenThrow(
            new AuthenticationCredentialsNotFoundException("Пользователь не авторизирован!")
        );

        assertThatThrownBy(() -> currentUserService.deleteUser())
            .isInstanceOf(AuthenticationCredentialsNotFoundException.class)
            .hasMessage("Пользователь не авторизирован!");
    }

}
