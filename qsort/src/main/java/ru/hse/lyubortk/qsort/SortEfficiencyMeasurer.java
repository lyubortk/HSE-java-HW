package ru.hse.lyubortk.qsort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/** This class compares efficiency of {@link QuickSort#sort} method and {@link Collections#sort}. */
public class SortEfficiencyMeasurer {
    private static final Random random = new Random();

    /** Sorts arrays of different sizes with two sorting methods and prints their execution time */
    public static void main(String[] args) {
        for (int i = 100; i <= 10_000_000;
             i *= (Integer.valueOf(i).toString().contains("5") ? 2 : 5)) {
            var list1 = new ArrayList<Integer>();
            for (int j = 0; j < i; ++j) {
                list1.add(random.nextInt());
            }
            var list2 = new ArrayList<>(list1);

            System.out.println("Running tests for " + i + " elements");
            long quickSortTime = getExecutionTime(() -> QuickSort.sort(list1));
            long collectionsSortTime = getExecutionTime(() -> Collections.sort(list2));

            System.out.println("Multi-thread QuickSort execution time: " + quickSortTime + " ms");
            System.out.println("Collections::sort execution time: " + collectionsSortTime + " ms");
            if (quickSortTime > collectionsSortTime) {
                System.out.println("Collections::sort is faster");
            } else {
                System.out.println("Multi-thread QuickSort is faster");
            }
            System.out.println();
        }
    }

    private static long getExecutionTime(Runnable function) {
        long startTime = System.currentTimeMillis();
        function.run();
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
}
