package ru.hse.crossopt.HashTable;

import java.util.Arrays;

import static java.lang.Math.abs;

/**
 * Hash table that stores pairs of strings.
 * Implemented via separate chaining with linked lists.
 */
public class HashTable {
    private List[] buckets;
    private int size;
    private int capacity;

    private static final int INITIAL_CAPACITY = 2;
    private static final int LOAD_FACTOR = 2;

    /** Constructor. Creates hash table with INITIAL_CAPACITY buckets. */
    public HashTable() {
        clear();
    }

    /**
     * hash function used within the hash table to determine correct bucket for key.
     * @return The hash, an integer from 0 to capacity. The hash of null is 0.
     */
    private int getHash(String key) {
        if (key == null) {
            return 0;
        }
        return abs(key.hashCode()) % capacity;
    }

    /** Increases capacity of hash table by LOAD_FACTOR. */
    private void rebuild() {
        List[] oldData = buckets;
        capacity *= LOAD_FACTOR;
        size = 0;
        buckets = new List[capacity];
        Arrays.setAll(buckets, ind -> new List());

        for (List bucket : oldData) {
           for (String key = bucket.anyKey(); key != null; key = bucket.anyKey()) {
                String value = bucket.remove(key);
                put(key, value);
            }
        }
    }

    /**
     * Gets size of hash table.
     * @return amount of elements in the hash table.
     */
    public int size() {
        return size;
    }

    /**
     *  Checks if given key is in the hash table.
     * @return true if the given key is in the hash table, false otherwise
     */
    public boolean contains(String key) {
        return buckets[getHash(key)].contains(key);
    }

    /**
     * Returns a value from the hash table with given key.
     * @return Value string with given key or null if none exist.
     */
    public String get(String key) {
        return buckets[getHash(key)].get(key);
    }

    /**
     * Puts node with given key and non-null value in hash table.
     * @return Previous value with given key if it exists or null otherwise.
     * @throws IllegalArgumentException if value string is null.
     */
    public String put(String key, String value) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException("Value string cannot be null.");
        }
        if (size * LOAD_FACTOR >= capacity) {
            rebuild();
        }
        if (!contains(key)) {
            size++;
        }
        return buckets[getHash(key)].put(key, value);
    }

    /**
     * Removes node with given key from hash table.
     * @return Value of removed node or null if it did not exist.
     */
    public String remove(String key) {
        if (contains(key)) {
            size--;
        }
        return buckets[getHash(key)].remove(key);
    }

    /**
     * Clears hash table.
     * Leaves a hash table with capacity INITIAL_CAPACITY and size 0.
     */
    void clear() {
        size = 0;
        capacity = INITIAL_CAPACITY;
        buckets = new List[capacity];
        Arrays.setAll(buckets, ind -> new List());
    }
}
