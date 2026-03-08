package io.github.alexdikun.marketplace.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.alexdikun.marketplace.request.ImageRequest;
import io.github.alexdikun.marketplace.response.ImageResponse;
import io.github.alexdikun.marketplace.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/image")
@RequiredArgsConstructor
@Tag(name= "Image", description = "API изображений")
public class ImageController {
    
    private final ImageService imageService;

    @GetMapping("{id}")
    @Operation(summary = "Получение изображения по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Изображение получено"),
        @ApiResponse(responseCode = "404", description = "Изображение не найдено"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<ImageResponse> getImage(@PathVariable Long id) {
        return new ResponseEntity<>(imageService.getImageById(id), HttpStatus.OK);
    }

    @PutMapping("{id}")
    @Operation(summary = "Замена изображения по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Изображение заменено"),
        @ApiResponse(responseCode = "400", description = "Неверно переданные данные"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<ImageResponse> updateImage(@PathVariable Long id, @RequestBody ImageRequest imageRequest) {
        return new ResponseEntity<>(imageService.updateImageById(id, imageRequest), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Удаление изображения по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Изображение удалено"),
        @ApiResponse(responseCode = "404", description = "Изображение не найдено"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<String> deleteImage(@PathVariable Long id) {
        return new ResponseEntity<>(imageService.deleteImageById(id), HttpStatus.OK);
    }
}
