package ru.adamdev.purchases.list.model;

import lombok.Builder;
import lombok.Data;
import ru.adamdev.purchases.list.constant.MethodType;

import java.io.File;

@Data
@Builder
public class InputParams {

    private MethodType methodType;
    private File inputFile;
    private File outputFile;
}
