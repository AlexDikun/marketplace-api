package io.github.alexdikun.marketplace.response;

import java.time.Instant;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String email;
    private String displayName;
    private Map<String, Object> messengerLinks;
    private Instant createdAt;
}
