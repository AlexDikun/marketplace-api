package io.github.alexdikun.marketplace.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.github.alexdikun.marketplace.request.AdvertRequest;
import io.github.alexdikun.marketplace.request.CommentRequest;
import io.github.alexdikun.marketplace.response.AdvertResponse;
import io.github.alexdikun.marketplace.response.CommentResponse;
import io.github.alexdikun.marketplace.response.ImageResponse;
import io.github.alexdikun.marketplace.service.AdvertService;
import io.github.alexdikun.marketplace.service.CommentService;
import io.github.alexdikun.marketplace.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

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
    private final CommentService commentService;
    private final ImageService imageService;

    @PostMapping
    @Operation(summary = "Создание объявления")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Объявление добавлено"),
        @ApiResponse(responseCode = "400", description = "Неверно переданные данные"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<AdvertResponse> createAdvert(
        @Parameter(description = "Модель для создания данных")
        @RequestBody AdvertRequest advertRequest) {
        return new ResponseEntity<>(advertService.createAdvert(advertRequest), HttpStatus.CREATED);
    }

    @GetMapping("/search")
    @Operation(summary = "Получить список интересующих объявлений по поиску")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Запрос выполнен успешно"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<List<AdvertResponse>> searchAdverts(
        @Parameter(description = "Текст запроса с клавиатуры")
        @RequestParam String query
    ) {
        return new ResponseEntity<>(advertService.searchAdverts(query), HttpStatus.OK);
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
    public ResponseEntity<AdvertResponse> updateAdvert(@PathVariable Long id, @RequestBody AdvertRequest advertRequest) {
        return new ResponseEntity<>(advertService.updateAdvertById(id, advertRequest), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Удаление объявления по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Объявление удалено"),
        @ApiResponse(responseCode = "404", description = "Объявление не найдено"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<Void> deleteAdvert(@PathVariable Long id) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("{id}/comments")
    @Operation(summary = "Создает комментарий по модели в объявлении")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Оставлен комментарий"),
        @ApiResponse(responseCode = "400", description = "Неверно переданные данные"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<CommentResponse> leaveCommentOnAdvert(@PathVariable Long id, 
        @Parameter(description = "Модель для создания данных") @RequestBody CommentRequest commentRequest
    ) {
        return new ResponseEntity<>(commentService.createComment(id, commentRequest), HttpStatus.CREATED);
    }

    @GetMapping("{id}/comments")
    @Operation(summary = "Получить список комментариев объявления")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список комментариев прочитан"),
        @ApiResponse(responseCode = "404", description = "Никакие комментарии не найдены"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<List<CommentResponse>> getAllCategories(@PathVariable Long id) {
        return new ResponseEntity<>(commentService.getAllComments(id), HttpStatus.OK);
    }

    @PostMapping("{id}/images")
    @Operation(summary = "Загружает изображение по модели в объявлении")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Изображение добавлено"),
        @ApiResponse(responseCode = "400", description = "Неверно переданные данные"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<ImageResponse> uploadImageOnAdvert(@PathVariable Long id, @RequestParam MultipartFile file) {
        return new ResponseEntity<>(imageService.uploadImage(id, file), HttpStatus.CREATED);
    }
}
