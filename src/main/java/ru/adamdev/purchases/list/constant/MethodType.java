package ru.adamdev.purchases.list.constant;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum MethodType {

    SEARCH,
    STAT;

    @Nullable
    public static MethodType getMethodType(@Nullable String name) {
        if (name == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(type -> name.equalsIgnoreCase(type.name()))
                .findFirst()
                .orElse(null);
    }
}
