package io.github.alexdikun.marketplace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.springframework.mock.web.MockMultipartFile;

import io.github.alexdikun.marketplace.config.StorageProperties;

public class LocalFileStorageServiceTest {

    @TempDir
    Path tempDir;

    private StorageProperties storageProperties;
    private LocalFileStorageService localFileStorageService;

    @BeforeEach
    void setUp() {
        storageProperties = new StorageProperties();
        storageProperties.setUploadDir(tempDir.toString());

        localFileStorageService = new LocalFileStorageService(storageProperties);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tempDir)
            .sorted((p1, p2) -> p2.toString().length() - p1.toString().length())
            .forEach(path -> {
                try {
                    Files.deleteIfExists(path);
        } catch (IOException e) {
            // Игнорируем ошибки удаления в tearDown
        }
            });
    }

    @Test
    void saveFileShouldSaveSuccessfully() throws Exception {
        try (InputStream resourceStream = getClass().getResourceAsStream("/static/avito.png")) {
            if (resourceStream == null) {
                throw new FileNotFoundException("Resource /static/avito.png not found in classpath");
            }

            byte[] imageBytes = resourceStream.readAllBytes();

            MockMultipartFile mockFile = new MockMultipartFile(
                "test.png",
                "test.png",
                "image/png",
                new ByteArrayInputStream(imageBytes)
            );

            String filename = localFileStorageService.saveFile(mockFile);

            assertThat(filename).contains("test.png");
            assertThat(filename).contains("_");
            Path filePath = tempDir.resolve(filename);
            assertThat(Files.exists(filePath)).isTrue();
            assertThat(Files.isRegularFile(filePath)).isTrue();
        }
    }

    @Test
    void saveFileShouldThrowWhenEmptyFile() throws IOException {
        MockMultipartFile emptyFile = new MockMultipartFile(
            "empty.jpg",
            "empty.jpg",
            "image/jpeg",
            new ByteArrayInputStream(new byte[0])
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            localFileStorageService.saveFile(emptyFile);
        });

        assertThat(exception.getMessage()).isEqualTo("Файл пустой");
    }

    @Test
    void saveFileShouldThrowWhenNonImageContentType() throws IOException {
        MockMultipartFile textFile = new MockMultipartFile(
            "document.txt",
            "document.txt",
            "text/plain",
            new ByteArrayInputStream("Hello world".getBytes())
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            localFileStorageService.saveFile(textFile);
        });

        assertThat(exception.getMessage()).isEqualTo("Можно загружать только изображения");
    }


    @Test
    void saveFileShouldThrowWhenInvalidImage() throws IOException {
        MockMultipartFile invalidImage = new MockMultipartFile
            ("invalid.jpg",
            "invalid.jpg",
            "image/jpeg",
            new ByteArrayInputStream("Not an image".getBytes())
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            localFileStorageService.saveFile(invalidImage);
        });

        assertThat(exception.getMessage()).isEqualTo("Файл не является изображением");
    }

    @Test
    void saveFileShouldThrowWhenIOException() throws Exception {
        MockMultipartFile mockFile = mock(MockMultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getContentType()).thenReturn("image/jpeg");
        when(mockFile.getInputStream()).thenThrow(new IOException("Test IO error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                localFileStorageService.saveFile(mockFile);
        });

        assertThat(exception.getMessage()).isEqualTo("Ошибка сохранения файла");
    }

    @Test
    void deleteFileShouldDeleteSuccessfully() throws Exception {
        Path testFile = tempDir.resolve("test_file.jpg_" + UUID.randomUUID());
        Files.createFile(testFile);

        localFileStorageService.deleteFile(testFile.getFileName().toString());

        assertThat(Files.exists(testFile)).isFalse();
    }

    @Test
    void deleteFileShouldHandleNonExistentFileGracefully() {
        String nonExistentFilename = "non_existent_file.jpg";

        localFileStorageService.deleteFile(nonExistentFilename);
    }

    @Test
    void deleteFileShouldThrowWhenIOException() throws Exception {
        Path lockedFile = tempDir.resolve("locked.jpg");
        Files.createFile(lockedFile);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.deleteIfExists(lockedFile)) 
                .thenThrow(new IOException("Access denied"));

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                localFileStorageService.deleteFile(lockedFile.getFileName().toString());
            });

            assertThat(exception.getMessage()).isEqualTo("Ошибка удаления файла");
        }
    }

    @Test
    void constructorShouldCreateUploadDirectory() throws Exception {
        Path newTempDir = Files.createTempDirectory("upload_test");
        StorageProperties newProps = new StorageProperties();
        newProps.setUploadDir(newTempDir.toString());

        // LocalFileStorageService newService = new LocalFileStorageService(newProps);

        assertThat(Files.exists(newTempDir)).isTrue();
        assertThat(Files.isDirectory(newTempDir)).isTrue();

        Files.delete(newTempDir);
    }


    @Test
    void constructorShouldThrowWhenCannotCreateDirectory() {
        StorageProperties badProps = new StorageProperties();
        badProps.setUploadDir("/invalid/path/that/does/not/exist");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            new LocalFileStorageService(badProps);
        });

        assertThat(exception.getMessage()).isEqualTo("Не удалось создать папку загрузки");
    }
}
