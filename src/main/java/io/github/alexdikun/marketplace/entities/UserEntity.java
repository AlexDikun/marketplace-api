package io.github.alexdikun.marketplace.entities;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "keycloak_id", unique = true, nullable = false)
    @NotBlank(message = "Кейлок-идентификатор не должно быть пустым")
    private String keycloakId;

    @Column(name = "email", nullable = false, length = 50)
    @NotBlank(message = "Имейл не должно быть пустым")
    private String email;

    @Column(name = "login", unique = true, nullable = false, length = 16)
    @NotBlank(message = "Логин не должен быть пустым")
    private String login;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

}
