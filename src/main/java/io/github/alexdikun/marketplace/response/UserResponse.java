package io.github.alexdikun.marketplace.response;

import io.github.alexdikun.marketplace.enums.Role;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String name;
    private String Login;
    private Role role;
}
