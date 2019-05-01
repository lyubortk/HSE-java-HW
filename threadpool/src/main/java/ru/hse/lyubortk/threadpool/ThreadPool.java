package ru.hse.lyubortk.threadpool;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

/** A simple thread pool class with fixed number of threads */
public class ThreadPool {
    private volatile boolean isShutdown = false;
    private final Thread[] threads;
    private final BlockingQueue<Task<?>> taskQueue = new BlockingQueue<>();

    /**
     * Creates a new ThreadPool with given number of threads. The number of threads
     * has to be greater than zero.
     */
    public ThreadPool(int numberOfThreads) {
        if (numberOfThreads < 1) {
            throw new IllegalArgumentException();
        }

        threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(() -> {
                while (!isShutdown) {
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

    /**
     * Adds task to the ThreadPool. The task will be computed when any worker thread is free and
     * there is no other tasks submitted before this task.
     */
    public <T> LightFuture<T> execute(@NotNull Supplier<? extends T> supplier) {
        if (isShutdown) {
            throw new IllegalStateException();
        }

        var task = new Task<T>(supplier);
        taskQueue.put(task);
        return task;
    }

    /**
     * Initiates a shutdown of the ThreadPool.
     * Attempts to stop all currently executing tasks by interrupting worker threads.
     * No new tasks will be accepted after this operation.
     */
    public void shutdown() {
        isShutdown = true;
        for (var thread : threads) {
            thread.interrupt();
        }
    }

    private class Task<T> implements LightFuture<T>, Runnable {
        private volatile boolean isReady = false;
        private T result = null;
        private Throwable caughtThrowable = null;
        private final List<Task<?>> thenApplyTasksQueue = new ArrayList<>();
        private Supplier<? extends T> targetSupplier;

        private final Object mutex = new Object();

        private Task(@NotNull Supplier<? extends T> targetSupplier) {
            this.targetSupplier = targetSupplier;
        }

        /** {@inheritDoc} */
        @Override
        public void run() {
            try {
                result = targetSupplier.get();
            } catch (Throwable throwable) {
                caughtThrowable = throwable;
            }
            //prevents holding of LightFuture chain from thenApply method
            targetSupplier = null;
            isReady = true;

            synchronized (mutex) {
                mutex.notifyAll();
                thenApplyTasksQueue.forEach(taskQueue::put);
            }
        }

        /** {@inheritDoc} */
        @Override
        public boolean isReady() {
            return isReady;
        }

        /** {@inheritDoc} */
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

        /** {@inheritDoc} */
        @Override
        public <S> LightFuture<S> thenApply(@NotNull Function<? super T, ? extends S> function) {
            if (isShutdown) {
                throw new IllegalStateException();
            }

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
            notify();
        }

        private synchronized T take() throws InterruptedException {
            while (queue.size() == 0) {
                wait();
            }
            return queue.poll();
        }
    }
}
