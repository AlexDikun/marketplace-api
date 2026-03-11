package io.github.alexdikun.marketplace.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import io.github.alexdikun.marketplace.entities.ImageEntity;
import io.github.alexdikun.marketplace.mapper.ImageMapper;
import io.github.alexdikun.marketplace.request.ImageRequest;
import io.github.alexdikun.marketplace.response.ImageResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageMapper imageMapper;

    public ImageResponse createImage(Long advertId, ImageRequest imageRequest) {
        System.out.println("Добавляем изображение к объявлению!");

        ImageEntity imageEntity = imageMapper.toImageEntity(imageRequest);
        return imageMapper.toImageResponse(imageEntity);
    }

    public ImageResponse getImageById(Long id) {
        System.out.println("Получаем изображение по id: " + id);

        return ImageResponse.builder()
            .id(id)
            .url("file path")
            .build();
    }

    public ImageResponse updateImageById(Long id, ImageRequest imageRequest) {
        System.out.println("Заменяем изображение в объявлении по id: " + id);

        return ImageResponse.builder()
            .id(id)
            .url(imageRequest.getUrl())
            .build();
    }

    public String deleteImageById(Long id) {
        System.out.println("В объявлении, удаляем изображение с id: " + id);
        return "Изображение с id: " + id + " удалено!";
    }
}
