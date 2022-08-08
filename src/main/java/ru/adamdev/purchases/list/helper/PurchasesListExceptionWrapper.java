package ru.adamdev.purchases.list.helper;

import ru.adamdev.purchases.list.function.ThrowableSupplier;

import java.io.File;

public interface PurchasesListExceptionWrapper {

    <R, E extends Exception> R wrap(ThrowableSupplier<R, E> supplier, File outputFile, boolean isLogIfSuccess);

    <R, E extends Exception> R wrap(ThrowableSupplier<R, E> supplier, File outputFile);

    <R, E extends Exception> R wrap(ThrowableSupplier<R, E> supplier);
}
