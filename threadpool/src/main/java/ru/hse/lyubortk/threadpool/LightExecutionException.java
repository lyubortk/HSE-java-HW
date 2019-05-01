package ru.hse.lyubortk.threadpool;

import org.jetbrains.annotations.NotNull;

/**
 * This exception is thrown by {@link LightFuture#get} to indicate
 * that corresponding computation finished with an exception.
 */
public class LightExecutionException extends Exception {
    LightExecutionException(@NotNull Throwable cause) {
        super(cause);
    }
}
