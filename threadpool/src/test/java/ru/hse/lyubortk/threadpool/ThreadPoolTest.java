package ru.hse.lyubortk.threadpool;

import org.junit.jupiter.api.RepeatedTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolTest {
    private static final int REPEATED_TEST_NUMBER = 10;
    private static final int BASE_THREAD_POOL_SIZE = 20;
    private final ThreadPool threadPool = new ThreadPool(BASE_THREAD_POOL_SIZE);

    @RepeatedTest(REPEATED_TEST_NUMBER)
    void testNumberOfThreads() throws InterruptedException {
        var value = new AtomicInteger(0);
        for (int i = 0; i < BASE_THREAD_POOL_SIZE * 10; i++) {
            threadPool.execute(() -> {
                value.incrementAndGet();
                try {
                    Thread.sleep(Long.MAX_VALUE);
                } catch (InterruptedException ignored) {
                }
                return null;
            });
        }
        Thread.sleep(300);
        assertEquals(BASE_THREAD_POOL_SIZE, value.get());
        threadPool.shutdown();
    }

    @RepeatedTest(REPEATED_TEST_NUMBER)
    void testOrderOfTasks() throws LightExecutionException, InterruptedException {
        var lastValue = new AtomicInteger(-1);
        var currentThreadPool = new ThreadPool(1);
        var lightFutureList = new ArrayList<LightFuture<?>>();

        for (int i = 0; i < 30; i++) {
            int k = i;
            var lightFuture = currentThreadPool.execute(() -> {
                assertEquals(k - 1, lastValue.get());
                lastValue.incrementAndGet();
                return null;
            });
            lightFutureList.add(lightFuture);
        }

        for (var lightFuture : lightFutureList) {
            lightFuture.get();
        }

    }

    @RepeatedTest(REPEATED_TEST_NUMBER)
    void testExecuteManyTasks() throws LightExecutionException, InterruptedException {
        final int numberOfTasks = BASE_THREAD_POOL_SIZE * 30;
        var results = new int[numberOfTasks];
        var lightFutureList = new ArrayList<LightFuture<?>>();

        Arrays.fill(results, 0);

        for (int i = 0; i < numberOfTasks; i++) {
            int k = i;
            var lightFuture = threadPool.execute(() -> {
                results[k] = k;
                return k;
            });
            lightFutureList.add(lightFuture);
        }

        for (int i = 0; i < numberOfTasks; i++) {
            assertEquals(i, lightFutureList.get(i).get());
            assertEquals(i, results[i]);
        }
    }

    @RepeatedTest(REPEATED_TEST_NUMBER)
    void testThenApplyOneChain() throws LightExecutionException, InterruptedException {
        var lightFuture = threadPool.execute(() -> 0);
        for (int i = 0; i < 100; i++) {
            lightFuture = lightFuture.thenApply((a) -> a + 1);
        }
        assertEquals(100, (int)lightFuture.get());
    }

    @RepeatedTest(REPEATED_TEST_NUMBER)
    void testThenApplyManyChains() throws LightExecutionException, InterruptedException {
        final int numberOfTasks = BASE_THREAD_POOL_SIZE;
        var lightFutureList = new ArrayList<LightFuture<Integer>>();

        for (int i = 0; i < numberOfTasks; i++) {
            int k = i;
            var lightFuture = threadPool.execute(() -> k * 50);
            lightFutureList.add(lightFuture);
        }

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < numberOfTasks; j++) {
                lightFutureList.set(j, lightFutureList.get(j).thenApply((a) -> {
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException ignored) {
                    }
                    return a + 1;
                }));
            }
        }

        for (int i = 0; i < numberOfTasks; i++) {
            assertEquals(i * 50 + 10, (int)lightFutureList.get(i).get());
        }
    }


    @RepeatedTest(REPEATED_TEST_NUMBER)
    void testGetWaits() throws LightExecutionException, InterruptedException {
        var lightFuture = threadPool.execute(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException ignored) {
            }
            return "test";
        });

        assertFalse(lightFuture.isReady());
        assertEquals("test", lightFuture.get());
    }

    @RepeatedTest(REPEATED_TEST_NUMBER)
    void testThenApplyDoesNotBlock() {
        var finished = new AtomicBoolean(false);
        var supplier = new Supplier<>() {
            @Override
            public Object get() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ignored) {
                }
                finished.set(true);
                return null;
            }
        };

        var lightFuture = threadPool.execute(supplier);
        lightFuture = lightFuture.thenApply((a) -> supplier.get());
        lightFuture = lightFuture.thenApply((a) -> supplier.get());
        lightFuture = lightFuture.thenApply((a) -> supplier.get());

        assertFalse(finished.get());
    }

    @RepeatedTest(REPEATED_TEST_NUMBER)
    void testBasicLightExecutionException() {
        var lightFuture = threadPool.execute(() -> {
            throw new IllegalArgumentException();
        });

        var exception = assertThrows(LightExecutionException.class, lightFuture::get);
        assertEquals(IllegalArgumentException.class, exception.getCause().getClass());
    }

    @RepeatedTest(REPEATED_TEST_NUMBER)
    void testThenApplyLightExecutionException() {
        var lightFuture = threadPool.execute(() -> {
            throw new IllegalArgumentException();
        });

        lightFuture = lightFuture.thenApply((a) -> a);
        lightFuture = lightFuture.thenApply((a) -> a);
        final var finalFuture = lightFuture;
        assertThrows(LightExecutionException.class, finalFuture::get);
    }

    @RepeatedTest(REPEATED_TEST_NUMBER)
    void testExecuteAfterShutdown() throws InterruptedException {
        threadPool.shutdown();
        assertThrows(IllegalStateException.class, () -> threadPool.execute(() -> null));
    }

    @RepeatedTest(REPEATED_TEST_NUMBER)
    void testThenApplyAfterShutdown() throws InterruptedException {
        var lightFuture = threadPool.execute(() -> null);
        threadPool.shutdown();
        assertThrows(IllegalStateException.class, () -> lightFuture.thenApply((a) -> a));

    }
}