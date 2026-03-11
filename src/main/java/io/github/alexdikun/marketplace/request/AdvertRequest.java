package io.github.alexdikun.marketplace.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AdvertRequest {
    private String title;
    private BigDecimal cost;
    private String address;
    private String phone;
    private String description;

    private Long userId;
    private Long categoryId;
}
