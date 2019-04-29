package ru.hse.lyubortk.threadpool;

import org.jetbrains.annotations.NotNull;

public class LightExecutionException extends Exception {
    LightExecutionException(@NotNull Throwable cause) {
        super(cause);
    }
}
