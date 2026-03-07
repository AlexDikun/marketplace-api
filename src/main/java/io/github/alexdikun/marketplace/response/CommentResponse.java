package io.github.alexdikun.marketplace.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentResponse {
    private Long id;
    private String content;
    private Long userId;
    private Long advertId;
}
