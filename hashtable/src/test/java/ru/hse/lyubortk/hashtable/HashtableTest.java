package ru.hse.lyubortk.hashtable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashtableTest {

    @BeforeEach
    void initializeHashtable() {
        table = new Hashtable();
    }

    @Test
    void sizeAfterPut() {
        assertEquals(0, table.size());
        for (int i = 0; i < 1000; i++) {
            table.put(Integer.toString(i), "a");
            assertEquals(i+1, table.size());
        }
    }

    @Test
    void sizeAfterRemove() {
        putThousandIntegers();
        for (int i = 0; i < 1000; i++) {
            assertEquals(1000 - i, table.size());
            table.remove(Integer.toString(i));
        }
        assertEquals(0, table.size());
    }

    @Test
    void containsInTable() {
        putThousandIntegers();
        for (int i = 0; i < 1000; i++) {
            assertTrue(table.contains(Integer.toString(i)));
        }
    }

    @Test
    void containsNotInTable() {
        assertFalse(table.contains("a"));
    }

    @Test
    void containsNull() {
        assertThrows(IllegalArgumentException.class, () -> table.contains(null));
    }

    @Test
    void getInTable() {
        putThousandIntegers();
        for (int i = 0; i < 1000; ++i) {
            assertEquals(Integer.toString(i + 1000), table.get(Integer.toString(i)));
        }
    }

    @Test
    void getNotInTable() {
        assertNull(table.get("a"));
    }

    @Test
    void getNull() {
        assertThrows(IllegalArgumentException.class, () -> table.get(null));
    }

    @Test
    void putWithoutHashCollisions() {
        assertNull(table.put("a","b"));
        assertEquals("b",table.put("a","c"));
        assertEquals("c", table.put("a","d"));
    }

    @Test
    void putWithHashCollisions() {
        assertNull(table.put("Siblings", "a"));
        assertNull(table.put("Teheran", "b"));
    }

    @Test
    void putNull() {
        assertThrows(IllegalArgumentException.class, () -> table.put(null, "a"));
        assertThrows(IllegalArgumentException.class, () -> table.put("a", null));

    }

    @Test
    void removeWithoutHashCollisions() {
        putThousandIntegers();
        for(int i = 0; i < 1000; i++) {
            assertEquals(Integer.toString(i + 1000), table.remove(Integer.toString(i)));
            assertNull(table.remove(Integer.toString(i)));
        }
    }

    @Test
    void removeWithHashCollisions() {
        table.put("Siblings", "a");
        table.put("Teheran", "b");
        assertEquals("a", table.remove("Siblings"));
        assertEquals("b", table.remove("Teheran"));
    }


    @Test
    void removeNull() {
        assertThrows(IllegalArgumentException.class, () -> table.remove(null));
    }

    @Test
    void clear() {
        putThousandIntegers();
        table.clear();
        assertEquals(0, table.size());
    }

    private void putThousandIntegers() {
        for (int i = 0; i < 1000; ++i) {
            table.put(Integer.toString(i),Integer.toString(i + 1000));
        }
    }

    private Hashtable table;
}