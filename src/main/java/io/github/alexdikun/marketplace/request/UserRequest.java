package io.github.alexdikun.marketplace.request;

import io.github.alexdikun.marketplace.enums.Role;
import lombok.Data;

@Data
public class UserRequest {
    private String name;
    private String login;
    private Role role;
}
