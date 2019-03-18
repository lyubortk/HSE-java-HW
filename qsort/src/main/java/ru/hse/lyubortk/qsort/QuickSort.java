package ru.hse.lyubortk.qsort;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class provides multi-thread quick sort algorithm for integer values.
 * Uses insertion sort if list which is being sorted is small enough.
 */
public class QuickSort {
    /**
     * Sorts given Integer list with multi-thread quick sort algorithm. {@link ArrayList} is sorted
     * in-place. For any other implementation of {@link List} interface its elements
     * are copied into temporary {@link ArrayList}, sorted there and then copied back into
     * original list.
     */
    public static void sort(@NotNull List<Integer> list) {
        boolean isArrayList = list.getClass() == ArrayList.class;

        ArrayList<Integer> listToSort;
        if (isArrayList) {
            listToSort = (ArrayList<Integer>)list;
        } else {
            listToSort = new ArrayList<>(list);
        }

        int numberOfCores = Runtime.getRuntime().availableProcessors();
        ExecutorService threadPool = Executors.newFixedThreadPool(numberOfCores);

        var numberOfSortedElements = new AtomicInteger();
        threadPool.submit(new SortTask(threadPool, listToSort, numberOfSortedElements));

        synchronized (numberOfSortedElements) {
            while (numberOfSortedElements.get() != list.size()) {
                try {
                    numberOfSortedElements.wait();
                } catch (InterruptedException ignored) {
                }
            }
        }

        if (!isArrayList) {
            list.clear();
            list.addAll(listToSort);
        }
    }

    private static class SortTask implements Runnable {
        private static final @NotNull Random RANDOM = new Random();
        private final @NotNull ExecutorService threadPool;
        private final @NotNull List<Integer> listToSort;
        private final @NotNull AtomicInteger numberOfSortedElements;

        private SortTask(@NotNull ExecutorService threadPool, @NotNull List<Integer> listToSort,
                         @NotNull AtomicInteger numberOfSortedElements) {
            this.threadPool = threadPool;
            this.listToSort = listToSort;
            this.numberOfSortedElements = numberOfSortedElements;
        }

        @Override
        public void run() {
            if (listToSort.size() < 40) {
                insertionSort(listToSort);
                synchronized (numberOfSortedElements) {
                    numberOfSortedElements.addAndGet(listToSort.size());
                    numberOfSortedElements.notifyAll();
                }
                return;
            }

            int middle = partition(listToSort);
            threadPool.submit(new SortTask(threadPool,
                    listToSort.subList(0, middle), numberOfSortedElements));
            threadPool.submit(new SortTask(threadPool,
                    listToSort.subList(middle, listToSort.size()), numberOfSortedElements));
        }

        private static void insertionSort(List<Integer> list) {
            for (int i = 1; i < list.size(); i++) {
                int value = list.get(i);
                int j;
                for (j = i; j > 0 && list.get(j-1) > value; j--) {
                    list.set(j, list.get(j-1));
                }
                list.set(j, value);
            }
        }

        private static int partition(List<Integer> list) {
            int pivot = list.get(RANDOM.nextInt(list.size()));
            int left = 0;
            int right = list.size() - 1;

            while (left <= right) {
                while (list.get(left) < pivot) {
                    left++;
                }
                while (list.get(right) > pivot) {
                    right--;
                }
                if (left <= right) {
                    Collections.swap(list, left, right);
                    left++;
                    right--;
                }
            }
            return right+1;
         }
    }
}
