package ru.adamdev.purchases.list.helper.impl;

import ru.adamdev.purchases.list.exception.PurchasesListException;
import ru.adamdev.purchases.list.function.ThrowableSupplier;
import ru.adamdev.purchases.list.helper.PurchasesListExceptionWrapper;
import ru.adamdev.purchases.list.model.ExceptionModel;
import ru.adamdev.purchases.list.util.FileUtil;
import ru.adamdev.purchases.list.util.JsonUtil;

import java.io.File;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static ru.adamdev.purchases.list.constant.ExceptionConstants.TYPE_ERROR;

public class PurchasesListExceptionWrapperImpl implements PurchasesListExceptionWrapper {

    private final static Logger LOGGER = Logger.getLogger(PurchasesListExceptionWrapper.class.getName());

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
        return consume(supplier, (result) -> LOGGER.info(JsonUtil.serialize(result)), true);
    }

    public <T, R,  E extends Exception> R consume(ThrowableSupplier<R, E> supplier, Consumer<Object> consumer, boolean isLogIfSuccess) {
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
            } else {
                exceptionModel.setMethodType(TYPE_ERROR);
            }
            exceptionModel.setMessage(e.getMessage());
            consumer.accept(exceptionModel);
        }
        return result;
    }
}
