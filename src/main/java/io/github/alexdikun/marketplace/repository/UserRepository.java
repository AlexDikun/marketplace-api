package io.github.alexdikun.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.alexdikun.marketplace.entities.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByLoginAndIdNot(String login, Long id);
}
