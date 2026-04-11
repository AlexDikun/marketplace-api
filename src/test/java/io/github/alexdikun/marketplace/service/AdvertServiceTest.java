package io.github.alexdikun.marketplace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.github.alexdikun.marketplace.entities.AdvertEntity;
import io.github.alexdikun.marketplace.entities.CategoryEntity;
import io.github.alexdikun.marketplace.entities.UserEntity;
import io.github.alexdikun.marketplace.exceptions.NotFoundException;
import io.github.alexdikun.marketplace.mapper.AdvertMapper;
import io.github.alexdikun.marketplace.repository.AdvertRepository;
import io.github.alexdikun.marketplace.repository.CategoryRepository;
import io.github.alexdikun.marketplace.request.AdvertRequest;
import io.github.alexdikun.marketplace.response.AdvertResponse;
import io.github.alexdikun.marketplace.utils.TestFactoryData;

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

    private UserEntity currentUser;
    private CategoryEntity category;
    private AdvertEntity advert;
    private AdvertRequest advertRequest;

    @BeforeEach
    void setUp() {
        currentUser = TestFactoryData.createUser();
        category = TestFactoryData.createCategory(null);
        advert = TestFactoryData.createAdvert(currentUser, category);
        advertRequest = TestFactoryData.createAdvertRequest(category);
    }

    @Test
    void createAdvertShouldCreateNewAdvertSuccessfully() {
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(categoryRepository.findById(advertRequest.getCategoryId()))
            .thenReturn(Optional.of(category));

        when(advertRepository.save(any(AdvertEntity.class))).thenAnswer(invocation -> {
            AdvertEntity saved = invocation.getArgument(0);
            saved.setId(1L); 
            return saved;
        });

        doAnswer(invocation -> {
            AdvertRequest request = invocation.getArgument(0);
            AdvertEntity entity = new AdvertEntity();
            entity.setTitle(request.getTitle());
            entity.setCost(request.getCost());
            entity.setAddress(request.getAddress());
            entity.setPhone(request.getPhone());
            entity.setDescription(request.getDescription());
            entity.setUser(currentUser);
            entity.setCategory(category);
            return entity;
        }).when(advertMapper).toAdvertEntity(any(AdvertRequest.class));

        when(advertMapper.toAdvertResponse(any(AdvertEntity.class)))
            .thenReturn(AdvertResponse.builder()
                .title(advertRequest.getTitle())
                .build());

        AdvertResponse advertResponse = advertService.createAdvert(advertRequest);

        verify(categoryRepository).findById(advertRequest.getCategoryId());
        verify(advertRepository).save(any(AdvertEntity.class)); 
        verify(advertMapper).toAdvertEntity(any(AdvertRequest.class)); 
        verify(advertMapper).toAdvertResponse(any(AdvertEntity.class));

        assertThat(advertResponse).isNotNull();
        assertThat(advertResponse.getTitle()).isEqualTo(advertRequest.getTitle());
    }

    @Test
    void createAdvertShouldThrowNotFoundExceptionWhenCategoryNotFound() {
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            advertService.createAdvert(advertRequest);
        });

        assertThat(exception.getMessage()).isEqualTo("Категория не найдена");
    }
        
    @Test
    void searchAdvertsShouldReturnPageOfAdvertResponses() {
        String query = "test";
        int page = 0;
        int size = 5;

        List<AdvertEntity> adverts = List.of(
            TestFactoryData.createAdvert(currentUser, category),
            TestFactoryData.createAdvert(currentUser, category)
        );

        Page<AdvertEntity> advertPage =
            new PageImpl<>(adverts, PageRequest.of(page, size), adverts.size());

        when(advertRepository.search(eq(query), any(Pageable.class)))
            .thenReturn(advertPage);

        when(advertMapper.toAdvertResponse(any()))
            .thenAnswer(invocation -> {
                AdvertEntity entity = invocation.getArgument(0);
                AdvertResponse response = AdvertResponse.builder().phone(entity.getPhone()).build();
                return response;
            });

        Page<AdvertResponse> result =
            advertService.searchAdverts(query, page, size);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);

        verify(advertRepository).search(eq(query), any(Pageable.class));
        verify(advertMapper, times(2)).toAdvertResponse(any());
    }

    @Test
    void getAdvertShouldReturnAdvertResponseWhenAdvertFound() {
        when(advertRepository.findWithDetailsById(anyLong())).thenReturn(Optional.of(advert));
        when(advertMapper.toAdvertResponse(advert))
            .thenReturn(AdvertResponse.builder().phone(advert.getPhone()).build());

        AdvertResponse advertResponse = advertService.getAdvert(advert.getId());

        verify(advertRepository).findWithDetailsById(advert.getId());
        assertThat(advertResponse).isNotNull();
        assertThat(advertResponse.getPhone()).isEqualTo(advert.getPhone());
    }

    @Test
    void getAdvertShouldThrowNotFoundExceptionWhenAdvertNotFound() {
        when(advertRepository.findWithDetailsById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            advertService.getAdvert(1L)
        );

        assertThat(exception.getMessage()).isEqualTo("Объявление не найдено");
    }

    @Test
    void updateAdvertShouldUpdateExistingAdvert() {
        AdvertRequest updateAdvertRequest = new AdvertRequest();
        updateAdvertRequest.setPhone("88005553535");
        when(advertRepository.findById(advert.getId())).thenReturn(Optional.of(advert));
        doAnswer(invocation -> {
            AdvertRequest request = invocation.getArgument(0);
            AdvertEntity entity = invocation.getArgument(1);
            if (request.getPhone() != null) entity.setPhone(request.getPhone());
            return null;
        }).when(advertMapper).updateAdvertFromDto(any(), any()); 
        when(advertMapper.toAdvertResponse(any()))
            .thenReturn(AdvertResponse.builder().build());

        AdvertResponse advertResponse = advertService.updateAdvert(advert.getId(), updateAdvertRequest);

        verify(advertRepository).findById(advert.getId());
        verify(advertMapper).updateAdvertFromDto(eq(updateAdvertRequest), eq(advert));
        assertThat(advert.getPhone()).isEqualTo(updateAdvertRequest.getPhone());
        assertThat(advertResponse).isNotNull();
    }

    @Test
    void updateAdvertShouldThrowNotFoundExceptionWhenAdvertNotFound() {
        when(advertRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            advertService.updateAdvert(1L, advertRequest)
        );

        assertThat(exception.getMessage()).isEqualTo("Объявление не найдено");
    }

    @Test
    void deleteAdvertShouldUpdateExistingAdvert() {
        when(advertRepository.findById(anyLong())).thenReturn(Optional.of(advert));

        advertService.deleteAdvert(advert.getId());

        verify(advertRepository).findById(advert.getId());
        verify(advertRepository).delete(advert);
    }

    @Test
    void deleteAdvertShouldThrowNotFoundExceptionWhenAdvertNotFound() {
        when(advertRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            advertService.deleteAdvert(1L)
        );

        assertThat(exception.getMessage()).isEqualTo("Объявление не найдено");
    }

}
