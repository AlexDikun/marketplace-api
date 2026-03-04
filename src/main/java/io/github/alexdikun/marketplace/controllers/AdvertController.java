package io.github.alexdikun.marketplace.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.alexdikun.marketplace.request.AdvertRequest;
import io.github.alexdikun.marketplace.response.AdvertResponse;
import io.github.alexdikun.marketplace.service.AdvertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/v1/adverts")
@RequiredArgsConstructor
@Tag(name= "Advert", description = "API объявлений")
public class AdvertController {

    private final AdvertService advertService;

    @PostMapping
    @Operation(summary = "Создание объявления")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Объявление добавлено"),
        @ApiResponse(responseCode = "400", description = "Неверно переданные данные"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<AdvertResponse> createAdvert(
        @Parameter(description = "Модель для создания данных")
        @RequestBody AdvertRequest request) {
        return new ResponseEntity<>(advertService.createAdvert(request), HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    @Operation(summary = "Получение объявления по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Объявление прочитано"),
        @ApiResponse(responseCode = "404", description = "Объявление не найдено"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<AdvertResponse> getAdvert(@PathVariable Long id) {
        return new ResponseEntity<>(advertService.getAdvertById(id), HttpStatus.OK);
    }

    @PutMapping("{id}")
    @Operation(summary = "Изменение объявления по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Объявление изменено"),
        @ApiResponse(responseCode = "400", description = "Неверно переданные данные"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<AdvertResponse> updateAdvert(@PathVariable Long id, @RequestBody AdvertRequest request) {
        return new ResponseEntity<>(advertService.updateAdvertById(id, request), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Удаление объявления по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Объявление удалено"),
        @ApiResponse(responseCode = "404", description = "Объявление не найдено"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<String> deleteAdvert(@PathVariable Long id) {
        return new ResponseEntity<>(advertService.deleteAdvertById(id), HttpStatus.OK);
    }
    
}
