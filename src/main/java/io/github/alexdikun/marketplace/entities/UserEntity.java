package io.github.alexdikun.marketplace.entities;

import java.time.Instant;
import java.util.Map;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @Column(name = "display_name", length = 50)
    @Size(max = 50, message = "Отображаемое имя не может быть длиннее 50 символов")
    private String displayName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "messenger_links")
    private Map<String, Object> messengerLinks;

}
