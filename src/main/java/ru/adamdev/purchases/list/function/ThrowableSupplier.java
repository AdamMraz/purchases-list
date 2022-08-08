package ru.adamdev.purchases.list.function;

@FunctionalInterface
public interface ThrowableSupplier<R, E extends Throwable> {

    R get() throws E;
}
