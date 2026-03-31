package io.github.alexdikun.marketplace.request;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank(message = "Отображаемое имя не должно быть пустым")
    @Size(max = 50, message = "Отображаемое имя не может быть длиннее 50 символов")
    private String displayName;

    // нужно добавить кастомную валидацию
    private Map<String, Object> messengerLinks;
}
