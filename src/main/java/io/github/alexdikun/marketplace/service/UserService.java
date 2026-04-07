package io.github.alexdikun.marketplace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.github.alexdikun.marketplace.entities.UserEntity;
import io.github.alexdikun.marketplace.exceptions.NotFoundException;
import io.github.alexdikun.marketplace.mapper.UserMapper;
import io.github.alexdikun.marketplace.repository.UserRepository;
import io.github.alexdikun.marketplace.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    public List<UserResponse> getAllUsers() {
        log.info("Получаем список всех пользователей!");

        List<UserEntity> allUsers = userRepository.findAll();
        return userMapper.toListUserResponse(allUsers);
    } 
    
    public UserResponse getUser(Long id) {
        log.info("Получаем пользователя. userId = {}", id);

        UserEntity userEntity = userRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Пользователь не найден. userId = {}", id);
                return new NotFoundException("Пользователь не найден");
            });

        return userMapper.toUserResponse(userEntity);
    }

}
