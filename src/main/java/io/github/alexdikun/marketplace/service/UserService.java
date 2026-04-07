package io.github.alexdikun.marketplace.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.alexdikun.marketplace.entities.UserEntity;
import io.github.alexdikun.marketplace.exceptions.NotFoundException;
import io.github.alexdikun.marketplace.mapper.UserMapper;
import io.github.alexdikun.marketplace.repository.UserRepository;
import io.github.alexdikun.marketplace.request.UserRequest;
import io.github.alexdikun.marketplace.response.UserResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    public List<UserResponse> getAllUsers() {
        System.out.println("Получаем список всех пользователей!");

        List<UserEntity> allUsers = userRepository.findAll();
        return userMapper.toListUserResponse(allUsers);
    } 
    
    public UserResponse getUser(Long id) {
        System.out.println("Получаем пользователя по id: " + id);

        UserEntity userEntity = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return userMapper.toUserResponse(userEntity);
    }

}
