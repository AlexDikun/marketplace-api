package io.github.alexdikun.marketplace.request;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AdvertRequest {
    private String name;
    private Double cost;
    private String address;
    private String phone;
    private String description;
    private LocalDateTime createDateTime;
}
