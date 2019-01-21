package ru.hse.lyubortk.hashtable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashtableTest {

    @Test
    void size() {
        Hashtable table = new Hashtable();
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
        Hashtable table = new Hashtable();
        assertFalse(table.contains("a"));
        table.put("a","b");
        assertTrue(table.contains("a"));
        assertFalse(table.contains("b"));
    }

    @Test
    void getInTable() {
        Hashtable table = new Hashtable();
        for (int i = 0; i < 1000; ++i) {
            table.put(Integer.toString(i),Integer.toString(i));
        }
        for (int i = 0; i < 1000; ++i) {
            assertEquals(Integer.toString(i), table.get(Integer.toString(i)));
        }
    }

    @Test
    void getNotInTable() {
        Hashtable table = new Hashtable();
        assertNull(table.get("a"));
    }

    @Test
    void put() {
        Hashtable table = new Hashtable();
        assertNull(table.put("a","b"));
        assertEquals("b",table.put("a","c"));
        assertEquals("c", table.put("a","d"));
    }

    @Test
    void putNull() {
        Hashtable table = new Hashtable();
        assertThrows(IllegalArgumentException.class, () -> {table.put(null, "a");});
        assertThrows(IllegalArgumentException.class, () -> {table.put("a", null);});

    }

    @Test
    void remove() {
        Hashtable table = new Hashtable();
        assertNull(table.remove("a"));
        table.put("a","b");
        assertEquals("b", table.remove("a"));
        assertNull(table.remove("a"));
        assertEquals(0, table.size());
        assertFalse(table.contains("a"));
    }

    @Test
    void clear() {
        Hashtable table = new Hashtable();
        for (int i = 0; i < 1000; ++i) {
            table.put(Integer.toString(i),Integer.toString(i));
        }
        table.clear();
        assertEquals(0, table.size());
    }
}