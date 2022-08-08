package ru.adamdev.purchases.list.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.istack.internal.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonUtil {

    private static final Logger LOGGER = Logger.getLogger(JsonUtil.class.getName());

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
            LOGGER.log(Level.WARNING, "Не удалось сериализовать объект");
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
            LOGGER.log(Level.WARNING, "Не удалось десериализовать объект");
        }
        return null;
    }
}