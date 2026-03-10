package io.github.alexdikun.marketplace.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "adverts")
@Data
public class AdvertEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    @NotBlank(message = "Заголовок не должен быть пустым")
    private String title;

    @Column(name = "cost", precision = 10, scale = 2, nullable = false)
    @DecimalMin(
        value = "0.0", 
        inclusive = true, 
        message = "Стоимость не может быть отрицательной"
    )
    private BigDecimal cost;

    @Column(name = "address", nullable = false, length = 100)
    @NotBlank(message = "Адрес не может быть пустым")
    private String address;

    @Column(name = "phone", nullable = false, length = 25)
    @NotBlank(message = "Номер телефона не может быть пустым")
    private String phone;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @OneToMany(
        mappedBy = "advert",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<ImageEntity> images = new ArrayList<>();

    @OneToMany(mappedBy = "advert", fetch = FetchType.LAZY)
    private List<CommentEntity> comments = new ArrayList<>();

}
