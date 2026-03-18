package io.github.alexdikun.marketplace.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String saveFile(MultipartFile file);

    void deleteFile(String filename);
    
}
