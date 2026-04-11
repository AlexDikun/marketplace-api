package io.github.alexdikun.marketplace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.alexdikun.marketplace.entities.AdvertEntity;
import io.github.alexdikun.marketplace.entities.CategoryEntity;
import io.github.alexdikun.marketplace.entities.UserEntity;
import io.github.alexdikun.marketplace.mapper.AdvertMapper;
import io.github.alexdikun.marketplace.repository.AdvertRepository;
import io.github.alexdikun.marketplace.repository.CategoryRepository;
import io.github.alexdikun.marketplace.request.AdvertRequest;
import io.github.alexdikun.marketplace.response.AdvertResponse;

@ExtendWith(MockitoExtension.class)
public class AdvertServiceTest {

    @Mock
    private AdvertRepository advertRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AdvertMapper advertMapper;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private AdvertService advertService;

    private final Long USER_ID = 1L;
    private final Long ADVERT_ID = 100L;
    private final Long CATEGORY_ID = 50L;

    @Test
    void createAdvertShouldCreateNewAdvertSuccessfully() {}

    @Test
    void createAdvertShouldThrowNotFoundExceptionWhenUserNotFound() {}

    @Test
    void createAdvertShouldThrowNotFoundExceptionWhenCategoryNotFound() {}
        
    @Test
    void searchAdvertsShouldReturnPageOfAdvertResponses() {}

    @Test
    void getAdvertShouldReturnAdvertResponseWhenAdvertExists() {}

    @Test
    void getAdvertShouldThrowNotFoundExceptionWhenAdvertNotFound() {}

    @Test
    void updateAdvertShouldUpdateExistingAdvert() {}

    @Test
    void updateAdvertShouldThrowNotFoundExceptionWhenAdvertNotFound() {}

    @Test
    void deleteAdvertShouldUpdateExistingAdvert() {}

    @Test
    void deleteAdvertShouldThrowNotFoundExceptionWhenAdvertNotFound() {}


    
}
