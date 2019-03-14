package ru.hse.crossopt.HashTable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashTableTest {

    private HashTable emptyHashTable, hashTable;

    @BeforeEach
    void setUp() {
        emptyHashTable = new HashTable();
        hashTable = new HashTable();
        hashTable.put("Pink", "Floyd");
        hashTable.put("Rolling", "Stones");
        hashTable.put("Jefferson", "Airplane");
        hashTable.put("The", "Who");
    }

    @Test
    void size() {
        assertEquals(0, emptyHashTable.size());
        assertEquals(4, hashTable.size());
        hashTable.remove("Pink");
        assertEquals(3, hashTable.size());
        hashTable.remove("Pink");
        assertEquals(3, hashTable.size());
    }

    @Test
    void contains_missingElement() {
        assertFalse(emptyHashTable.contains("Pearl"));
        assertFalse(emptyHashTable.contains(null));
        assertFalse(hashTable.contains("Pearl"));
    }

    @Test
    void contains_existingElement() {
        assertTrue(hashTable.contains("Pink"));
        assertTrue(hashTable.contains("Jefferson"));
    }

    @Test
    void get_missingElement() {
        assertNull(emptyHashTable.get("Pearl"));
        assertNull(hashTable.get("Pearl"));
    }

    @Test
    void get_existingElement() {
        assertEquals(hashTable.get("Pink"), "Floyd");
        assertEquals(hashTable.get("Jefferson"), "Airplane");
    }

    @Test
    void put_missingElement() {
        assertNull(emptyHashTable.put("Led", "Zeppelin"));
        assertEquals("Zeppelin", emptyHashTable.get("Led"));
        assertNull(hashTable.put("Led", "Zeppelin"));
        assertEquals("Zeppelin", hashTable.get("Led"));
    }

    @Test
    void put_valueNull() {
        assertThrows(IllegalArgumentException.class, () -> hashTable.put("Queen", null));
    }

    @Test
    void put_existingElement() {
        assertEquals("Airplane", hashTable.put("Jefferson", "Starship"));
    }

    @Test
    void remove_missingElement() {
        assertNull(emptyHashTable.remove("Rolling"));
        assertNull(hashTable.remove("Pearl"));
    }

    @Test
    void remove_existingElement() {
        assertEquals("Stones", hashTable.remove("Rolling"));
    }

    @Test
    void clear() {
        emptyHashTable.clear();
        hashTable.clear();
        assertEquals(0, emptyHashTable.size());
        assertEquals(0, hashTable.size());
        assertFalse(hashTable.contains("Rolling"));
    }
}