package io.github.alexdikun.marketplace.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.github.alexdikun.marketplace.exceptions.BadRequestException;
import io.github.alexdikun.marketplace.exceptions.ConflictException;
import io.github.alexdikun.marketplace.exceptions.NotFoundException;
import io.github.alexdikun.marketplace.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ErrorResponse> unauthorizedRequest(
        AuthenticationCredentialsNotFoundException  exception,
        HttpServletRequest request) {

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.UNAUTHORIZED.value())
            .errors(List.of(exception.getMessage()))
            .path(request.getRequestURI())
            .build();

            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> forbiddenRequest(
        AccessDeniedException exception,
        HttpServletRequest request) {

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.FORBIDDEN.value())
            .errors(List.of(exception.getMessage()))
            .path(request.getRequestURI())
            .build();

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
        BadRequestException exception,
        HttpServletRequest request) {

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .errors(List.of(exception.getMessage()))
            .path(request.getRequestURI())
            .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
        NotFoundException exception,
        HttpServletRequest request) {

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .errors(List.of(exception.getMessage()))
            .path(request.getRequestURI())
            .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
        ConflictException exception,
        HttpServletRequest request) {

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.CONFLICT.value())
            .errors(List.of(exception.getMessage()))
            .path(request.getRequestURI())
            .build();

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(
        Exception exception,
        HttpServletRequest request) {

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .errors(List.of("Internal server error"))
            .path(request.getRequestURI())
            .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
        ConstraintViolationException exception,
        HttpServletRequest request) {

        List<String> errors = exception.getConstraintViolations()
            .stream()
            .map(violation -> violation.getPropertyPath() + ": %s" + violation.getMessage())
            .toList();

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .errors(errors)
            .path(request.getRequestURI())
            .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

    }   

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException exception,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request) {

        List<String> errors = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> String.format(error.getField() + ": %s" + error.getDefaultMessage()))
            .toList();

        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .errors(errors)
            .path(((ServletWebRequest) request).getRequest().getRequestURI())
            .build();

        return ResponseEntity.badRequest().body(errorResponse);

    }
    
}
