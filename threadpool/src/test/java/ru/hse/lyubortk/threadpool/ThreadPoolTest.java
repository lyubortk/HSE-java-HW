package ru.hse.lyubortk.threadpool;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolTest {
    private static final int THREAD_POOL_SIZE = 30;
    private final ThreadPool threadPool = new ThreadPool(THREAD_POOL_SIZE);

    @RepeatedTest(10)
    void testNumberOfThreads() throws InterruptedException {
        AtomicInteger value = new AtomicInteger(0);
        for (int i = 0; i < THREAD_POOL_SIZE*10; i++) {
            threadPool.addTask(() -> {
                value.incrementAndGet();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
                return null;
            });
        }
        Thread.sleep(300);
        assertEquals(THREAD_POOL_SIZE, value.get());
        threadPool.shutDown();
    }
}