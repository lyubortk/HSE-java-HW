package ru.hse.lyubortk.mytreeset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TreeTest {
    private MyTreeSet<Integer> tree;
    private final Integer[] numbersDict20 = {12, 16, 6, 2, 0, 10, 8, 7, 19, 14,
                                              9, 1, 5, 13, 17, 18, 11, 4, 3, 15};
    private final Integer[] numbersDict10 = {5, 13, 17, 9, 19, 3, 11, 1, 7, 15};

    @BeforeEach
    void initializeTree() {
        tree = new Tree<>();
    }

    @Test
    void iteratorEmpty() {
        Iterator<Integer> iterator = tree.iterator();
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void iteratorBasic() {
        tree.addAll(Arrays.asList(numbersDict20));
        var iterator = tree.iterator();
        for (int i = 0; i < 20; ++i) {
            assertTrue(iterator.hasNext());
            assertEquals(i, (int)iterator.next());
        }
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void iteratorInDescendingSet() {
        var descendingSet = tree.descendingSet();
        descendingSet.addAll(Arrays.asList(numbersDict20));
        var iterator = descendingSet.iterator();
        for (int i = 0; i < 20; ++i) {
            assertTrue(iterator.hasNext());
            assertEquals(19-i, (int)iterator.next());
        }
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void iteratorNoInvalidationAfterDummyAdd() {
        tree.addAll(Arrays.asList(numbersDict20));
        var iterator = tree.iterator();
        for (int i = 0; i < 5; ++i) {
            iterator.next();
        }
        tree.add(3);
        tree.add(5);
        for (int i = 5; i < 20; ++i) {
            assertDoesNotThrow(iterator::hasNext);
            assertEquals(i, (int)iterator.next());
        }
        assertDoesNotThrow(iterator::hasNext);
        assertFalse(iterator.hasNext());
    }

    @Test
    void iteratorInvalidation() {
        tree.addAll(Arrays.asList(numbersDict20));
        var iterator = tree.iterator();
        for (int i = 0; i < 5; ++i) {
            iterator.next();
        }
        tree.add(21);
        assertThrows(IllegalStateException.class, iterator::next);
    }

    @Test
    void descendingIteratorEmpty() {
        Iterator<Integer> iterator = tree.descendingIterator();
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void descendingIteratorBasic() {
        tree.addAll(Arrays.asList(numbersDict20));
        var iterator = tree.descendingIterator();
        for (int i = 0; i < 20; ++i) {
            assertTrue(iterator.hasNext());
            assertEquals(19-i, (int)iterator.next());
        }
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void descendingIteratorInDescendingSet() {
        var descendingSet = tree.descendingSet();
        descendingSet.addAll(Arrays.asList(numbersDict20));
        var iterator = descendingSet.descendingIterator();
        for (int i = 0; i < 20; ++i) {
            assertTrue(iterator.hasNext());
            assertEquals(i, (int)iterator.next());
        }
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void sizeBasic() {
        for(int i = 0; i < 20; ++i) {
            assertEquals(i, tree.size());
            tree.add(numbersDict20[i]);
        }
        assertEquals(20, tree.size());
    }

    @Test
    void sizeDoubleAdding() {
        for(int i = 0; i < 20; ++i) {
            assertEquals(i, tree.size());
            tree.add(numbersDict20[i]);
            assertEquals(i+1, tree.size());
            tree.add(numbersDict20[i]);
            assertEquals(i+1, tree.size());
        }
        assertEquals(20, tree.size());
    }

    @Test
    void sizeDescendingSet() {
        tree.addAll(Arrays.asList(numbersDict20));
        var descendingSet = tree.descendingSet();
        assertEquals(20, descendingSet.size());
    }

    @Test
    void sizeInCreatedDescendingSet() {
        var descendingSet = tree.descendingSet();
        for (int i = 0; i < 20; ++i) {
            assertEquals(i, tree.size());
            assertEquals(i, descendingSet.size());
            if ((i&1) == 0) {
                tree.add(numbersDict20[i]);
            } else {
                descendingSet.add(numbersDict20[i]);
            }
        }
    }

    @Test
    void addIncomparable() {
        var testTree = new Tree<>();
        assertDoesNotThrow(() -> testTree.add(0));
        assertThrows(ClassCastException.class, () -> testTree.add("23"));
    }

    @Test
    void addWithComparator() {
        var testTree = new Tree<>(Comparator.<Integer>comparingInt(o -> Math.abs(o - 10)));
        Collections.addAll(testTree, 17, 5, 16, 11, 10, 8);
        var iterator = testTree.iterator();

        assertEquals(10, (int)iterator.next());
        assertEquals(11, (int)iterator.next());
        assertEquals(8, (int)iterator.next());
        assertEquals(5, (int)iterator.next());
        assertEquals(16, (int)iterator.next());
        assertEquals(17, (int)iterator.next());
    }

    @Test
    void addSameValues() {
        assertTrue(tree.add(1));
        assertFalse(tree.add(1));
        assertFalse(tree.add(1));

        assertTrue(tree.add(5));
        assertFalse(tree.add(5));
        assertFalse(tree.add(5));

        assertTrue(tree.add(2));
        assertFalse(tree.add(2));
        assertFalse(tree.add(2));

        assertTrue(tree.add(3));
        assertFalse(tree.add(3));
        assertFalse(tree.add(3));

        assertTrue(tree.add(6));
        assertFalse(tree.add(6));
        assertFalse(tree.add(6));
    }

    @Test
    void removeBasic() {
        tree.addAll(Arrays.asList(numbersDict20));
        for (int i = 0; i < 20; ++i) {
            assertTrue(tree.remove(i));
            assertFalse(tree.remove(i));
            assertEquals(20 - i - 1, tree.size());
        }
    }

    @Test
    void removeDescending() {
        tree.addAll(Arrays.asList(numbersDict20));
        var descendingTree = tree.descendingSet();
        for (int i = 0; i < 20; ++i) {
            assertTrue(descendingTree.remove(i));
            assertFalse(descendingTree.remove(i));
            assertEquals(20 - i - 1, descendingTree.size());
            assertEquals(20 - i - 1, tree.size());
        }
    }

    @Test
    void firstBasic() {
        tree.addAll(Arrays.asList(numbersDict20));
        assertEquals(0, (int)tree.first());
    }

    @Test
    void firstEmpty() {
        assertThrows(NoSuchElementException.class, tree::first);
    }

    @Test
    void firstDescending() {
        var descendingSet = tree.descendingSet();
        descendingSet.addAll(Arrays.asList(numbersDict20));
        assertEquals(19, (int)descendingSet.first());
    }

    @Test
    void lastBasic() {
        tree.addAll(Arrays.asList(numbersDict20));
        assertEquals(19, (int)tree.last());
    }

    @Test
    void lastEmpty() {
        assertThrows(NoSuchElementException.class, tree::last);
    }

    @Test
    void lastDescending() {
        var descendingSet = tree.descendingSet();
        descendingSet.addAll(Arrays.asList(numbersDict20));
        assertEquals(0, (int)tree.first());
    }

    @Test
    void lower() {
        tree.addAll(Arrays.asList(numbersDict10));
        assertNull(tree.lower(1));
        for (int i = 2; i < 20; ++i) {
            assertEquals(i - 1 - i % 2, (int)tree.lower(i));
        }
        assertEquals(19, (int)tree.lower(50));
    }

    @Test
    void floor() {
        tree.addAll(Arrays.asList(numbersDict10));
        assertNull(tree.floor(0));
        for (int i = 1; i < 20; ++i) {
            assertEquals(i - (i + 1) % 2, (int)tree.floor(i));
        }
        assertEquals(19, (int)tree.floor(50));
    }

    @Test
    void ceiling() {
        tree.addAll(Arrays.asList(numbersDict10));
        assertEquals(1, (int)tree.ceiling(-10));
        for (int i = 1; i < 20; ++i) {
            assertEquals(i + (i + 1) % 2, (int)tree.ceiling(i));
        }
        assertNull(tree.ceiling(50));
    }

    @Test
    void ceilingDescending() {
        var descendingSet = tree.descendingSet();
        descendingSet.addAll(Arrays.asList(numbersDict10));
        assertNull(descendingSet.ceiling(0));
        for (int i = 1; i < 20; ++i) {
            assertEquals(i - (i + 1) % 2, (int)descendingSet.ceiling(i));
        }
        assertEquals(19, (int)descendingSet.ceiling(50));
    }

    @Test
    void higher() {
        tree.addAll(Arrays.asList(numbersDict10));
        assertEquals(1, (int)tree.higher(-10));
        for (int i = 1; i < 19; ++i) {
            assertEquals(i + 1 + i % 2, (int)tree.higher(i));
        }
        assertNull(tree.higher(50));
    }

    @Test
    void higherDescending() {
        var descendingSet = tree.descendingSet();
        descendingSet.addAll(Arrays.asList(numbersDict10));
        assertNull(descendingSet.higher(1));
        for (int i = 2; i < 20; ++i) {
            assertEquals(i - 1 - i % 2, (int)descendingSet.higher(i));
        }
        assertEquals(19, (int)descendingSet.higher(50));
    }
}