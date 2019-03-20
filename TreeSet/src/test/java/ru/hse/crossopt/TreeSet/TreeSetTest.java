package ru.hse.crossopt.TreeSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class TreeSetTest {
    private TreeSet<Integer> intSet;
    private MyTreeSet<Integer> descendingIntSet;
    private TreeSet<String> emptySet;
    private TreeSet<String> stringSet;

    @BeforeEach
    void setUpIntSets() {
        intSet = new TreeSet<>();
        descendingIntSet = intSet.descendingSet();
        intSet.add(3);
        intSet.add(4);
        intSet.add(5);
    }

    @BeforeEach
    void SetUpStringSets() {
        emptySet = new TreeSet<>();
        stringSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        stringSet.add("a");
        stringSet.add("B");
        stringSet.add("c");
    }

    @Test
    void iterator() {
        assertFalse(emptySet.iterator().hasNext());
        assertArrayEquals(new Integer[]{3, 4, 5}, intSet.toArray());
        var it = intSet.iterator();
        assertTrue(it.hasNext());
        assertEquals(Integer.valueOf(3), it.next());
        assertEquals(Integer.valueOf(4), it.next());
        assertEquals(Integer.valueOf(5), it.next());
        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void iteratorInvalidation() {
        var intIterator = intSet.iterator();
        descendingIntSet.remove(5);
        assertThrows(ConcurrentModificationException.class, intIterator::hasNext);
        assertThrows(ConcurrentModificationException.class, intIterator::next);
        var emptyIterator = emptySet.iterator();
        emptySet.add("doge");
        assertThrows(ConcurrentModificationException.class, emptyIterator::next);
        emptyIterator = emptySet.descendingIterator();
        emptySet.clear();
        assertThrows(ConcurrentModificationException.class, emptyIterator::hasNext);
    }

    @Test
    void iteratorCorrectOperations() {
        var stringIterator = stringSet.iterator();
        assertTrue(stringSet.contains("b"));
        assertTrue(stringIterator.hasNext());
        var newIterator = stringSet.iterator();
        assertTrue(stringIterator.hasNext());
        assertEquals("B", stringSet.floor("B"));
        assertTrue(newIterator.hasNext());
    }

    @Test
    void add_existingElement() {
        assertFalse(intSet.add(5));
        assertTrue(intSet.add(6));
        assertFalse(descendingIntSet.add(6));
        assertFalse(stringSet.add("b"));
    }

    @Test
    void add_missingElement() {
        assertTrue(intSet.add(6));
        assertTrue(stringSet.add("bb"));
        assertTrue(emptySet.add(""));
        assertTrue(descendingIntSet.add(-5));
    }

    @Test
    void remove_missingElement() {
        assertFalse(emptySet.remove("2"));
        assertFalse(descendingIntSet.remove(-5));
        assertTrue(descendingIntSet.remove(5));
        assertFalse(intSet.remove(5));
        assertFalse(stringSet.remove("aaa"));
    }

    @Test
    void remove_existingElement() {
        assertTrue(stringSet.remove("b"));
        assertTrue(intSet.remove(4));
        assertArrayEquals(new Integer[]{3, 5}, intSet.toArray());
        assertTrue(intSet.remove(3));
        assertTrue(intSet.remove(5));
        assertEquals(0, descendingIntSet.size());
    }

    @Test
    void clear() {
        intSet.clear();
        assertEquals(0, intSet.size());
        assertFalse(intSet.contains(3));
    }

    @Test
    void size() {
        assertEquals(3, descendingIntSet.size());
        assertEquals(3, intSet.size());
        assertEquals(3, stringSet.size());
        assertEquals(0, emptySet.size());
    }

    @Test
    void contains_existingElement() {
        assertTrue(stringSet.contains("b"));
        assertTrue(intSet.contains(5));
        assertTrue(intSet.contains(3));
        assertTrue(descendingIntSet.contains(4));
    }

    @Test
    void contains_missingElement() {
        assertFalse(stringSet.contains("bb"));
        assertFalse(stringSet.contains(""));
        assertFalse(intSet.contains(2));
        assertFalse(descendingIntSet.contains(2));
        assertFalse(emptySet.contains("2"));
    }


    @Test
    void descendingIterator() {
        assertFalse(emptySet.descendingIterator().hasNext());
        var it = intSet.descendingIterator();
        assertTrue(it.hasNext());
        assertEquals(Integer.valueOf(5), it.next());
        assertEquals(Integer.valueOf(4), it.next());
        assertEquals(Integer.valueOf(3), it.next());
        assertFalse(it.hasNext());
    }

    @Test
    void descendingSetIterator() {
        var it = descendingIntSet.iterator();
        assertTrue(it.hasNext());
        assertEquals(Integer.valueOf(5), it.next());
        assertEquals(Integer.valueOf(4), it.next());
        assertEquals(Integer.valueOf(3), it.next());
        assertFalse(it.hasNext());
    }

    @Test
    void descendingSet() {
        assertArrayEquals(new Integer[]{5, 4, 3}, descendingIntSet.toArray());
        assertArrayEquals(new Integer[]{3, 4, 5}, descendingIntSet.descendingSet().toArray());
        assertArrayEquals(new String[]{"c", "B", "a"}, stringSet.descendingSet().toArray());
    }

    @Test
    void setAndDescendingSetOperations() {
        assertEquals(Integer.valueOf(4), descendingIntSet.lower(3));
        assertTrue(descendingIntSet.add(2));
        assertArrayEquals(new Integer[]{2, 3, 4, 5}, intSet.toArray());
        assertEquals(4, intSet.size());
        assertEquals(Integer.valueOf(5), descendingIntSet.first());
        assertTrue(descendingIntSet.remove(5));
        assertArrayEquals(new Integer[]{4, 3, 2}, descendingIntSet.toArray());
    }

    @Test
    void first() {
        assertThrows(NoSuchElementException.class, () -> emptySet.first());
        assertEquals(Integer.valueOf(3), intSet.first());
        assertEquals(Integer.valueOf(5), descendingIntSet.first());
        assertEquals("a", stringSet.first());
    }

    @Test
    void last() {
        assertThrows(NoSuchElementException.class, () -> emptySet.first());
        assertEquals(Integer.valueOf(5), intSet.last());
        assertEquals(Integer.valueOf(3), descendingIntSet.last());
        assertEquals("c", stringSet.last());
    }

    @Test
    void lower_existingElement() {
        assertNull(intSet.lower(3));
        assertEquals("a", stringSet.lower("B"));
        assertEquals(Integer.valueOf(4), intSet.lower(5));
        assertNull(stringSet.lower("a"));
    }

    @Test
    void lower_missingElement() {
        assertNull(emptySet.lower("2"));
        assertEquals(Integer.valueOf(5), intSet.lower(179));
        assertNull(descendingIntSet.lower(179));
        assertEquals(Integer.valueOf(3), descendingIntSet.lower(-1));
    }

    @Test
    void floor_existingElement() {
        assertEquals(Integer.valueOf(3), intSet.floor(3));
        assertEquals("B", stringSet.floor("b"));
        assertEquals(Integer.valueOf(5), intSet.floor(5));
        assertEquals("a", stringSet.floor("a"));
    }

    @Test
    void floor_missingElement() {
        assertNull(emptySet.floor("2"));
        assertEquals(Integer.valueOf(5), intSet.floor(179));
        assertNull(descendingIntSet.floor(179));
        assertEquals(Integer.valueOf(3), descendingIntSet.floor(-1));
    }

    @Test
    void ceiling_missingElement() {
        assertNull(emptySet.ceiling("2"));
        assertEquals(Integer.valueOf(3), intSet.ceiling(1));
        assertNull(descendingIntSet.ceiling(-1));
        assertEquals("B", stringSet.ceiling("ab"));
    }

    @Test
    void ceiling_existingElement() {
        assertEquals(Integer.valueOf(5), intSet.ceiling(5));
        assertEquals(Integer.valueOf(4), intSet.ceiling(4));
        assertEquals(Integer.valueOf(4), descendingIntSet.ceiling(4));
        assertEquals("B", stringSet.higher("ab"));
    }

    @Test
    void higher_missingElement() {
        assertNull(emptySet.higher("2"));
        assertEquals(Integer.valueOf(3), intSet.higher(1));
        assertNull(descendingIntSet.higher(-1));
        assertEquals("B", stringSet.higher("ab"));
    }

    @Test
    void higher_existingElement() {
        assertNull(intSet.higher(5));
        assertEquals(Integer.valueOf(5), intSet.higher(4));
        assertEquals(Integer.valueOf(3), descendingIntSet.higher(4));
        assertEquals("B", stringSet.higher("a"));
    }
}