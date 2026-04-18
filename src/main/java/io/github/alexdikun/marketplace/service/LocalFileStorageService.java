package io.github.alexdikun.marketplace.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.github.alexdikun.marketplace.config.StorageProperties;
import io.github.alexdikun.marketplace.exceptions.BadRequestException;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path uploadPath;

    public LocalFileStorageService(StorageProperties storageProperties) {

        this.uploadPath = Paths.get(storageProperties.getUploadDir())
            .toAbsolutePath()
            .normalize();

        try {
            Files.createDirectories(uploadPath); 
        } catch (IOException exception) {
            throw new RuntimeException("Не удалось создать папку загрузки", exception);
        }
    }

    @Override
    public String saveFile(MultipartFile file) {

        try {
            validateFile(file);
            String original = Optional.ofNullable(file.getOriginalFilename())
                .orElse("file");

            String filename = UUID.randomUUID() + "_" + original;
            Path targetLocation = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation);
            return filename;
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка сохранения файла", exception); // проблема сервера?
        }
    }

    @Override
    public void deleteFile(String filename) {
        try {
            Path filePath = uploadPath.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка удаления файла", exception); // проблема сервера?
        }
    }

    private void validateFile(MultipartFile file) throws IOException {
        if (file.isEmpty())
            throw new BadRequestException("Файл пустой");
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) 
            throw new BadRequestException("Можно загружать только изображения");

        BufferedImage image = ImageIO.read(file.getInputStream());

        if (image == null)
            throw new BadRequestException("Файл не является изображением");

    }
    
}
