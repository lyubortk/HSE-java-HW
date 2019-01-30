package ru.hse.temp;

import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.function.Function;


public class Maybe<T> {
    private T value;
    private boolean isNothing;

    private Maybe() {}

    public static <T> Maybe<T> just(@Nullable T t) {
        var mb = new Maybe<T>();
        mb.value = t;
        mb.isNothing = false;
        return mb;
    }

    public static <T> Maybe<T> nothing() {
        var mb = new Maybe<T>();
        mb.isNothing = true;
        return mb;
    }

    public @Nullable T get() {
        if (isNothing) {
            throw new NoSuchElementException("Trying to get null.");
        }
        return value;
    }

    public boolean isPresent() {
        return !isNothing;
    }

    public <U> Maybe<U> map(Function<? super T, ? extends U> mapper) {
        if (isNothing) {
            return nothing();
        }
        return just(mapper.apply(value));
    }
}
