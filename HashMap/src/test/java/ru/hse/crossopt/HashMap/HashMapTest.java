package ru.hse.crossopt.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashMapTest {
    private HashMap<String, String> hashMap;
    private HashMap<String, Integer> intMap;
    private HashMap<String, String> emptyMap;

    @BeforeEach
    void setUp() {
        emptyMap = new HashMap<>();
        hashMap = new HashMap<>();
        hashMap.put("Pink", "Floyd");
        hashMap.put("Rolling", "Stones");
        hashMap.put("Jefferson", "Airplane");
        intMap = new HashMap<>();
        intMap.put("Dog", 3);
        intMap.put("Cat", 3);
        intMap.put("Rooster", 7);
        intMap.put("Elephant", 8);
    }

    @Test
    void size() {
        assertEquals(0, emptyMap.size());
        assertEquals(3, hashMap.size());
        assertEquals(4, intMap.size());
    }

    @Test
    void contains_missing() {
        assertFalse(emptyMap.contains("pink"));
        assertFalse(hashMap.contains("pink"));
        assertFalse(intMap.contains("pink"));
    }

    @Test
    void contains_existing() {
        assertTrue(hashMap.contains("Pink"));
        assertTrue(intMap.contains("Dog"));
    }

    @Test
    void get_missing() {
        assertNull(emptyMap.get("pink"));
        assertNull(hashMap.get("pink"));
        assertNull(intMap.get("pink"));
    }

    @Test
    void get_existing() {
        assertEquals("Floyd", hashMap.get("Pink"));
        assertEquals(Integer.valueOf(3), intMap.get("Dog"));
        assertEquals("Floyd", hashMap.get("Pink"));
    }

    @Test
    void put_missing() {
        assertNull(emptyMap.put("pink", "p"));
        assertTrue(emptyMap.contains("pink"));
        assertTrue(emptyMap.containsValue("p"));
        assertNull(hashMap.put("pink", "p"));
        assertTrue(hashMap.contains("pink"));
        assertNull(intMap.put("pink", 4));
        assertTrue(intMap.containsValue(4));
        assertEquals(5, intMap.size());
    }

    @Test
    void put_existing() {
        assertEquals("Airplane", hashMap.put("Jefferson", "Starship"));
        assertFalse(hashMap.containsValue("Airplane"));
        assertTrue(hashMap.containsValue("Starship"));
        assertEquals(Integer.valueOf(3), intMap.put("Dog", 4));
        assertTrue(intMap.containsValue(4));
        assertEquals(4, intMap.size());
    }

    @Test
    void remove_missing() {
        assertNull(emptyMap.remove("pink"));
        assertNull(hashMap.remove("pink"));
        assertNull(intMap.remove("pink"));
    }

    @Test
    void remove_existing() {
        assertEquals("Floyd", hashMap.remove("Pink"));
        assertEquals("Airplane", hashMap.remove("Jefferson"));
        assertNull(hashMap.remove("Pink"));
        assertEquals(Integer.valueOf(7), intMap.remove("Rooster"));
        assertEquals(3, intMap.size());
        assertFalse(intMap.containsKey("Rooster"));
        assertFalse(intMap.containsValue(7));
        assertNull(intMap.put("Rooster", 6));
    }

    @Test
    void clear() {
        hashMap.clear();
        assertEquals(0, hashMap.size());
        assertFalse(hashMap.containsValue("Floyd"));
        assertFalse(hashMap.containsKey("Pink"));
        assertNull(hashMap.put("Pink", "Floyd"));
    }

    @Test
    void isEmpty() {
        assertTrue(emptyMap.isEmpty());
        assertFalse(hashMap.isEmpty());
        hashMap.clear();
        assertTrue(hashMap.isEmpty());
    }

    @Test
    void containsKey_missing() {
        assertFalse(emptyMap.containsKey("pink"));
        assertFalse(hashMap.containsKey("pink"));
        assertFalse(intMap.containsKey("pink"));
    }

    @Test
    void containsKey_existing() {
        assertTrue(hashMap.containsKey("Pink"));
        assertTrue(intMap.containsKey("Dog"));
    }

    @Test
    void putAll_existing() {
        HashMap<String, String> secondMap = new HashMap<>();
        secondMap.put("Pink", "Floyd");
        secondMap.put("Rolling", "Stones");
        secondMap.put("Jefferson", "Airplane");
        hashMap.putAll(secondMap);
        assertEquals(3, hashMap.size());
    }

    @Test
    void putAll_existingKey() {
        HashMap<String, String> secondMap = new HashMap<>();
        secondMap.put("Pink", "Lloyd");
        secondMap.put("Jefferson", "Hairplane");
        hashMap.putAll(secondMap);
        assertEquals(3, hashMap.size());
        assertTrue(hashMap.containsValue("Lloyd"));
        assertFalse(hashMap.containsValue("Floyd"));
        assertEquals( "Hairplane", hashMap.get("Jefferson"));
    }

    @Test
    void putAll_empty() {
        HashMap<String, String> secondMap = new HashMap<>();
        hashMap.putAll(secondMap);
        assertEquals(3, hashMap.size());
    }

    @Test
    void putAll_new() {
        HashMap<String, String> secondMap = new HashMap<>();
        secondMap.put("Abney", "Park");
        secondMap.put("Also", "Floyd");
        hashMap.putAll(secondMap);
        assertEquals(5, hashMap.size());
        assertTrue(hashMap.containsValue("Park"));
        assertTrue(hashMap.containsKey("Pink"));
        assertTrue(hashMap.containsKey("Also"));
    }

    @Test
    void containsValue_missing() {
        assertFalse(emptyMap.containsValue("pink"));
        assertFalse(hashMap.containsValue("Pink"));
        assertFalse(intMap.containsValue(10));
    }

    @Test
    void containsValue_existing() {
        assertTrue(hashMap.containsValue("Floyd"));
        assertTrue(intMap.containsValue(3));
        intMap.remove("Dog");
        assertTrue(intMap.containsValue(3));
        intMap.remove("Cat");
        assertFalse(intMap.containsValue(3));
        intMap.put("Pig", 3);
        assertTrue(intMap.containsValue(3));
    }

    @Test
    void entrySet_empty() {
        var emptySet = emptyMap.entrySet();
        assertEquals(0, emptySet.size());
        assertFalse(emptySet.iterator().hasNext());
    }

    @Test
    void entrySet_size() {
        assertEquals(0, emptyMap.entrySet().size());
        assertEquals(3, hashMap.entrySet().size());
        assertEquals(4, intMap.entrySet().size());
    }

    @Test
    void entrySet_synchronizesOnPut() {
        var emptySet = emptyMap.entrySet();
        assertFalse(emptySet.iterator().hasNext());
        emptyMap.put("Here", "I come");
        assertTrue(emptySet.iterator().hasNext());
    }

    @Test
    void entrySet_iteratesInOrderSimple() {
        var iterator = hashMap.entrySet().iterator();
        assertTrue(iterator.hasNext());
        var currentEntry = iterator.next();
        assertEquals("Pink", currentEntry.getKey());
        assertEquals("Floyd", currentEntry.getValue());
        assertTrue(iterator.hasNext());
        currentEntry = iterator.next();
        assertEquals("Rolling", currentEntry.getKey());
        assertEquals("Stones", currentEntry.getValue());
        assertTrue(iterator.hasNext());
        currentEntry = iterator.next();
        assertEquals("Jefferson", currentEntry.getKey());
        assertEquals("Airplane", currentEntry.getValue());
        assertFalse(iterator.hasNext());
    }

    @Test
    void entrySet_iteratesInOrderPreviousPut() {
        var iterator = hashMap.entrySet().iterator();
        assertTrue(iterator.hasNext());
        var currentEntry = iterator.next();
        assertEquals("Pink", currentEntry.getKey());
        assertEquals("Floyd", currentEntry.getValue());
        assertTrue(iterator.hasNext());
        currentEntry = iterator.next();
        assertEquals("Rolling", currentEntry.getKey());
        assertEquals("Stones", currentEntry.getValue());
        assertEquals("Floyd", hashMap.put("Pink", "Panther"));
        assertTrue(iterator.hasNext());
        currentEntry = iterator.next();
        assertEquals("Jefferson", currentEntry.getKey());
        assertEquals("Airplane", currentEntry.getValue());
        assertTrue(iterator.hasNext());
        currentEntry = iterator.next();
        assertEquals("Pink", currentEntry.getKey());
        assertEquals("Panther", currentEntry.getValue());
        assertFalse(iterator.hasNext());
    }

    @Test
    void entrySet_iteratesInOrderPreviousRemove() {
        var iterator = hashMap.entrySet().iterator();
        assertTrue(iterator.hasNext());
        var currentEntry = iterator.next();
        assertEquals("Pink", currentEntry.getKey());
        assertEquals("Floyd", currentEntry.getValue());
        assertTrue(iterator.hasNext());
        assertEquals("Stones", hashMap.remove("Rolling"));
        currentEntry = iterator.next();
        assertEquals("Jefferson", currentEntry.getKey());
        assertEquals("Airplane", currentEntry.getValue());
        assertFalse(iterator.hasNext());
    }

    @Test
    void entrySet_iteratesInOrderExistingPutChangesOrder() {
        var iterator = hashMap.entrySet().iterator();
        assertTrue(iterator.hasNext());
        var currentEntry = iterator.next();
        assertEquals("Pink", currentEntry.getKey());
        assertEquals("Floyd", currentEntry.getValue());
        assertEquals("Stones", hashMap.put("Rolling", "Dice"));
        assertTrue(iterator.hasNext());
        currentEntry = iterator.next();
        assertEquals("Jefferson", currentEntry.getKey());
        assertEquals("Airplane", currentEntry.getValue());
        assertTrue(iterator.hasNext());
        currentEntry = iterator.next();
        assertEquals("Rolling", currentEntry.getKey());
        assertEquals("Dice", currentEntry.getValue());
        assertFalse(iterator.hasNext());
    }
}
