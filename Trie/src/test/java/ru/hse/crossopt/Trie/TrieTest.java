package ru.hse.crossopt.Trie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TrieTest {
    Trie emptyTrie;
    Trie testTrie;

    @BeforeEach
    void setUp() {
        emptyTrie = new Trie();
        testTrie = new Trie();
        testTrie.add("dog");
        testTrie.add("cat");
        testTrie.add("car");
        testTrie.add("cart");
        testTrie.add("care");
    }

    @Test
    void size() {
        assertEquals(0, emptyTrie.size());
        assertEquals(5, testTrie.size());
    }

    @Test
    void howManyStartWithPrefix_existingPrefix() {
        assertEquals(testTrie.howManyStartWithPrefix("ca"), 4);
        assertEquals(testTrie.howManyStartWithPrefix("cart"), 1);
        assertEquals(testTrie.howManyStartWithPrefix(""), 5);
    }

    @Test
    void howManyStartWithPrefix_missingPrefix() {
        assertEquals(emptyTrie.howManyStartWithPrefix("ca"), 0);
        assertEquals(testTrie.howManyStartWithPrefix("me"), 0);
        assertEquals(testTrie.howManyStartWithPrefix("cares"), 0);
    }

    @Test
    void contains_missingElement() {
        assertFalse(emptyTrie.contains("cats"));
        assertFalse(testTrie.contains("cats"));
        assertFalse(testTrie.contains("ca"));
    }

    @Test
    void contains_existingElement() {
        assertTrue(testTrie.contains("care"));
        testTrie.add("cats");
        assertTrue(testTrie.contains("cats"));
    }

    @Test
    void add_existingElement() {
        assertFalse(testTrie.add("care"));
        assertFalse(testTrie.add("dog"));
        assertEquals(5, testTrie.size());
        assertEquals(testTrie.howManyStartWithPrefix("ca"), 4);
    }

    @Test
    void add_missingElement() {
        assertTrue(testTrie.add("ca"));
        assertTrue(testTrie.add("cats"));
        assertEquals(7, testTrie.size());
        assertEquals(testTrie.howManyStartWithPrefix("c"), 6);
        assertTrue(emptyTrie.add("cat"));
        assertEquals(1, emptyTrie.size());
    }

    @Test
    void remove_existingElement() {
        assertTrue(testTrie.remove("care"));
        assertTrue(testTrie.remove("car"));
        assertEquals(testTrie.size(), 3);
        assertEquals(testTrie.howManyStartWithPrefix("car"), 1);
    }

    @Test
    void remove_missingElement() {
        assertFalse(emptyTrie.remove("cat"));
        assertFalse(testTrie.remove("c"));
        assertEquals(5, testTrie.size());
        assertEquals(testTrie.howManyStartWithPrefix("car"), 3);
    }

    @Test
    void operationsWithNull() {
        assertThrows(IllegalArgumentException.class, () -> testTrie.contains(null));
        assertThrows(IllegalArgumentException.class, () -> testTrie.add(null));
        assertThrows(IllegalArgumentException.class, () -> testTrie.remove(null));
        assertThrows(IllegalArgumentException.class, () -> testTrie.howManyStartWithPrefix(null));
    }

    @Test
    void serializeAndDeserialize() throws IOException {
        var byteArrayOutputStream = new ByteArrayOutputStream();
        testTrie.serialize(byteArrayOutputStream);
        var byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        var serializedResult = new Trie();
        serializedResult.deserialize(byteArrayInputStream);

        assertEquals(5, serializedResult.size());
        assertTrue(serializedResult.contains("care"));
        assertTrue(serializedResult.contains("cart"));
        assertTrue(serializedResult.contains("car"));
        assertTrue(serializedResult.contains("cat"));
        assertTrue(serializedResult.contains("dog"));
    }

    @Test
    void serializingException() {
        var byteArrayOutputStream = new ByteArrayOutputStream();
        var emptyInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        assertThrows(IOException.class, () -> testTrie.deserialize(emptyInputStream));
    }
}