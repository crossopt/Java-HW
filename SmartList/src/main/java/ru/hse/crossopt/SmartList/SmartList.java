package ru.hse.crossopt.SmartList;

import java.util.*;

/** Smart list class, optimized for short lists.
 * List with 0 elements is stored as null.
 * List with one element stores the element itself.
 * List with 2-5 elements stores a 5-element array.
 * Larger lists store an ArrayList of elements.
 */
public class SmartList<E> extends AbstractList<E> implements List<E> {
    private int size;
    Object data;

    /** Constructor that creates empty list. */
    public SmartList() {
        size = 0;
        data = null;
    }

    /** Constructor that creates list from elements of given collection.
     * @param collection a collection the elements of which will be the list elements.
     */
    public SmartList(Collection <? extends E> collection) {
        size = collection.size();
        if (size == 0) {
            data = null;
        } else if (size == 1) {
            for (E element : collection) {
                data = element;
            }
        } else if (size <= 5) {
            data = new Object[5];
            int index = 0;
            for (E element : collection) {
                ((Object[]) data)[index++] = element;
            }
        } else {
            data = new ArrayList<E>((Collection<? extends E>) Arrays.asList(collection.toArray()));
        }
    }

    /**
     * Returns the element at the specified position in this list.
     * @param index index of the element to return.
     * @return the element at the specified position in this list.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    @Override
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("List index is out of range");
        }
        if (size == 1) {
            return (E)data;
        } else if (size <= 5) {
            return (E)((Object[]) data)[index];
        } else {
            return ((ArrayList <E>) data).get(index);
        }
    }

    /**
     * Replaces the element at the specified position in this list with the specified element.
     * @param index index of the element to replace.
     * @param element element to be stored at the specified position.
     * @return the element previously at the specified position.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    @Override
    public E set(int index, E element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("List index is out of range");
        }
        E oldData = get(index);
        if (size == 1) {
            data = element;
        } else if (size <= 5) {
            ((Object[]) data)[index] = element;
        } else {
            ((ArrayList<E>) data).set(index, element);
        }
        return oldData;
    }

    /**
     * Returns the number of elements in this list.
     * @return the number of elements in this list.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Adds element to list.
     * @param element an element to add.
     * @return true if add was successful.
     */
    @Override
    public boolean add(E element) {
        if (size == 0) {
            data = element;
        } else if (size == 1) {
            E oldData = (E)data;
            data = new Object[5];
            ((Object[]) data)[0] = oldData;
            ((Object[]) data)[1] = element;
        } else if (size <= 4) {
            ((Object[]) data)[size] = element;
        } else if (size == 5) {
            data = new ArrayList<E>((Collection<? extends E>) Arrays.asList((Object[]) data));
            ((ArrayList<E>) data).add(element);
        } else {
            ((ArrayList<E>) data).add(element);
        }
        size++;
        return true;
    }

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their indices).
     * @param index index of the elements to remove.
     * @return the element that was removed from the list.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    @Override
    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("List index is out of range");
        }

        E oldData = get(index);
        if (size == 1) {
            data = null;
        } else if (size <= 5) {
            var dataList = new ArrayList<E>((Collection<? extends E>) Arrays.asList((Object[]) data));
            dataList.remove(index);
            data = ((ArrayList <E>) data).toArray();
        } else if (size == 6) {
            ((ArrayList<E>) data).remove(index);
            data = ((ArrayList <E>) data).toArray();
        } else {
            ((ArrayList<E>) data).remove(index);
        }
        size--;
        return oldData;
    }
}
