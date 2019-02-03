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
    static final int DICT_SIZE = 1000;

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
        String[] dict = getDictionary(DICT_SIZE);
        for (var str : dict) {
            assertTrue(trie.add(str));
        }
    }

    @Test
    void addAlreadyAdded() {
        String[] dict = getDictionary(DICT_SIZE);
        for (var str : dict) {
            trie.add(str);
        }
        for (var str : dict) {
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
        String[] dict = getDictionary(DICT_SIZE);
        for (var str : dict) {
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
        String[] dict = getDictionary(DICT_SIZE);
        for (var str : dict) {
            trie.add(str);
        }
        for (var str : dict) {
            assertTrue(trie.contains(str));
        }
    }

    @Test
    void containsNull() {
        assertThrows(IllegalArgumentException.class, () -> trie.contains(null));
    }

    @Test
    void removeNotAdded() {
        String[] dict = getDictionary(DICT_SIZE);
        for(var str: dict) {
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
        String[] dict = getDictionary(DICT_SIZE);
        for (int i = 0; i < DICT_SIZE; i++) {
            assertEquals(i, trie.size());
            trie.add(dict[i]);
        }
        assertEquals(DICT_SIZE, trie.size());
    }

    @Test
    void sizeDoubleAdding() {
        String[] dict = getDictionary(DICT_SIZE);
        for (var str : dict) {
            trie.add(str);
        }
        for (var str : dict) {
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
        String[] dict = getDictionary(DICT_SIZE);
        for (var str: dict) {
            trie.add(str);
        }

        var anotherTrie = new Trie();
        var out = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> trie.serialize(out));
        var in = new ByteArrayInputStream(out.toByteArray());
        assertDoesNotThrow(() -> anotherTrie.deserialize(in));

        for (var str: dict) {
            assertTrue(anotherTrie.contains(str));
        }
    }

    @Test
    void serializeDoesNotContainOldValues() {
        trie.add("Test String");
        var anotherTrie = new Trie();
        anotherTrie.add("Old String");

        var out = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> trie.serialize(out));
        var in = new ByteArrayInputStream(out.toByteArray());
        assertDoesNotThrow(() -> anotherTrie.deserialize(in));

        assertFalse(anotherTrie.contains("Old String"));
        assertTrue(anotherTrie.contains("Test String"));
    }

    private String[] getDictionary(int size) {
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