package ru.adamdev.purchases.list.function;

@FunctionalInterface
public interface ThrowableFunction<T, R, E extends Throwable> {

    R apply(T t) throws E;
}
