package ru.hse.crossopt.Trie;

import org.jetbrains.annotations.NotNull;
import ru.hse.crossopt.Serializable.Serializable;
import java.io.*;
import java.util.HashMap;

/** Trie class that stores strings. */
public class Trie implements Serializable {
    @NotNull private Node root = new Node();

    /**
     * Checks if given string is in trie.
     * Runs in O(n) time, where n is the length of the given string.
     * @param element a string for which to check whether it is in trie.
     * @return true if the given string is in the trie or false otherwise.
     */
    public boolean contains(@NotNull String element) {
        Node current = root;
        for (char symbol : element.toCharArray()) {
            if (!current.existsNext(symbol)) {
                return false;
            }
            current = current.getNext(symbol);
        }
        return current.isTerminal;
    }

    /**
     * Adds given string to trie.
     * Runs in O(n) time, where n is the length of the given string.
     * @param element a string to add to trie.
     * @return true if a new element has been added or false otherwise.
     */
    public boolean add(@NotNull String element) {
        if (contains(element)) {
            return false;
        }
        Node current = root;
        for (char symbol : element.toCharArray()) {
            current.suffixAmount++;
            current = current.getNext(symbol);
        }
        current.suffixAmount++;
        current.isTerminal = true;
        return true;
    }

    /**
     * Removes given string from trie.
     * Runs in O(n) time, where n is the length of the given string.
     * @param element a string to remove from trie.
     * @return true if the string had been in the trie prior to removal or false otherwise.
     */
    public boolean remove(@NotNull String element) {
        if (!contains(element)) {
            return false;
        }
        Node current = root;
        for (char symbol : element.toCharArray()) {
            current.suffixAmount--;
            current = current.getNext(symbol);
        }
        current.suffixAmount--;
        current.isTerminal = false;
        return true;
    }

    /**
     * Gets size of trie. Runs in O(1) time.
     * @return amount of strings in the trie.
     */
    public int size() {
        return root.suffixAmount;
    }

    /**
     * Counts amount of strings in trie that start with given prefix.
     * Runs in O(n) time, where n is the length of the given prefix.
     * @param prefix a prefix for which to count amount of strings.
     * @return amount of strings in trie that start with given prefix.
     */
    public int howManyStartWithPrefix(@NotNull String prefix) {
        Node current = root;
        for (char symbol : prefix.toCharArray()) {
            if (!current.existsNext(symbol)) {
                return 0;
            }
            current = current.getNext(symbol);
        }
        return current.suffixAmount;
    }

    /**
     * Writes trie into output stream.
     * @param out an output stream to write into.
     * @throws IOException if writing into stream failed.
     */
    public void serialize(OutputStream out) throws IOException {
        try (var objectOutputStream = new ObjectOutputStream(out)) {
            root.serialize(objectOutputStream);
        }
    }

    /**
     * Reads trie from input stream.
     * @param in An input stream to read from.
     * @throws IOException if reading from stream failed.
     */
    public void deserialize(InputStream in) throws IOException {
        try (var objectInputStream = new ObjectInputStream(in)) {
            root.deserialize(objectInputStream);
        }
    }

    private static class Node {
        private boolean isTerminal;
        private int suffixAmount;
        @NotNull private HashMap <Character, Node> nextNode = new HashMap<>();

        @NotNull private Node getNext(char symbol) {
            if (!nextNode.containsKey(symbol)) {
                nextNode.put(symbol, new Node());
            }
            return nextNode.get(symbol);
        }

        private boolean existsNext(char symbol) {
            return nextNode.containsKey(symbol);
        }

        private void serialize(ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.writeBoolean(isTerminal);
            objectOutputStream.writeInt(suffixAmount);
            objectOutputStream.writeInt(nextNode.size());
            for (var element : nextNode.entrySet()) {
                objectOutputStream.writeChar(element.getKey());
                element.getValue().serialize(objectOutputStream);
            }
        }

        private void deserialize(ObjectInputStream objectInputStream) throws IOException {
            isTerminal = objectInputStream.readBoolean();
            suffixAmount = objectInputStream.readInt();
            nextNode.clear();
            int nextNodeSize = objectInputStream.readInt();
            for (int i = 0; i < nextNodeSize; i++) {
                char symbol = objectInputStream.readChar();
                var nodeForSymbol = new Node();
                nodeForSymbol.deserialize(objectInputStream);
                nextNode.put(symbol, nodeForSymbol);
            }
        }
    }
}
