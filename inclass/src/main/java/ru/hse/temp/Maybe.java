package ru.hse.temp;

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.function.Function;

public class Maybe<T> {
    private T value;

    private Maybe() {}

    private void set(T value) {
        this.value = value;
    }

    public static <T> Maybe<T> just(@NotNull T t) {
        var mb = new Maybe<T>();
        mb.set(t);
        return mb;
    }

    public static <T> Maybe<T> nothing() {
        var mb = new Maybe<T>();
        mb.set(null);
        return mb;
    }

    public @NotNull T get() {
        if (value == null) {
            throw new NoSuchElementException("Trying to get null.");
        }
        return value;
    }

    public boolean isPresent() {
        return value != null;
    }

    public <U> Maybe<U> map(Function<? super T, ? extends U> mapper) {
        if (value == null) {
            return nothing();
        }
        return just(mapper.apply(value));
    }
}
