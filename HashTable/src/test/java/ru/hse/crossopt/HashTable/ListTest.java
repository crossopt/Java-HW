package ru.hse.crossopt.HashTable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ListTest {

    private List emptyList;
    private List list;

    @BeforeEach
    void setUp() {
        emptyList = new List();
        list = new List();
        list.put("Pink", "Floyd");
        list.put("Rolling", "Stones");
        list.put("Jefferson", "Airplane");
    }

    @Test
    void anyKey_emptyList() {
        assertNull(emptyList.anyKey());
    }

    @Test
    void anyKey_nonEmptyList() {
        assertEquals("Pink", list.anyKey());
    }

    @Test
    void contains_missingElement() {
        assertFalse(emptyList.contains("Pearl"));
        assertFalse(emptyList.contains(null));
        assertFalse(list.contains("Pearl"));
    }

    @Test
    void contains_existingElement() {
        assertTrue(list.contains("Pink"));
        assertTrue(list.contains("Jefferson"));
    }

    @Test
    void get_missingElement() {
        assertNull(emptyList.get("Pearl"));
        assertNull(list.get("Pearl"));
    }

    @Test
    void get_existingElement() {
        assertEquals(list.get("Pink"), "Floyd");
        assertEquals(list.get("Jefferson"), "Airplane");
    }

    @Test
    void put_missingElement() {
        assertNull(emptyList.put("Led", "Zeppelin"));
        assertEquals("Zeppelin", emptyList.get("Led"));
        assertNull(list.put("Led", "Zeppelin"));
        assertEquals("Zeppelin", list.get("Led"));
    }

    @Test
    void put_valueNull() {
        assertThrows(IllegalArgumentException.class, () -> list.put("Queen", null));
    }

    @Test
    void put_existingElement() {
        assertEquals("Airplane", list.put("Jefferson", "Starship"));
    }

    @Test
    void remove_missingElement() {
        assertNull(emptyList.remove("Rolling"));
        assertNull(list.remove("Pearl"));
    }

    @Test
    void remove_existingElement() {
        assertEquals("Stones", list.remove("Rolling"));
    }
}