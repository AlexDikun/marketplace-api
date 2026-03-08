package io.github.alexdikun.marketplace.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdvertResponse {
    private Long id;
    private String name;
    private Double cost;
    private String address;
    private String phone;
    private String description;
    private LocalDateTime createDateTime;
}
