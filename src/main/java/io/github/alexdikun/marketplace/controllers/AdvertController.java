package io.github.alexdikun.marketplace.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.alexdikun.marketplace.request.AdvertRequest;
import io.github.alexdikun.marketplace.response.AdvertResponse;
import io.github.alexdikun.marketplace.service.AdvertService;
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
public class AdvertController {

    private final AdvertService advertService;

    @PostMapping
    public ResponseEntity<AdvertResponse> createAdvert(@RequestBody AdvertRequest request) {
        return new ResponseEntity<>(advertService.createAdvert(request), HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<AdvertResponse> getAdvert(@PathVariable Long id) {
        return new ResponseEntity<>(advertService.getAdvertisementById(id), HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<AdvertResponse> updateAdvert(@PathVariable Long id, @RequestBody AdvertRequest request) {
        return new ResponseEntity<>(advertService.updateAdvertisementById(id, request), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteAdvert(@PathVariable Long id) {
        return new ResponseEntity<>(advertService.deleteAdvertById(id), HttpStatus.OK);
    }
    
}
