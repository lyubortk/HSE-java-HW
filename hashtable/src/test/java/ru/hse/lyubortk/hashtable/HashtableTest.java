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
    void size() {
        assertEquals(0, table.size());
        for (int i = 0; i < 1000; i++) {
            table.put(Integer.toString(i), "a");
            assertEquals(i+1, table.size());
        }
        table.put("0","a");
        assertEquals(1000, table.size());
        table.remove("0");
        assertEquals(999, table.size());
    }

    @Test
    void contains() {
        assertFalse(table.contains("a"));
        table.put("a","b");
        assertTrue(table.contains("a"));
        assertFalse(table.contains("b"));
    }

    @Test
    void getInTable() {
        for (int i = 0; i < 1000; ++i) {
            table.put(Integer.toString(i),Integer.toString(i));
        }
        for (int i = 0; i < 1000; ++i) {
            assertEquals(Integer.toString(i), table.get(Integer.toString(i)));
        }
    }

    @Test
    void getNotInTable() {
        assertNull(table.get("a"));
    }

    @Test
    void put() {
        assertNull(table.put("a","b"));
        assertEquals("b",table.put("a","c"));
        assertEquals("c", table.put("a","d"));
    }

    @Test
    void putNull() {
        assertThrows(IllegalArgumentException.class, () -> {table.put(null, "a");});
        assertThrows(IllegalArgumentException.class, () -> {table.put("a", null);});

    }

    @Test
    void remove() {
        assertNull(table.remove("a"));
        table.put("a","b");
        assertEquals("b", table.remove("a"));
        assertNull(table.remove("a"));
        assertEquals(0, table.size());
        assertFalse(table.contains("a"));
    }

    @Test
    void clear() {
        for (int i = 0; i < 1000; ++i) {
            table.put(Integer.toString(i),Integer.toString(i));
        }
        table.clear();
        assertEquals(0, table.size());
    }

    private Hashtable table;
}