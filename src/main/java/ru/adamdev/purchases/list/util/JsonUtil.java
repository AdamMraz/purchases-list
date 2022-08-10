package ru.adamdev.purchases.list.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jetbrains.annotations.Nullable;
import org.apache.log4j.Logger;

public class JsonUtil {

    private static final Logger LOGGER = Logger.getLogger(JsonUtil.class);

    @Nullable
    public static <T> String serialize(T t) {
        try {
            return JsonMapper.builder()
                    .addModule(new JavaTimeModule())
                    .build()
                    .writer()
                    .withDefaultPrettyPrinter()
                    .writeValueAsString(t);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Не удалось сериализовать объект. " + e.getMessage());
        }
        return null;
    }

    @Nullable
    public static <T> T deserialize(String json, Class<T> clazz) {
        try {
            return JsonMapper.builder()
                    .addModule(new JavaTimeModule())
                    .build()
                    .readValue(json, clazz);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Не удалось десериализовать объект. " + e.getMessage());
        }
        return null;
    }
}