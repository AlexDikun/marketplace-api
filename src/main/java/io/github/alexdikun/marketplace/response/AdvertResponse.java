package io.github.alexdikun.marketplace.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdvertResponse {
    private Long id;
    private String title;
    private BigDecimal cost;
    private String address;
    private String phone;
    private String description;

    private Long userId;
    private Long categoryId;

    private Instant createdAt;

    List<ImageResponse> images;
}
