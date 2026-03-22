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

import io.github.alexdikun.marketplace.request.CommentRequest;
import io.github.alexdikun.marketplace.response.CommentResponse;
import io.github.alexdikun.marketplace.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
@Tag(name= "Comment", description = "API комментария")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("{id}")
    @Operation(summary = "Получение комментария по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Комментарий прочитан"),
        @ApiResponse(responseCode = "404", description = "Комментарий не найден"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<CommentResponse> getComment(@PathVariable @Positive Long id) {
        return new ResponseEntity<>(commentService.getComment(id), HttpStatus.OK);
    }

    @PutMapping("{id}")
    @Operation(summary = "Изменение комментария по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Комментарий отредактирован"),
        @ApiResponse(responseCode = "400", description = "Неверно переданные данные"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<CommentResponse> updateComment(
        @PathVariable @Positive Long id, 
        @RequestBody CommentRequest commentRequest
    ) {
        return new ResponseEntity<>(commentService.updateComment(id, commentRequest), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Удаление комментария по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Комментарий удален"),
        @ApiResponse(responseCode = "404", description = "Комментарий не найден"),
        @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public ResponseEntity<Void> deleteComment(@PathVariable @Positive Long id) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
}
