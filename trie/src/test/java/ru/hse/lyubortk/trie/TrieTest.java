package ru.hse.lyubortk.trie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
        assertEquals(DICT_SIZE, trie.size());
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
    void equalsBasic() {
        var anotherTrie = new Trie();
        for (var str: DICT) {
            trie.add(str);
            anotherTrie.add(str);
        }

        assertEquals(trie, anotherTrie);
    }

    @Test
    void notEquals() {
        var anotherTrie = new Trie();
        for (var str: new String[]{"a", "b", "c", "ad"}) {
            trie.add(str);
            anotherTrie.add(str);
        }
        trie.add("e");

        assertNotEquals(trie, anotherTrie);
    }

    @Test
    void serialize() throws IOException {
        try (var serializationResult = new ByteArrayOutputStream();
             var expectedResult = new ByteArrayOutputStream()) {
            writeTrieWithThreeStrings(expectedResult);

            trie.add("a");
            trie.add("b");
            trie.add("bc");
            assertDoesNotThrow(() -> trie.serialize(serializationResult));

            assertArrayEquals(expectedResult.toByteArray(), serializationResult.toByteArray());
        }
    }

    @Test
    void deserialize() throws IOException {
        try (var expectedInput = new ByteArrayOutputStream()) {
            writeTrieWithThreeStrings(expectedInput);

            var anotherTrie = new Trie();
            assertDoesNotThrow(() -> anotherTrie.deserialize(
                    new ByteArrayInputStream(expectedInput.toByteArray())));

            trie.add("a");
            trie.add("b");
            trie.add("bc");

            assertEquals(trie, anotherTrie);
        }
    }

    private void writeTrieWithThreeStrings(ByteArrayOutputStream out) throws IOException {
        var dataOut = new DataOutputStream(out);
        dataOut.writeBoolean(false);
        dataOut.writeInt(0);
        dataOut.writeInt(3);
        dataOut.writeChar('\0');
        dataOut.writeInt(2);

        dataOut.writeChar('a');

        dataOut.writeBoolean(true);
        dataOut.writeInt(1);
        dataOut.writeInt(1);
        dataOut.writeChar('a');
        dataOut.writeInt(0);

        dataOut.writeChar('b');

        dataOut.writeBoolean(true);
        dataOut.writeInt(1);
        dataOut.writeInt(2);
        dataOut.writeChar('b');
        dataOut.writeInt(1);

        dataOut.writeChar('c');

        dataOut.writeBoolean(true);
        dataOut.writeInt(2);
        dataOut.writeInt(1);
        dataOut.writeChar('c');
        dataOut.writeInt(0);

        assertDoesNotThrow(dataOut::flush);
    }

    @Test
    void serializeDeserializeBasic() throws IOException {
        for (var str: DICT) {
            trie.add(str);
        }

        var anotherTrie = new Trie();
        sendData(trie, anotherTrie);

        assertEquals(trie, anotherTrie);
    }

    @Test
    void serializeDeserializeNotEmpty() throws IOException {
        trie.add("Test String");
        var anotherTrie = new Trie();
        anotherTrie.add("Old String");
        anotherTrie.add("Another Old String");

        sendData(trie, anotherTrie);

        assertEquals(trie, anotherTrie);
    }

    @Test
    void serializeDeserializeFromEmptyTrie() throws IOException {
        var anotherTrie = new Trie();
        for (var str : DICT) {
            anotherTrie.add(str);
        }
        assertEquals(DICT_SIZE, anotherTrie.size());

        sendData(trie, anotherTrie);

        assertEquals(trie, anotherTrie);
    }

    private static void sendData(Trie from, Trie to) throws IOException {
        try (var out = new ByteArrayOutputStream()) {
            assertDoesNotThrow(() -> from.serialize(out));
            try (var in = new ByteArrayInputStream(out.toByteArray())) {
                assertDoesNotThrow(() -> to.deserialize(in));
            }
        }
    }

    private static String[] getDictionary(int size) {
        Random generator = new Random(1);
        var wordsSet = new HashSet<String>();

        while (wordsSet.size() < size) {
            char[] word = new char[3 + generator.nextInt(27)];
            for(int j = 0; j < word.length; j++) {
                word[j] = (char) generator.nextInt(Character.MAX_VALUE);
            }
            wordsSet.add(new String(word));
        }

        return wordsSet.toArray(new String[size]);
    }
}