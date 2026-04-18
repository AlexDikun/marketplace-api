package io.github.alexdikun.marketplace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import io.github.alexdikun.marketplace.entities.AdvertEntity;
import io.github.alexdikun.marketplace.entities.ImageEntity;
import io.github.alexdikun.marketplace.exceptions.NotFoundException;
import io.github.alexdikun.marketplace.mapper.ImageMapper;
import io.github.alexdikun.marketplace.repository.AdvertRepository;
import io.github.alexdikun.marketplace.repository.ImageRepository;
import io.github.alexdikun.marketplace.response.ImageResponse;
import io.github.alexdikun.marketplace.utils.TestFactoryData;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {
    
    @Mock
    private AdvertRepository advertRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private ImageMapper imageMapper;

    @InjectMocks
    private ImageService imageService;

    @Test
    void uploadImage_ShouldUploadSuccessfully() throws Exception {
        AdvertEntity advertEntity = TestFactoryData.createAdvert(
            TestFactoryData.createUser(), 
            TestFactoryData.createCategory(null));
        Long advertId = advertEntity.getId();

        String filename = "test.jpg_12345";
        MultipartFile mockFile = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", new byte[]{});

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setId(100L);
        imageEntity.setUrl(filename);
        imageEntity.setAdvert(advertEntity);

        ImageResponse expectedResponse = ImageResponse.builder().id(100L).url(filename).build();

        when(advertRepository.findById(advertId)).thenReturn(Optional.of(advertEntity));
        when(fileStorageService.saveFile(mockFile)).thenReturn(filename);
        when(imageRepository.save(any(ImageEntity.class))).thenReturn(imageEntity);
        when(imageMapper.toImageResponse(imageEntity)).thenReturn(expectedResponse);

        ImageResponse result = imageService.uploadImage(advertId, mockFile);

        assertThat(result).isEqualTo(expectedResponse);

        verify(advertRepository).findById(advertId);
        verify(fileStorageService).saveFile(mockFile);
        verify(imageRepository).save(any(ImageEntity.class));
        verify(imageMapper).toImageResponse(imageEntity);
    }

    @Test
    void uploadImage_ShouldThrowWhenAdvertNotFound() {
        Long nonExistentAdvertId = 999L;
        MockMultipartFile mockFile = new MockMultipartFile(
            "test.jpg", 
            "test.jpg", 
            "image/jpeg", new byte[]{}
        );

        when(advertRepository.findById(nonExistentAdvertId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            imageService.uploadImage(nonExistentAdvertId, mockFile);
        });

        assertThat(exception.getMessage()).isEqualTo("Объявление не найдено");
        verify(fileStorageService, never()).saveFile(any());
        verify(imageRepository, never()).save(any());
    }

    @Test
    void getImage_ShouldReturnImage() {
        Long imageId = 100L;
        String url = "test.jpg";

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setId(imageId);
        imageEntity.setUrl(url);

        ImageResponse expectedResponse = ImageResponse.builder().id(imageId).url(url).build();

        when(imageRepository.findById(imageId)).thenReturn(Optional.of(imageEntity));
        when(imageMapper.toImageResponse(imageEntity)).thenReturn(expectedResponse);

        ImageResponse result = imageService.getImage(imageId);

        assertThat(result).isEqualTo(expectedResponse);
        verify(imageRepository, times(1)).findById(imageId);
        verify(imageMapper, times(1)).toImageResponse(imageEntity);
    }

    @Test
    void getImage_ShouldThrowWhenImageNotFound() {
        Long nonExistentImageId = 999L;

        when(imageRepository.findById(nonExistentImageId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            imageService.getImage(nonExistentImageId);
        });

        assertThat(exception.getMessage()).isEqualTo("Изображение не найдено");
        verify(imageMapper, never()).toImageResponse(any());
    }

    @Test
    void deleteImage_ShouldDeleteSuccessfully() {
        Long imageId = 100L;
        String url = "test.jpg";

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setId(imageId);
        imageEntity.setUrl(url);

        when(imageRepository.findById(imageId)).thenReturn(Optional.of(imageEntity));

        imageService.deleteImage(imageId);

        verify(imageRepository).findById(imageId);
        verify(fileStorageService).deleteFile(url);
        verify(imageRepository).delete(imageEntity);
    }

    @Test
    void deleteImage_ShouldThrowWhenImageNotFound() {
        Long nonExistentImageId = 999L;

        when(imageRepository.findById(nonExistentImageId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            imageService.deleteImage(nonExistentImageId);
        });

        assertThat(exception.getMessage()).isEqualTo("Изображение не найдено");
        verify(fileStorageService, never()).deleteFile(anyString());
        verify(imageRepository, never()).delete(any());
    }
}

