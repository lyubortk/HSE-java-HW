package ru.hse.lyubortk.trie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TrieTest {
    private Trie trie;
    private static final int DICT_SIZE = 1000;
    private static final String[] DICT = getDictionary(DICT_SIZE);

    @BeforeEach
    void initializeTrie() {
        trie = new Trie();
    }

    @Test
    void addEmpty() {
        assertTrue(trie.add(""));
    }

    @Test
    void addNew() {
        for (var str : DICT) {
            assertTrue(trie.add(str));
        }
    }

    @Test
    void addAlreadyAdded() {
        for (var str : DICT) {
            trie.add(str);
        }
        for (var str : DICT) {
            assertFalse(trie.add(str));
        }
    }

    @Test
    void addPrefix() {
        assertTrue(trie.add("test"));
        assertTrue(trie.add("tes"));
        assertTrue(trie.add("te"));
        assertTrue(trie.add("t"));
        assertTrue(trie.add(""));
    }

    @Test
    void addWithSamePrefix() {
        assertTrue(trie.add("test"));
        assertTrue(trie.add("tea"));
    }

    @Test
    void addNull() {
        assertThrows(IllegalArgumentException.class, () -> trie.add(null));
    }

    @Test
    void containsEmpty() {
        assertFalse(trie.contains(""));
        trie.add("");
        assertTrue(trie.contains(""));
    }

    @Test
    void containsNotAdded() {
        for (var str : DICT) {
            assertFalse(trie.contains(str));
        }
    }

    @Test
    void containsPrefixes() {
        trie.add("test");
        assertTrue(trie.contains("test"));
        assertFalse(trie.contains("tes"));
        assertFalse(trie.contains("te"));
        assertFalse(trie.contains("t"));
        assertFalse(trie.contains(""));
    }

    @Test
    void containsAdded() {
        for (var str : DICT) {
            trie.add(str);
        }
        for (var str : DICT) {
            assertTrue(trie.contains(str));
        }
    }

    @Test
    void containsNull() {
        assertThrows(IllegalArgumentException.class, () -> trie.contains(null));
    }

    @Test
    void removeNotAdded() {
        for(var str: DICT) {
            assertFalse(trie.remove(str));
        }
    }

    @Test
    void removePrefix() {
        trie.add("test");
        assertFalse(trie.remove(""));
        assertFalse(trie.remove("t"));
        assertFalse(trie.remove("te"));
        assertFalse(trie.remove("tes"));
        assertTrue(trie.contains("test"));
        assertTrue(trie.remove("test"));
        assertFalse(trie.remove("test"));
    }

    @Test
    void removeWithSamePrefix() {
        trie.add("test");
        trie.add("tea");
        assertTrue(trie.remove("test"));
        assertFalse(trie.contains("test"));
        assertTrue(trie.contains("tea"));
        assertTrue(trie.remove("tea"));
        assertFalse(trie.remove("tea"));
    }

    @Test
    void removeNull() {
        assertThrows(IllegalArgumentException.class, () -> trie.remove(null));
    }

    @Test
    void size() {
        for (int i = 0; i < DICT_SIZE; i++) {
            assertEquals(i, trie.size());
            trie.add(DICT[i]);
        }
        assertEquals(DICT_SIZE, trie.size());
    }

    @Test
    void sizeDoubleAdding() {
        for (var str : DICT) {
            trie.add(str);
        }
        for (var str : DICT) {
            trie.add(str);
        }
        assertEquals(1000, trie.size());
    }

    @Test
    void sizeEmpty() {
        assertEquals(0, trie.size());
        trie.add("");
        assertEquals(1, trie.size());
    }

    @Test
    void howManyStartWithPrefix() {
        String[] dict = {"to", "tea", "ted", "ten", "inn", "in"};
        for (var str : dict) {
            trie.add(str);
        }
        assertEquals(4, trie.howManyStartWithPrefix("t"));
        assertEquals(3, trie.howManyStartWithPrefix("te"));
        assertEquals(1, trie.howManyStartWithPrefix("ten"));
        assertEquals(1, trie.howManyStartWithPrefix("inn"));
        assertEquals(2, trie.howManyStartWithPrefix("i"));
        assertEquals(2, trie.howManyStartWithPrefix("in"));
        assertEquals(6, trie.howManyStartWithPrefix(""));
        assertEquals(0, trie.howManyStartWithPrefix("a"));
    }

    @Test
    void serializeContainsContent() {
        for (var str: DICT) {
            trie.add(str);
        }

        var anotherTrie = new Trie();
        sendData(trie, anotherTrie);

        for (var str: DICT) {
            assertTrue(anotherTrie.contains(str));
        }

        assertEquals(DICT_SIZE, anotherTrie.size());
    }

    @Test
    void serializeDoesNotContainOldValues() {
        trie.add("Test String");
        var anotherTrie = new Trie();
        anotherTrie.add("Old String");
        anotherTrie.add("Another Old String");

        sendData(trie, anotherTrie);

        assertFalse(anotherTrie.contains("Old String"));
        assertFalse(anotherTrie.contains("Another Old String"));
        assertTrue(anotherTrie.contains("Test String"));
    }

    @Test
    void serializeFromEmptyTrie() {
        var anotherTrie = new Trie();
        for (var str : DICT) {
            anotherTrie.add(str);
        }
        assertEquals(DICT_SIZE, anotherTrie.size());

        sendData(trie, anotherTrie);

        assertEquals(0, anotherTrie.size());
        for(var str : DICT) {
            assertFalse(anotherTrie.contains(str));
        }
    }

    private static void sendData(Trie from, Trie to) {
        var out = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> from.serialize(out));
        var in = new ByteArrayInputStream(out.toByteArray());
        assertDoesNotThrow(() -> to.deserialize(in));
    }

    private static String[] getDictionary(int size) {
        Random generator = new Random(1);
        var wordsSet = new HashSet<String>();

        while (wordsSet.size() < size) {
            char[] word = new char[3 + generator.nextInt(27)];
            for(int j = 0; j < word.length; j++) {
                word[j] = (char)(generator.nextInt(Character.MAX_VALUE));
            }
            wordsSet.add(new String(word));
        }

        return wordsSet.toArray(new String[size]);
    }
}