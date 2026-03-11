package io.github.alexdikun.marketplace.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.github.alexdikun.marketplace.entities.AdvertEntity;
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

    // private final AdvertRepository advertRepository;
    // private final UserRepository userRepository;
    // private final CategoryRepository categoryRepository;
    private final AdvertMapper advertMapper;

    public AdvertResponse createAdvert(AdvertRequest advertRequest) {
        System.out.println("Cоздаем объявление!");

        AdvertEntity advert = advertMapper.toAdvertEntity(advertRequest);
        return advertMapper.toAdvertResponse(advert);
    }

    public AdvertResponse getAdvertById(Long id) {
        System.out.println("Получаем объявление по id: " + id);

        return AdvertResponse.builder()       
            .id(id)
            .title("Имя лота")
            .cost(new BigDecimal("0.00"))
            .address("Адрес лота")
            .phone("Номер телефона продавца")
            .description("Описание лота")
            .build();
    }

    public AdvertResponse updateAdvertById(Long id, AdvertRequest advertRequest) {
        System.out.println("Изменение объявления с id: " + id);

        AdvertEntity advert = advertMapper.toAdvertEntity(advertRequest);
        return advertMapper.toAdvertResponse(advert);
    }

    public String deleteAdvertById(Long id) {
        System.out.println("Удаляем объявление с id: " + id);
        return "Объявление с id: " + id + " удалено!";
    }

}
