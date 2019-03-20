package ru.hse.crossopt.HashMap;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Singly linked list that holds entries for HashMap.
 * @param <K> key parameter.
 * @param <V> value parameter.
 */
public class List <K, V> {
    private Node head = new Node();

    /** Elements of list. Store a key, a value and the next node in the list. */
    public class Node {
        private @Nullable Node next;
        private @Nullable K key; // null for dummy
        private @Nullable V value; // null for dummy

        /** Getter for next node. */
        public @Nullable Node getNext() {
            return next;
        }

        /** Getter for key in node. */
        public K getKey() {
            return key;
        }

        /** Getter for value in node. */
        public V getValue() {
            return value;
        }

        private Node(@NotNull K key, @NotNull V value) {
            this.key = key;
            this.value = value;
        }

        private Node() {} // constructor only for dummy head.
    }

    /**
     * Getter for head of list.
     * @return Node that is dummy head of list.
     */
    public @NotNull Node getHead() {
        return head;
    }

    /**
     * Gets any key in the list.
     * @return any key or null if none exist.
     */
    public @Nullable K anyKey() {
        if (head.next != null) {
            return head.next.key;
        }
        return null;
    }

    /**
     *  Checks if there is a node with given key in the list.
     * @return true if the given key is in the list, false otherwise
     */
    public boolean contains(K key) {
        for (Node current = head; current.next != null; current = current.next) {
            if (Objects.equals(key, current.next.key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a value from the list with given key.
     * @return Value string with given key or null if none exist.
     */
    public @Nullable V get(@NotNull K key) {
        for (Node current = head; current.next != null; current = current.next) {
            if (Objects.equals(key, current.next.key)) {
                return current.next.value;
            }
        }
        return null;
    }

    /** Adds node with given key and value to end of list. */
    public void add(@NotNull K key, @NotNull V value) {
        Node current = head;
        while (current.next != null) {
            current = current.next;
        }
        current.next = new Node(key, value);
    }

    /**
     * Puts node with given key and non-null value in list.
     * @return Previous value with given key if it exists or null otherwise.
     * @throws IllegalArgumentException if value string is null.
     */
    public @Nullable V put(@NotNull K key, @NotNull V value) {
        V removed = remove(key);
        add(key, value);
        return removed;
    }

    /**
     * Removes node with given key from list.
     * @return Value of removed node or null if it did not exist.
     */
    public @Nullable V remove(@NotNull K key) {
        for (Node current = head; current.next != null; current = current.next) {
            if (Objects.equals(key, current.next.key)) {
                V removed = current.next.value;
                current.next = current.next.next;
                return removed;
            }
        }
        return null;
    }
}
