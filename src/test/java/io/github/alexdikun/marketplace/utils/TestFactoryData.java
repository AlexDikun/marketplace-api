package io.github.alexdikun.marketplace.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.github.alexdikun.marketplace.entities.AdvertEntity;
import io.github.alexdikun.marketplace.entities.CategoryEntity;
import io.github.alexdikun.marketplace.entities.CommentEntity;
import io.github.alexdikun.marketplace.entities.ImageEntity;
import io.github.alexdikun.marketplace.entities.UserEntity;
import io.github.alexdikun.marketplace.request.AdvertRequest;
import io.github.alexdikun.marketplace.request.CategoryRequest;
import io.github.alexdikun.marketplace.request.CommentRequest;
import io.github.alexdikun.marketplace.request.UserRequest;
import net.datafaker.Faker;

public class TestFactoryData {

    private static final Faker faker = new Faker(new java.util.Locale("ru"));

    public static UserEntity createUser() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(faker.number().numberBetween(1L, Long.MAX_VALUE));
        userEntity.setKeycloakId(faker.idNumber().valid());
        userEntity.setEmail(faker.internet().emailAddress());
        userEntity.setLogin(faker.internet().username());
        userEntity.setCreatedAt(faker.date().past(365, TimeUnit.DAYS).toInstant());
        userEntity.setDisplayName(faker.name().fullName());

        Map<String, Object> messengerLinks = new HashMap<>();
        messengerLinks.put("telegram", faker.internet().url());
        messengerLinks.put("max", faker.phoneNumber().phoneNumber());

        return userEntity;
    }

    public static CategoryEntity createCategory(CategoryEntity parentCategory) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(faker.number().numberBetween(1L, Long.MAX_VALUE));
        categoryEntity.setName(faker.commerce().department());
        categoryEntity.setParentCategory(parentCategory);

        return categoryEntity;
    }

    public static AdvertEntity createAdvert(UserEntity userEntity, CategoryEntity categoryEntity) {
        AdvertEntity advertEntity = new AdvertEntity();
        advertEntity.setId(faker.number().numberBetween(1L, Long.MAX_VALUE));
        advertEntity.setTitle(faker.commerce().productName());
        advertEntity.setCost(BigDecimal.valueOf(
            faker.number().randomDouble(2, 0L, 1000000L)));

        advertEntity.setDescription(faker.lorem().paragraph(3)); 
        advertEntity.setAddress(faker.address().fullAddress());
        advertEntity.setPhone(faker.phoneNumber().phoneNumber());
        advertEntity.setUser(userEntity);
        advertEntity.setCategory(categoryEntity);
        advertEntity.setCreatedAt(faker.date().past(365, TimeUnit.DAYS).toInstant());
        return advertEntity;
    }

    public static CommentEntity createComment(
        UserEntity userEntity, 
        AdvertEntity advertEntity, 
        CommentEntity parentComment
    ) {
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setId(faker.number().numberBetween(1L, Long.MAX_VALUE));
        commentEntity.setContent(faker.lorem().sentence(3));
        commentEntity.setParentComment(parentComment);
        commentEntity.setUser(userEntity);
        commentEntity.setAdvert(advertEntity);
        commentEntity.setCreatedAt(faker.date().past(365, TimeUnit.DAYS).toInstant());

        return commentEntity;
    }

    public static ImageEntity createImage(AdvertEntity advertEntity, boolean localStorage) {
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setId(faker.number().numberBetween(1L, Long.MAX_VALUE));
        imageEntity.setAdvert(advertEntity);
        imageEntity.setUploadedAt(faker.date().past(365, TimeUnit.DAYS).toInstant());
        if (localStorage == true ) {
            imageEntity.setUrl("/avito.png");
        } else {
            imageEntity.setUrl(faker.internet().url() + faker.commerce().productName() + ".png");
        }

        return imageEntity;
    }

    public static AdvertRequest createAdvertRequest(CategoryEntity category) {
        AdvertRequest advertRequest = new AdvertRequest();
        advertRequest.setTitle(faker.commerce().productName());
        advertRequest.setCost(BigDecimal.valueOf(
            faker.number().randomDouble(2, 0L, 1000000L)));
        advertRequest.setAddress(faker.address().fullAddress());
        advertRequest.setPhone(faker.phoneNumber().phoneNumber());
        advertRequest.setDescription(faker.lorem().paragraph(3)); 
        advertRequest.setCategoryId(category.getId());

        return advertRequest;
    }
    
    public static CategoryRequest createCategoryRequest(CategoryEntity parentCategory) {
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName(faker.commerce().department());
        if (parentCategory != null) categoryRequest.setParentId(parentCategory.getId());
        
        return categoryRequest;
    }

    public static CommentRequest createCommentRequest(CommentEntity parentComment) {
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent(faker.lorem().sentence(3));
        if (parentComment != null) commentRequest.setParentId(parentComment.getId());

        return commentRequest;
    }

    public static UserRequest createUserRequest() {
        UserRequest userRequest = new UserRequest();
        userRequest.setDisplayName(faker.name().fullName());

        Map<String, Object> messengerLinks = new HashMap<>();
        messengerLinks.put("telegram", faker.internet().url());
        messengerLinks.put("max", faker.phoneNumber().phoneNumber());
        userRequest.setMessengerLinks(messengerLinks);

        return userRequest;
    }

}
