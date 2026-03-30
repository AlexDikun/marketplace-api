package io.github.alexdikun.marketplace.response;

import java.time.Instant;
import java.util.Map;

import io.github.alexdikun.marketplace.enums.MessengerType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String email;
    private String displayName;
    private Map<MessengerType, String> messengerLinks;
    private Instant createdAt;
}
