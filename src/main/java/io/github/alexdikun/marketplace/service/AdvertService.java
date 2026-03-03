package io.github.alexdikun.marketplace.service;

import org.springframework.stereotype.Service;

import io.github.alexdikun.marketplace.repository.AdvertRepository;
import io.github.alexdikun.marketplace.request.AdvertRequest;
import io.github.alexdikun.marketplace.response.AdvertResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdvertService {

    private final AdvertRepository advertRepository;

    public AdvertResponse createAdvert(AdvertRequest advertRequest) {
        System.out.println("Cоздаем объявление!");

        return AdvertResponse.builder()
            .id(14L)
            .name("Mark Tven")
            .cost(22.5)
            .address("Sevastopol, 135")
            .phone("88005553535")
            .description("nice book")
            .build();
    }

    public AdvertResponse getAdvertisementById(Long id) {
        System.out.println("Получаем объявление по id: " + id);

        return AdvertResponse.builder()
            .id(id)
            .name("Mark Tven")
            .cost(22.5)
            .address("Sevastopol, 135")
            .phone("88005553535")
            .description("nice book")
            .build();
    }

    public AdvertResponse updateAdvertisementById(Long id, AdvertRequest advertRequest) {
        System.out.println("Изменение объявления с id: " + id);

        return AdvertResponse.builder()
            .id(id)
            .name(advertRequest.getName())
            .cost(advertRequest.getCost())
            .address(advertRequest.getAddress())
            .phone(advertRequest.getPhone())
            .description(advertRequest.getDescription())
            .build();
    }

    public void deleteAdvertById(Long id) {
        System.out.println("Удаляем объявление с id: " + id);
    }

}
