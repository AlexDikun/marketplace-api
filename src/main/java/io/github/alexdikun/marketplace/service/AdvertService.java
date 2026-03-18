package io.github.alexdikun.marketplace.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.alexdikun.marketplace.entities.AdvertEntity;
import io.github.alexdikun.marketplace.entities.CategoryEntity;
import io.github.alexdikun.marketplace.entities.UserEntity;
import io.github.alexdikun.marketplace.mapper.AdvertMapper;
import io.github.alexdikun.marketplace.repository.AdvertRepository;
import io.github.alexdikun.marketplace.repository.CategoryRepository;
import io.github.alexdikun.marketplace.repository.UserRepository;
import io.github.alexdikun.marketplace.request.AdvertRequest;
import io.github.alexdikun.marketplace.response.AdvertResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdvertService {

    private final AdvertRepository advertRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AdvertMapper advertMapper;

    @Transactional
    public AdvertResponse createAdvert(AdvertRequest advertRequest) {
        System.out.println("Cоздаем объявление!");

        AdvertEntity advert = advertMapper.toAdvertEntity(advertRequest);

        UserEntity user = userRepository.findById(advertRequest.getUserId())
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        CategoryEntity category = categoryRepository.findById(advertRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        advert.setUser(user);
        advert.setCategory(category);

        AdvertEntity savedAdvert = advertRepository.save(advert);

        return advertMapper.toAdvertResponse(savedAdvert);
    }

    public Page<AdvertResponse> searchAdverts(String query, int page, int size) {
        System.out.println("Получаем список объявлений по поисковому запросу!");

        Pageable pageable = PageRequest.of(page, size);
        Page<AdvertEntity> advertPage = advertRepository.search(query, pageable);
        return advertPage.map(advertMapper::toAdvertResponse);
    }

    public AdvertResponse getAdvert(Long id) {
        System.out.println("Получаем объявление по id: " + id);

        AdvertEntity advert = advertRepository.findWithDetailsById(id)
            .orElseThrow(() -> new RuntimeException("Объявление не найдено"));
        
        return advertMapper.toAdvertResponse(advert);
    }

    @Transactional
    public AdvertResponse updateAdvert(Long id, AdvertRequest advertRequest) {
        System.out.println("Изменение объявления с id: " + id);

        AdvertEntity advert = advertRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Объявление не найдено"));

        advertMapper.updateAdvertFromDto(advertRequest, advert);
        return advertMapper.toAdvertResponse(advert);
    }

    @Transactional
    public void deleteAdvert(Long id) {
        System.out.println("Удаляем объявление с id: " + id);

        AdvertEntity advert = advertRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Объявление не найдено"));

        advertRepository.delete(advert);
    }

}
