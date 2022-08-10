package ru.adamdev.purchases.list.helper.impl;

import org.jetbrains.annotations.Nullable;
import lombok.Setter;
import org.apache.log4j.Logger;
import ru.adamdev.purchases.list.exception.PurchasesListException;
import ru.adamdev.purchases.list.function.ThrowableSupplier;
import ru.adamdev.purchases.list.helper.PurchasesListExceptionWrapper;
import ru.adamdev.purchases.list.model.ExceptionModel;
import ru.adamdev.purchases.list.util.FileUtil;
import ru.adamdev.purchases.list.util.JsonUtil;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;

import static ru.adamdev.purchases.list.constant.ErrorConstants.TYPE_ERROR;

public class PurchasesListExceptionWrapperImpl implements PurchasesListExceptionWrapper {

    private final static Logger LOGGER = Logger.getLogger(PurchasesListExceptionWrapper.class);

    @Setter
    private static File outputFile;

    @Override
    public <R, E extends Exception> R wrap(ThrowableSupplier<R, E> supplier, File outputFile, boolean isLogIfSuccess) {
        return consume(supplier, (result) -> FileUtil.writeToFile(result, outputFile), isLogIfSuccess);
    }

    @Override
    public <R, E extends Exception> R wrap(ThrowableSupplier<R, E> supplier, File outputFile) {
        return consume(supplier, (result) -> FileUtil.writeToFile(result, outputFile), true);
    }

    @Override
    public <R, E extends Exception> R wrap(ThrowableSupplier<R, E> supplier) {
        return consume(supplier, this::log, true);
    }

    private void log(Object o) {
        if (outputFile != null) {
            FileUtil.writeToFile(o, outputFile);
        } else {
            LOGGER.info(JsonUtil.serialize(o));
        }
    }

    private void logCause(Exception e) {
        Optional.ofNullable(e).ifPresent(t -> LOGGER.error(t.getMessage()));
    }

    @Nullable
    private <R, E extends Exception> R consume(ThrowableSupplier<R, E> supplier, Consumer<Object> consumer, boolean isLogIfSuccess) {
        R result = null;
        try {
            result = supplier.get();
            if (isLogIfSuccess) {
                consumer.accept(result);
            }
        } catch (Exception e) {
            ExceptionModel exceptionModel = new ExceptionModel();
            if (e instanceof PurchasesListException) {
                exceptionModel.setMethodType(((PurchasesListException) e).getType());
                logCause(e);
            } else {
                exceptionModel.setMethodType(TYPE_ERROR);
            }
            exceptionModel.setMessage(e.getMessage());
            consumer.accept(exceptionModel);
        }
        return result;
    }
}
