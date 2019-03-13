package ru.hse.crossopt.HashMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.lang.Math.abs;

import java.util.*;

/**
 * Linked HashMap class that can iterate in order of the elements' addition and do most everything a map does as well.
 * Time complexity of iteration is O(1) amortized.
 * @param <K> key parameter.
 * @param <V> value parameter.
 */
public class HashMap<K, V> implements Map<K, V> {
    private List<K, V>[] buckets;
    private List<K, V> order;
    private int size;
    private int capacity;

    private static final int INITIAL_CAPACITY = 2;
    private static final int LOAD_FACTOR = 2;

    /** Constructor. Creates HashMap with INITIAL_CAPACITY buckets. */
    public HashMap() {
        clear();
    }

    /**
     * hash function used within the HashMap to determine correct bucket for key.
     * @return The hash, an integer from 0 to capacity.
     */
    private int getHash(@NotNull Object key) {
        return abs(key.hashCode()) % capacity;
    }

    /** Increases capacity of HashMap by LOAD_FACTOR. */
    private void rebuild() {
        List<K, V>[] oldData = buckets;
        capacity *= LOAD_FACTOR;
        size = 0;
        buckets = (List<K, V>[])(new List[capacity]);
        Arrays.setAll(buckets, ind -> new List());
        order = new List<>();

        for (var bucket : oldData) {
            for (var key = (K)bucket.anyKey(); key != null; key = bucket.anyKey()) {
                V value = bucket.remove(key);
                put(key, Objects.requireNonNull(value));
            }
        }
    }

    /**
     * Gets size of HashMap.
     * @return amount of elements in the HashMap.
     */
    public int size() {
        return size;
    }

    /**
     *  Checks if given key is in the HashMap.
     * @return true if the given key is in the HashMap, false otherwise
     */
    @SuppressWarnings("unchecked") // key should be of type k.
    public boolean contains(@NotNull Object key) {
        return buckets[getHash(key)].contains((K)key);
    }

    /**
     * Returns a value from the HashMap with given key.
     * @return Value string with given key or null if none exist.
     */
    @SuppressWarnings("unchecked") // key should be of type k.
    public @Nullable V get(@NotNull Object key) {
        return buckets[getHash(key)].get((K)key);
    }

    /**
     * Puts node with given key and non-null value in HashMap.
     * @return Previous value with given key if it exists or null otherwise.
     */
    public @Nullable V put(@NotNull K key, @NotNull V value) {
        if (size * LOAD_FACTOR >= capacity) {
            rebuild();
        }
        if (!contains(key)) {
            size++;
        }
        order.add(key, value);
        return buckets[getHash(key)].put(key, value);
    }

    /**
     * Removes node with given key from HashMap.
     * @return Value of removed node or null if it did not exist.
     */
    @SuppressWarnings("unchecked") // key should be of type k.
    public @Nullable V remove(@NotNull Object key) {
        if (contains(key)) {
            size--;
        }
        return buckets[getHash(key)].remove((K)key);
    }

    /**
     * Clears HashMap.
     * Leaves a HashMap with capacity INITIAL_CAPACITY and size 0.
     */
    public void clear() {
        size = 0;
        capacity = INITIAL_CAPACITY;
        buckets = (List<K, V>[])(new List[capacity]);
        Arrays.setAll(buckets, ind -> new List());
        order = new List<>();
    }


    /**
     * Returns false if HashMap is empty or true otherwise.
     * @return false if HashMap is empty or true otherwise.
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns true if key is in HashMap or false otherwise.
     * @param key a key to check.
     * @return true if key is in HashMap or false otherwise.
     */
    @Override
    public boolean containsKey(@NotNull Object key) {
        return contains(key);
    }

    /**
     * Adds all elements from map to the HashMap.
     * @param map a map to add all elements from.
     */
    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> map) {
        for (var entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Checks if given value is in the HashMap.
     * @param value a value to check.
     * @return true if value is in HashMap, false otherwise.
     */
    @Override
    public boolean containsValue(@NotNull Object value) {
        for (var list : buckets) {
            for (var node = list.getHead(); node != null; node = node.getNext()) {
                if (value.equals(node.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public @NotNull Set<K> keySet() {
        return null;
    }

    @Override
    public @NotNull Collection<V> values() {
        return null;
    }

    /**
     * Returns a Set view of the mappings contained in the HashMap.
     * Only size and iterator work.
     * @return a Set view of the mappings contained in the HashMap.
     */
    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    /** Class for set of entries of the HashMap. Is synchronized with HashMap. */
    class EntrySet extends AbstractSet<Entry<K, V>> {
        /**
         * Returns iterator over the EntrySet. If HashMap is modified iterator will reflect the changes.
         * @return an iterator.
         */
        @Override
        public @NotNull Iterator<Entry<K, V>> iterator() {
            return new HashMapIterator();
        }

        /** Method that returns amount of elements in the EntrySet. */
        @Override
        public int size() {
            return size;
        }
    }

    /** Class for entries of the HashMap. */
    public class HashMapEntry implements Map.Entry<K, V> {
        private @NotNull K key;
        private @NotNull V value;

        public HashMapEntry(@NotNull K key, @NotNull V value) {
            this.key = key;
            this.value = value;
        }

        /** Getter for entry key. */
        @Override
        public @NotNull K getKey() {
            return key;
        }

        /** Getter for entry value. */
        @Override
        public @NotNull V getValue() {
            return value;
        }

        /**
         * Setter for entry value.
         * @param value a value to set to the current entry.
         * @return the previous value.
         */
        @Override
        public @NotNull V setValue(@NotNull V value) {
            V previous = this.value;
            this.value = value;
            return previous;
        }
    }

    /**
     * Iterator over the HashMap.
     * Because removing from list is lazy the time complexity of next() operation is amortized.
     */
    private class HashMapIterator implements Iterator<Entry <K, V>> {
        @Nullable List<K, V>.Node currentNode = order.getHead().getNext();

        /**
         * Checks whether the iterator has next element.
         * @return true if iterator has next element, false otherwise.
         */
        @Override
        public boolean hasNext() {
            return currentNode != null;
        }

        /**
         * Returns the next element in iteration order.
         * @return the next element.
         */
        @Override
        public Entry<K, V> next() {
            var result = new HashMapEntry(currentNode.getKey(), Objects.requireNonNull(currentNode.getValue()));
            currentNode = currentNode.getNext();
            if (!Objects.equals(get(result.getKey()), (result.getValue()))) {
                return next();
            }

            return result;
        }
    }
}
