package ru.adamdev.purchases.list.util;

import org.jetbrains.annotations.Nullable;
import ru.adamdev.purchases.list.constant.MethodType;
import ru.adamdev.purchases.list.exception.InputParamsException;
import ru.adamdev.purchases.list.helper.PurchasesListExceptionWrapper;
import ru.adamdev.purchases.list.helper.impl.PurchasesListExceptionWrapperImpl;
import ru.adamdev.purchases.list.model.InputParams;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static ru.adamdev.purchases.list.constant.ErrorConstants.MESSAGE_ILLEGAL_INPUT_FILE;
import static ru.adamdev.purchases.list.constant.ErrorConstants.MESSAGE_ILLEGAL_OUTPUT_FILE_CREATE;
import static ru.adamdev.purchases.list.constant.ErrorConstants.MESSAGE_ILLEGAL_PARAM;
import static ru.adamdev.purchases.list.constant.ErrorConstants.MESSAGE_ILLEGAL_TYPE;
import static ru.adamdev.purchases.list.constant.ErrorConstants.TYPE_ERROR;

public class ValidationUtil {

    private static final int ARGS_LENGTH = 3;

    public static boolean isNullOrEmpty(Object... o) {
        return Arrays.stream(o)
                .anyMatch(ValidationUtil::isNullOrEmpty);
    }

    public static boolean isNullOrEmpty(Object o) {
        return o == null || (o instanceof String && ((String) o).isEmpty());
    }

    public static InputParams validateInputParams(String[] args) throws InputParamsException {
        if (args.length != ARGS_LENGTH) {
            throw new InputParamsException(TYPE_ERROR, MESSAGE_ILLEGAL_PARAM);
        }
        return prepareInputParams(args[0], args[1], args[2]);
    }

    private static InputParams prepareInputParams(String methodType, String inputFileName, String outputFileName) throws InputParamsException {
        File outputFile = prepareOutputFile(outputFileName);
        MethodType methodType1 = Optional.ofNullable(MethodType.getMethodType(methodType))
                .orElseThrow(() -> new InputParamsException(TYPE_ERROR, MESSAGE_ILLEGAL_TYPE));
        File inputFile = prepareInputFile(inputFileName);
        return InputParams.builder()
                .methodType(methodType1)
                .inputFile(inputFile)
                .outputFile(outputFile)
                .build();
    }

    private static File prepareInputFile(String inputFileName) throws InputParamsException {
        File inputFile = new File(inputFileName);
        if (!inputFile.exists()) {
            throw new InputParamsException(TYPE_ERROR, MESSAGE_ILLEGAL_INPUT_FILE);
        }
        return inputFile;
    }

    private static File prepareOutputFile(String outputFileName) throws InputParamsException {
        File outputFile = new File(outputFileName);
        if (!outputFile.exists()) {
            boolean creationSuccessful;
            try {
                creationSuccessful = outputFile.createNewFile();
            } catch (IOException e) {
                creationSuccessful = false;
            }
            if (!creationSuccessful) {
                throw new InputParamsException(TYPE_ERROR, MESSAGE_ILLEGAL_OUTPUT_FILE_CREATE);
            }
        }
        PurchasesListExceptionWrapperImpl.setOutputFile(outputFile);
        return outputFile;
    }
}
