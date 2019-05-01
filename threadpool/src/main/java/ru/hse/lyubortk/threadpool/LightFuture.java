package ru.hse.lyubortk.threadpool;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * This interface represents the result of asynchronous computation which is being done by
 * {@link ThreadPool}.
 * @param <T> resulting type
 */
public interface LightFuture<T> {
    /** Checks whether the computation is done. */
    boolean isReady();

    /**
     * Waits until the computation is done and then returns the result.
     *
     * @throws InterruptedException if the current thread is interrupted while waiting for
     * the result of computation.
     * @throws LightExecutionException if the corresponding computation finished with an exception
     */
    T get() throws InterruptedException, LightExecutionException;

    /**
     * Applies function to the result of computation in a new task which is added to
     * the corresponding ThreadPool. The new task will not occupy a free thread if
     * the result is not yet computed.
     */
    <S> LightFuture<S> thenApply(@NotNull Function<? super T, ? extends S> function);
}
