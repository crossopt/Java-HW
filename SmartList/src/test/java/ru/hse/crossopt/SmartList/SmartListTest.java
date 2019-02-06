package ru.hse.crossopt.SmartList;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class SmartListTest {

    @Test
    public void myTestSize() {
        assertEquals(0, newList(Collections.emptyList()).size());
        assertEquals(1, newList(Collections.singletonList(1)).size());
        assertEquals(2, newList(Arrays.asList(1, 2)).size());
    }

    @Test
    public void myTestAdd() {
        List<Integer> list = newList();
        list.add(1);
        assertEquals(Collections.singletonList(1), list);
        list.add(2);
        assertEquals(Arrays.asList(1, 2), list);
        list.add(3);
        assertEquals(Arrays.asList(1, 2, 3), list);
        list.add(4);
        assertEquals(Arrays.asList(1, 2, 3, 4), list);
        list.add(5);
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), list);
        list.add(6);
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), list);
    }

    @Test
    public void myTestThrows() {
        List<Integer> list = newList();
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        list.add(1);
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(8));
        list.add(2);
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(7, 1));
        list.remove(0);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-5));
        list.remove(0);
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
    }

    @Test
    public void testSimple() {
        List<Integer> list = newList();

        assertEquals(Collections.<Integer>emptyList(), list);

        list.add(1);
        assertEquals(Collections.singletonList(1), list);

        list.add(2);
        assertEquals(Arrays.asList(1, 2), list);
    }

    @Test
    public void testGetSet() {
        List<Object> list = newList();

        list.add(1);

        assertEquals(1, list.get(0));
        assertEquals(1, list.set(0, 2));
        assertEquals(2, list.get(0));
        assertEquals(2, list.set(0, 1));

        list.add(2);

        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));

        assertEquals(1, list.set(0, 2));

        assertEquals(Arrays.asList(2, 2), list);

        list.add(3);
        list.add(4);
        assertEquals(4, list.set(3, 7));
        list.add(5);
        list.add(6);
        list.set(4, 0);
        assertEquals(Arrays.asList(2, 2, 3, 7, 0, 6), list);
    }

    @Test
    public void testRemove() throws Exception {
        List<Object> list = newList();

        list.add(1);
        list.remove(0);
        assertEquals(Collections.emptyList(), list);

        list.add(2);
        list.remove((Object) 2);
        assertEquals(Collections.emptyList(), list);

        list.add(1);
        list.add(2);
        assertEquals(Arrays.asList(1, 2), list);

        list.remove(0);
        assertEquals(Collections.singletonList(2), list);

        list.remove(0);
        assertEquals(Collections.emptyList(), list);
    }

    @Disabled // sorry, no iterators
    @Test
    public void testIteratorRemove() throws Exception {
        List<Object> list = newList();
        assertFalse(list.iterator().hasNext());

        list.add(1);

        Iterator<Object> iterator = list.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(1, iterator.next());

        iterator.remove();
        assertFalse(iterator.hasNext());
        assertEquals(Collections.emptyList(), list);

        list.addAll(Arrays.asList(1, 2));

        iterator = list.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(1, iterator.next());

        iterator.remove();
        assertTrue(iterator.hasNext());
        assertEquals(Collections.singletonList(2), list);
        assertEquals(2, iterator.next());

        iterator.remove();
        assertFalse(iterator.hasNext());
        assertEquals(Collections.emptyList(), list);
    }


    @Test
    public void testCollectionConstructor() throws Exception {
        assertEquals(Collections.emptyList(), newList(Collections.emptyList()));
        assertEquals(
                Collections.singletonList(1),
                newList(Collections.singletonList(1)));

        assertEquals(
                Arrays.asList(1, 2),
                newList(Arrays.asList(1, 2)));
    }

    @Test
    public void testAddManyElementsThenRemove() throws Exception {
        List<Object> list = newList();
        for (int i = 0; i < 7; i++) {
            list.add(i + 1);
        }

        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7), list);

        for (int i = 0; i < 7; i++) {
            list.remove(list.size() - 1);
            assertEquals(6 - i, list.size());
        }

        assertEquals(Collections.emptyList(), list);
    }

    private static <T> List<T> newList() {
        try {
            return (List<T>) getListClass().getConstructor().newInstance();
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> List<T> newList(Collection<T> collection) {
        try {
            return (List<T>) getListClass().getConstructor(Collection.class).newInstance(collection);
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> getListClass() throws ClassNotFoundException {
        return Class.forName("ru.hse.crossopt.SmartList.SmartList");
    }
}