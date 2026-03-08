package io.github.alexdikun.marketplace.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import io.github.alexdikun.marketplace.request.ImageRequest;
import io.github.alexdikun.marketplace.response.ImageResponse;

@Service
public class ImageService {

    public ImageResponse createImage(Long advertId, ImageRequest imageRequest) {
        System.out.println("Добавляем изображение к объявлению!");

        return ImageResponse.builder()
            .id(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE)
            .content(imageRequest.getContent())
            .advertId(imageRequest.getAdvertId())
            .build();
    }

    public ImageResponse getImageById(Long id) {
        System.out.println("Получаем изображение по id: " + id);

        return ImageResponse.builder()
            .id(id)
            .content("матрица значений, преобразующиеся в пиксели")
            .advertId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE)
            .build();
    }

    public ImageResponse updateImageById(Long id, ImageRequest imageRequest) {
        System.out.println("Заменяем изображение в объявлении по id: " + id);

        return ImageResponse.builder()
            .id(id)
            .content(imageRequest.getContent())
            .advertId(imageRequest.getAdvertId())
            .build();
    }

    public String deleteImageById(Long id) {
        System.out.println("В объявлении, удаляем изображение с id: " + id);
        return "Изображение с id: " + id + " удалено!";
    }
}
