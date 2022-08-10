package ru.adamdev.purchases.list.constant;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.adamdev.purchases.list.constant.MethodType.SEARCH;
import static ru.adamdev.purchases.list.constant.MethodType.STAT;

class MethodTypeTest {

    private static final String SEARCH_STR = "search";
    private static final String STAT_STR = "stat";
    private static final Map<String, MethodType> methodTypeMap;

    static {
        Map<String, MethodType> methodTypeTempMap = new HashMap<>();
        methodTypeTempMap.put(SEARCH_STR, SEARCH);
        methodTypeTempMap.put(STAT_STR, STAT);
        methodTypeMap = Collections.unmodifiableMap(methodTypeTempMap);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {SEARCH_STR, STAT_STR})
    void getMethodTypeOk(String strType) {
        MethodType methodType = MethodType.getMethodType(strType);
        assertEquals(methodTypeMap.get(strType), methodType);
    }
}