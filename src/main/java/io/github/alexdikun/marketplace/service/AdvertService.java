package io.github.alexdikun.marketplace.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.github.alexdikun.marketplace.repository.AdvertRepository;
import io.github.alexdikun.marketplace.request.AdvertRequest;
import io.github.alexdikun.marketplace.response.AdvertResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdvertService {

    // private final AdvertRepository advertRepository;

    public AdvertResponse createAdvert(AdvertRequest advertRequest) {
        System.out.println("Cоздаем объявление!");

        return AdvertResponse.builder()
            .id(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE)
            .title(advertRequest.getTitle())
            .cost(advertRequest.getCost())
            .address(advertRequest.getAddress())
            .phone(advertRequest.getPhone())
            .description(advertRequest.getDescription())
            .build();
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

        return AdvertResponse.builder()
            .id(id)
            .title(advertRequest.getTitle())
            .cost(advertRequest.getCost())
            .address(advertRequest.getAddress())
            .phone(advertRequest.getPhone())
            .description(advertRequest.getDescription())
            .build();
    }

    public String deleteAdvertById(Long id) {
        System.out.println("Удаляем объявление с id: " + id);
        return "Объявление с id: " + id + " удалено!";
    }

}
