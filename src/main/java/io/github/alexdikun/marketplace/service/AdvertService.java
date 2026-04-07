package io.github.alexdikun.marketplace.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.alexdikun.marketplace.entities.AdvertEntity;
import io.github.alexdikun.marketplace.entities.CategoryEntity;
import io.github.alexdikun.marketplace.entities.UserEntity;
import io.github.alexdikun.marketplace.exceptions.NotFoundException;
import io.github.alexdikun.marketplace.mapper.AdvertMapper;
import io.github.alexdikun.marketplace.repository.AdvertRepository;
import io.github.alexdikun.marketplace.repository.CategoryRepository;
import io.github.alexdikun.marketplace.request.AdvertRequest;
import io.github.alexdikun.marketplace.response.AdvertResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdvertService {

    private final AdvertRepository advertRepository;
    private final CategoryRepository categoryRepository;
    private final AdvertMapper advertMapper;
    private final CurrentUserService currentUserService;

    @Transactional
    public AdvertResponse createAdvert(AdvertRequest advertRequest) {
        log.info("Cоздаем объявление. userId={}", 
            currentUserService.getCurrentUser().getId());

        AdvertEntity advert = advertMapper.toAdvertEntity(advertRequest);

        UserEntity currentUser = currentUserService.getCurrentUser();
        
        CategoryEntity category = categoryRepository.findById(advertRequest.getCategoryId())
            .orElseThrow(() -> {
                log.warn("Категория не найдена. categoryId = {}", advertRequest.getCategoryId());
                return new NotFoundException("Категория не найдена");
            });

        advert.setUser(currentUser);
        advert.setCategory(category);

        AdvertEntity savedAdvert = advertRepository.save(advert);

        log.info("Объявление создано. advertId = {}", savedAdvert.getId());

        return advertMapper.toAdvertResponse(savedAdvert);
    }

    public Page<AdvertResponse> searchAdverts(String query, int page, int size) {
        log.info("Получаем список объявлений по поисковому запросу!");

        Pageable pageable = PageRequest.of(page, size);
        Page<AdvertEntity> advertPage = advertRepository.search(query, pageable);

        log.info("Распечатано объявлений на текущей странице = {} ", 
            advertPage.getNumberOfElements());

        return advertPage.map(advertMapper::toAdvertResponse);
    }

    public AdvertResponse getAdvert(Long id) {
        log.info("Получаем объявление. advertId = {} ", id);

        AdvertEntity advert = advertRepository.findWithDetailsById(id)
            .orElseThrow(() -> { 
                log.warn("Объявление не найдено. advertId = {}", id);
                return new NotFoundException("Объявление не найдено");
            });
        
        return advertMapper.toAdvertResponse(advert);
    }

    @Transactional
    public AdvertResponse updateAdvert(Long id, AdvertRequest advertRequest) {
        log.info("Изменение объявления. advertId = {} ", id);

        AdvertEntity advert = advertRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Объявление не найдено. advertId = {}", id);
                return new NotFoundException("Объявление не найдено");
            });

        advertMapper.updateAdvertFromDto(advertRequest, advert);

        log.info("Объявление обновлено. advertId = {}", advert.getId());

        return advertMapper.toAdvertResponse(advert);
    }

    @Transactional
    public void deleteAdvert(Long id) {
        log.info("Удаление объявления. advertId = {} ", id);

        AdvertEntity advert = advertRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Объявление не найдено. advertId = {}", id);
                return new NotFoundException("Объявление не найдено");
            });

        advertRepository.delete(advert);
    }

}
