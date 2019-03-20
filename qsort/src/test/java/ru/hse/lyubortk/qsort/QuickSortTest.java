package ru.hse.lyubortk.qsort;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class QuickSortTest {
    @Test
    void testSortsSmall1() {
        var list = new ArrayList<>(Arrays.asList(2, 2, 2, 2, 1, 1, 1, 1, 1));
        QuickSort.sort(list);
        assertTrue(isSorted(list));
    }

    @Test
    void testSortsSmall2() {
        var list = new ArrayList<>(Arrays.asList(10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, -1));
        QuickSort.sort(list);
        assertTrue(isSorted(list));
    }

    @Test
    void testSortsSmall3() {
        var list = new ArrayList<>(Arrays.asList(10, 4, 7, 9, 2, 8, 3, 2, 1, 1, 7, 4, 2, 1));
        QuickSort.sort(list);
        assertTrue(isSorted(list));
    }

    @Test
    void testSortsLinkedList() {
        var list = new LinkedList<>(Arrays.asList(10, 4, 7, 9, 2, 8, 3, 2, 1, 1, 7, 4, 2, 1));
        QuickSort.sort(list);
        assertTrue(isSorted(list));
    }

    @Test
    void testSortsBig1() {
        var rand = new Random(1);
        var list = new ArrayList<Integer>();
        for (int i = 0; i < 100_000; i++) {
            list.add(rand.nextInt());
        }

        QuickSort.sort(list);
        assertTrue(isSorted(list));
    }

    @Test
    void testSortsBig2() {
        var rand = new Random(1);
        var list = new ArrayList<Integer>();
        for (int i = 0; i < 100_000; i++) {
            list.add(rand.nextInt(50));
        }

        QuickSort.sort(list);
        assertTrue(isSorted(list));
    }

    private static boolean isSorted(List<Integer> list) {
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i-1) > list.get(i)) {
                return false;
            }
        }
        return true;
    }
}