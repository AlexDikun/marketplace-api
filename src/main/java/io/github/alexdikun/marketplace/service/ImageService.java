package io.github.alexdikun.marketplace.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import io.github.alexdikun.marketplace.entities.AdvertEntity;
import io.github.alexdikun.marketplace.entities.ImageEntity;
import io.github.alexdikun.marketplace.exceptions.NotFoundException;
import io.github.alexdikun.marketplace.mapper.ImageMapper;
import io.github.alexdikun.marketplace.repository.AdvertRepository;
import io.github.alexdikun.marketplace.repository.ImageRepository;
import io.github.alexdikun.marketplace.response.ImageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final AdvertRepository advertRepository;
    private final ImageRepository imageRepository;
    private final FileStorageService fileStorageService;
    private final ImageMapper imageMapper;

    @Transactional
    public ImageResponse uploadImage(Long advertId, MultipartFile file) {
        log.info("Добавляем изображение к объявлению. advertId = {}", advertId);

        AdvertEntity advert = advertRepository.findById(advertId)
            .orElseThrow(() -> {
                log.warn("Объявление не найдено. advertId = {}", advertId);
                return new NotFoundException("Объявление не найдено");
            });

        String filename = fileStorageService.saveFile(file);

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setUrl(filename);
        imageEntity.setAdvert(advert);

        ImageEntity savedImage = imageRepository.save(imageEntity);
        log.info("Изображение добавлено. imageId = {}", savedImage.getId());
        return imageMapper.toImageResponse(savedImage);
    }

    public ImageResponse getImage(Long id) {
        log.info("Получаем изображение. imageId = {}", id);

        ImageEntity imageEntity = imageRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Изображение не найдено. imageId = {}", id);
                return new NotFoundException("Изображение не найдено");
            });

        return imageMapper.toImageResponse(imageEntity);
    }

    @Transactional
    public void deleteImage(Long id) {
        log.info("Удаляем изображение. imageId = {}", id);

        ImageEntity imageEntity = imageRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Изображение не найдено. imageId = {}", id);
                return new NotFoundException("Изображение не найдено");
            });

        fileStorageService.deleteFile(imageEntity.getUrl());
        imageRepository.delete(imageEntity);
        
    }
}
