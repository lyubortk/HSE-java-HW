package ru.hse.lyubortk.test3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HashMapTest {
    private HashMap<String, String> table;

    @BeforeEach
    void initializeHashtable() {
        table = new HashMap<>();
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
    void sizeAfterDoublePut() {
        putThousandIntegers();
        putThousandIntegers();
        assertEquals(1000, table.size());
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
            assertTrue(table.containsKey(Integer.toString(i)));
        }
    }

    @Test
    void containsNotInTable() {
        assertFalse(table.containsKey("a"));
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
    void clear() {
        putThousandIntegers();
        table.clear();
        assertEquals(0, table.size());
    }

    @Test
    void orderSimpleTest() {
        putThousandIntegers();
        int i = 0;
        for (var iterator = table.entrySet().iterator(); iterator.hasNext(); i++) {
            Map.Entry<String, String> entry = iterator.next();
            assertEquals(Integer.toString(i), entry.getKey());
            assertEquals(Integer.toString(i + 1000), entry.getValue());
        }
    }
    

    private void putThousandIntegers() {
        for (int i = 0; i < 1000; ++i) {
            table.put(Integer.toString(i),Integer.toString(i + 1000));
        }
    }
}