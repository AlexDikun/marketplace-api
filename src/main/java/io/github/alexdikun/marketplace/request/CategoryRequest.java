package io.github.alexdikun.marketplace.request;

import lombok.Data;

@Data
public class CategoryRequest {
    String name;
    Long parentId;
}
