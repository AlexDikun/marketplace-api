package io.github.alexdikun.marketplace.request;

import java.math.BigDecimal;

import io.github.alexdikun.marketplace.validation.OnCreate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class AdvertRequest {
    
    @NotBlank(groups = OnCreate.class) 
    private String title;

    @PositiveOrZero
    private BigDecimal cost;

    @NotBlank(groups = OnCreate.class) 
    private String address;

    @NotBlank(groups = OnCreate.class) 
    private String phone;

    private String description;

    @Positive
    private Long categoryId;
    
}
