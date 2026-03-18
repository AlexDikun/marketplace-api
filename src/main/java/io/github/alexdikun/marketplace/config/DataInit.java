package io.github.alexdikun.marketplace.config;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.alexdikun.marketplace.entities.AdvertEntity;
import io.github.alexdikun.marketplace.entities.CategoryEntity;
import io.github.alexdikun.marketplace.entities.CommentEntity;
import io.github.alexdikun.marketplace.entities.ImageEntity;
import io.github.alexdikun.marketplace.entities.RoleEntity;
import io.github.alexdikun.marketplace.entities.UserEntity;
import io.github.alexdikun.marketplace.enums.Role;
import io.github.alexdikun.marketplace.repository.AdvertRepository;
import io.github.alexdikun.marketplace.repository.CategoryRepository;
import io.github.alexdikun.marketplace.repository.CommentRepository;
import io.github.alexdikun.marketplace.repository.ImageRepository;
import io.github.alexdikun.marketplace.repository.RoleRepository;
import io.github.alexdikun.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInit implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AdvertRepository advertRepository;
    private final CommentRepository commentRepository;
    private final ImageRepository imageRepository;

    @Override
    @Transactional
    public void run(String... args) {

        if (userRepository.count() > 0) return;

        RoleEntity adminRole = roleRepository.save(new RoleEntity(Role.ROLE_ADMIN));
        RoleEntity userRole = roleRepository.save(new RoleEntity(Role.ROLE_USER));

        CategoryEntity parent = categoryRepository.save(
            new CategoryEntity("Parent", null)
        );

        List<CategoryEntity> subCategories = IntStream.rangeClosed(1, 2)
            .mapToObj(i -> new CategoryEntity("Sub " + i, parent))
            .map(categoryRepository::save)
            .toList();

        UserEntity admin = userRepository.save(
            createUser("ADMIN", "ADMIN", "12345678", adminRole)
        );

        List<UserEntity> users = IntStream.rangeClosed(1, 2)
            .mapToObj(i -> createUser("user" + i, "User " + i, "12345678", userRole))
            .map(userRepository::save)
            .toList();

        List<AdvertEntity> adverts = IntStream.rangeClosed(1, 10)
            .mapToObj(i -> {
                AdvertEntity advert = new AdvertEntity();
                advert.setTitle("Title " + i);
                advert.setAddress("Address " + i);
                advert.setCost(BigDecimal.valueOf(i));
                advert.setDescription("Description " + i);
                advert.setPhone(String.format("%08d", i));

                advert.setUser(users.get(i % users.size()));
                advert.setCategory(subCategories.get(i % subCategories.size()));

                return advertRepository.save(advert);
            })
            .toList();

        adverts.forEach(advert -> {
            ImageEntity image = new ImageEntity();
            image.setAdvert(advert);
            image.setUrl("/uploads/test.webp");
            imageRepository.save(image);
        });

        adverts.stream().limit(3).forEach(advert -> {
            CommentEntity comment = new CommentEntity();
            comment.setAdvert(advert);
            comment.setUser(users.get(0));
            comment.setContent("Test comment");
            commentRepository.save(comment);
        });
    }

    private UserEntity createUser(String login, String name, String password, RoleEntity role) {
        UserEntity userEntity = new UserEntity();
        userEntity.setLogin(login);
        userEntity.setName(name);
        userEntity.setPassword(password);
        userEntity.setRole(role);
        return userEntity;
    }
}
