package io.github.alexdikun.marketplace.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import io.github.alexdikun.marketplace.entities.AdvertEntity;
import io.github.alexdikun.marketplace.entities.ImageEntity;
import io.github.alexdikun.marketplace.mapper.ImageMapper;
import io.github.alexdikun.marketplace.repository.AdvertRepository;
import io.github.alexdikun.marketplace.repository.ImageRepository;
import io.github.alexdikun.marketplace.response.ImageResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final AdvertRepository advertRepository;
    private final ImageRepository imageRepository;
    private FileStorageService fileStorageService;
    private final ImageMapper imageMapper;

    @Transactional
    public ImageResponse uploadImage(Long advertId, MultipartFile file) {
        System.out.println("Добавляем изображение к объявлению!");

        AdvertEntity advert = advertRepository.findById(advertId)
            .orElseThrow(() -> new RuntimeException("Объявление не найдено"));
        String filename = fileStorageService.saveFile(file);

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setUrl(filename);
        imageEntity.setAdvert(advert);

        ImageEntity savedImage = imageRepository.save(imageEntity);
        return imageMapper.toImageResponse(savedImage);
    }

    public ImageResponse getImage(Long id) {
        System.out.println("Получаем изображение по id: " + id);

        ImageEntity imageEntity = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Изображение не найдено"));

        return imageMapper.toImageResponse(imageEntity);
    }

    @Transactional
    public void deleteImage(Long id) {
        System.out.println("В объявлении, удаляем изображение с id: " + id);

        ImageEntity imageEntity = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Изображение не найдено"));

        fileStorageService.deleteFile(imageEntity.getUrl());
        imageRepository.delete(imageEntity);
        
    }
}
