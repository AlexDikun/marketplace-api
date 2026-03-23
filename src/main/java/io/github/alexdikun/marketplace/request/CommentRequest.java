package io.github.alexdikun.marketplace.request;

import io.github.alexdikun.marketplace.validation.OnCreate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CommentRequest {

    @NotBlank
    private String content;

    @Positive
    private Long parentId;

    @Positive
    @NotBlank(groups = OnCreate.class)
    private Long userId;
    
}
