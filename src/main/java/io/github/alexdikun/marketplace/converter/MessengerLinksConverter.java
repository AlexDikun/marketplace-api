package io.github.alexdikun.marketplace.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.alexdikun.marketplace.enums.MessengerType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Converter(autoApply = true) 
@RequiredArgsConstructor
public class MessengerLinksConverter implements AttributeConverter<Map<MessengerType, String>, String> {

    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(Map<MessengerType, String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка конвертации messengerLinks в JSON", exception);
        }
    }

    @Override
    public Map<MessengerType, String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank() || "{}".equals(dbData)) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<Map<MessengerType, String>>() {});
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка парсинга JSON из messengerLinks", exception);
        }
    }
}