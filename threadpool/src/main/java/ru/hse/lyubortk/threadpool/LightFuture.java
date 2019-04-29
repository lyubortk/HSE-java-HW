package ru.hse.lyubortk.threadpool;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface LightFuture<T> {

    boolean isReady();

    T get() throws InterruptedException, LightExecutionException;

    <S> LightFuture<S> thenApply(@NotNull Function<? super T, ? extends S> function);

}
