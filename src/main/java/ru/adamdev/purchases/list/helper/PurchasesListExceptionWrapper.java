package ru.adamdev.purchases.list.helper;

import org.jetbrains.annotations.Nullable;
import ru.adamdev.purchases.list.function.ThrowableSupplier;

import java.io.File;

public interface PurchasesListExceptionWrapper {

    @Nullable
    <R, E extends Exception> R wrap(ThrowableSupplier<R, E> supplier, File outputFile, boolean isLogIfSuccess);

    @Nullable
    <R, E extends Exception> R wrap(ThrowableSupplier<R, E> supplier, File outputFile);

    @Nullable
    <R, E extends Exception> R wrap(ThrowableSupplier<R, E> supplier);
}
