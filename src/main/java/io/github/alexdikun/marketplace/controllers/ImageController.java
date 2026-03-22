package io.github.alexdikun.marketplace.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.alexdikun.marketplace.response.ImageResponse;
import io.github.alexdikun.marketplace.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@Validated
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
    public ResponseEntity<ImageResponse> getImage(@PathVariable @Positive Long id) {
        return new ResponseEntity<>(imageService.getImage(id), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Удаление изображения по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Изображение удалено"),
        @ApiResponse(responseCode = "404", description = "Изображение не найдено"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<Void> deleteImage(@PathVariable @Positive Long id) {
        return new ResponseEntity<>( HttpStatus.NO_CONTENT);
    }
}
