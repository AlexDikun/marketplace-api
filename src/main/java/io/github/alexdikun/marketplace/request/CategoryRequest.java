package io.github.alexdikun.marketplace.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank
    private String name;
    
    @Positive
    private Long parentId;

}
