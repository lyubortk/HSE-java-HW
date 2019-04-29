package ru.hse.lyubortk.threadpool;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadPool {
    private volatile boolean wasShutdown = false;
    private final Thread[] threads;
    private final BlockingQueue<Task<?>> taskQueue = new BlockingQueue<>();

    public ThreadPool(int numberOfThreads) {
        threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(() -> {
                while (!wasShutdown) {
                    try {
                        var task = taskQueue.take();
                        task.run();
                    } catch (InterruptedException ignored) {
                    }
                }
            });
            threads[i].start();
        }
    }

    public <T> LightFuture<T> addTask(@NotNull Supplier<T> supplier) {
        var task = new Task<>(supplier);
        taskQueue.put(task);
        return task;
    }

    public void shutDown() {
        wasShutdown = true;
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    private class Task<T> implements LightFuture<T>, Runnable {
        private volatile boolean isReady = false;
        private T result = null;
        private final List<Task<?>> thenApplyTasksQueue = new ArrayList<>();
        private final Supplier<T> targetSupplier;
        private Throwable caughtThrowable = null;

        private final Object mutex = new Object();

        private Task(@NotNull Supplier<T> targetSupplier) {
            this.targetSupplier = targetSupplier;
        }

        @Override
        public void run() {
            try {
                result = targetSupplier.get();
            } catch (Throwable throwable) {
                caughtThrowable = throwable;
            }
            isReady = true;

            synchronized (mutex) {
                mutex.notifyAll();
                thenApplyTasksQueue.forEach(taskQueue::put);
            }
        }

        @Override
        public boolean isReady() {
            return isReady;
        }

        @Override
        public T get() throws InterruptedException, LightExecutionException {
            synchronized (mutex) {
                while (!isReady) {
                    mutex.wait();
                }
            }
            if (caughtThrowable != null) {
                throw new LightExecutionException(caughtThrowable);
            }
            return result;
        }

        @Override
        public <S> LightFuture<S> thenApply(@NotNull Function<? super T, ? extends S> function) {
            var task = new Task<S>(() -> {
                if (caughtThrowable != null) {
                    throw new RuntimeException(caughtThrowable);
                }
                return function.apply(result);
            });

            synchronized (mutex) {
                if (isReady) {
                    taskQueue.put(task);
                } else {
                    thenApplyTasksQueue.add(task);
                }
            }

            return task;
        }
    }

    private static class BlockingQueue<T> {
        private Queue<T> queue = new LinkedList<>();

        private synchronized void put(@Nullable T value) {
            queue.offer(value);
        }

        private synchronized T take() throws InterruptedException {
            while (queue.size() == 0) {
                wait();
            }
            return queue.poll();
        }

        private synchronized int size() {
            return queue.size();
        }
    }
}
