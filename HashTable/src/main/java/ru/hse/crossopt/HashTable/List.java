package ru.hse.crossopt.HashTable;

import java.util.Objects;

/** Singly linked list. */
public class List {
    private Node head = new Node(null, null);;

    /** Elements of list. Store a key, a value and the next node in the list. */
    private static class Node {
        private Node next;
        private String key;
        private String value;

        private Node(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * Gets any key in the list.
     * @return any key or null if none exist.
     */
    public String anyKey() {
        if (head.next != null) {
            return head.next.key;
        }
        return null;
    }

    /**
     *  Checks if there is a node with given key in the list.
     * @return true if the given key is in the list, false otherwise
     */
    public boolean contains(String key) {
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
    public String get(String key) {
        for (Node current = head; current.next != null; current = current.next) {
            if (Objects.equals(key, current.next.key)) {
                return current.next.value;
            }
        }
        return null;
    }

    /** Adds node with given key and value to end of list. */
    private void add(String key, String value) {
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
    public String put(String key, String value) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException("Value string cannot be null.");
        }
        String removed = remove(key);
        add(key, value);
        return removed;
    }

    /**
     * Removes node with given key from list.
     * @return Value of removed node or null if it did not exist.
     */
    public String remove(String key) {
        for (Node current = head; current.next != null; current = current.next) {
            if (Objects.equals(key, current.next.key)) {
                String removed = current.next.value;
                current.next = current.next.next;
                return removed;
            }
        }
        return null;
    }
}
