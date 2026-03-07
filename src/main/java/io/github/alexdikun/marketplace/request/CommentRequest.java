package io.github.alexdikun.marketplace.request;

import lombok.Data;

@Data
public class CommentRequest {
    private String content;
    private Long userId;
    private Long advertId;
}
