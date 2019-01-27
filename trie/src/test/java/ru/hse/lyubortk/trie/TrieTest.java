package ru.hse.lyubortk.trie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TrieTest {

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
        String[] dict = getDictionary(1000);
        for (var str : dict) {
            assertTrue(trie.add(str));
        }
    }

    @Test
    void addAlreadyAdded() {
        String[] dict = getDictionary(1000);
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
        String[] dict = getDictionary(1000);
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
        String[] dict = getDictionary(1000);
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
        String[] dict = getDictionary(1000);
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
        String[] dict = getDictionary(1000);
        for (int i = 0; i < 1000; i++) {
            assertEquals(i, trie.size());
            trie.add(dict[i]);
        }
        assertEquals(1000, trie.size());
    }

    @Test
    void sizeDoubleAdding() {
        String[] dict = getDictionary(1000);
        for (int i = 0; i < 1000; i++) {
            trie.add(dict[i]);
        }
        for (int i = 0; i < 1000; i++) {
            trie.add(dict[i]);
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
        for (var str: dict) {
            trie.add(str);
        }
        assertEquals(4, trie.howManyStartWithPrefix("t"));
        assertEquals(3, trie.howManyStartWithPrefix("te"));
        assertEquals(1, trie.howManyStartWithPrefix("ten"));
        assertEquals(1, trie.howManyStartWithPrefix("inn"));
        assertEquals(2, trie.howManyStartWithPrefix("i"));
        assertEquals(6, trie.howManyStartWithPrefix(""));
    }

    private String[] getDictionary(int size) {
        Random generator = new Random(1);
        var wordsSet = new HashSet<String>();

        while (wordsSet.size() < size) {
            char[] word = new char[3 + generator.nextInt(27)];
            for(int j = 0; j < word.length; j++) {
                word[j] = (char)(generator.nextInt(65535));
            }
            wordsSet.add(new String(word));
        }

        return wordsSet.toArray(new String[size]);
    }

    private Trie trie;
}