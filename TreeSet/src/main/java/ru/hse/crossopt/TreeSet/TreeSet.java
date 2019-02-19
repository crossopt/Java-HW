package ru.hse.crossopt.TreeSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Set implementation using unbalanced BST.
 * @param <E> the type of elements maintained by this set.
 */
public class TreeSet<E> extends AbstractSet<E> implements MyTreeSet<E> {
    @NotNull private Tree tree;
    private boolean isReversed;

    /**
     * Constructs empty TreeSet, sorted according to the given comparator.
     * @param comparator the comparator that will be used to order this set.
     */
    public TreeSet(Comparator<? super E> comparator) {
        tree = new Tree(comparator);
    }

    /**
     * Constructs empty TreeSet, sorted according to the natural ordering of its elements.
     * All elements inserted into the set must implement the Comparable interface.
     */
    @SuppressWarnings({"unchecked"})
    public TreeSet() {
        tree = new Tree(((o1, o2) -> ((Comparable<? super E>)o1).compareTo(o2)));
    }

    /**
     * Adds given element to the TreeSet.
     * @param element an element to be added to the set.
     * @return true if element was not in set previously or false otherwise.
     */
    @Override
    public boolean add(@NotNull E element) {
        if (contains(element)) {
            return false;
        }
        tree.modCount++;
        tree.size++;
        if (tree.root == null) {
            tree.root = new Node(element);
            return true;
        }

        Node parent = null;
        Node current = tree.root;
        while (current != null) {
            parent = current;
            current = current.nextInPathTo(element);
        }
        if (tree.comparator.compare(parent.value, element) > 0) {
            parent.left = new Node(element, parent);
        } else {
            parent.right = new Node(element, parent);
        }
        return true;
    }

    /**
     * Returns an iterator over the elements in this set in ascending order.
     * @return an iterator over the elements in the set in ascending order.
     */
    @Override
    @NotNull public Iterator<E> iterator() {
        return new TreeSetIterator(false);
    }

    /**
     * Removes the given element from the TreeSet.
     * @param object an element to remove from the set.
     * @return true if element had been in set previously or false otherwise.
     */
    @SuppressWarnings({"unchecked", "ConstantConditions"}) // empty tree won't contain the object.
    @Override
    public boolean remove(@NotNull Object object) {
        if (!contains(object)) {
            return false;
        }
        tree.modCount++;
        tree.size--;
        tree.root = tree.root.removeNode((E)object);
        return true;
    }

    /** Removes all of the elements from the set. */
    @Override
    public void clear() {
        tree.root = null;
        tree.size = 0;
        tree.modCount++;
    }

    /**
     * Gets size of TreeSet.
     * @return amount of elements in the TreeSet.
     */
    @Override
    public int size() {
        return tree.size;
    }

    /**
     * Checks if given element is in the TreeSet.
     * @param object an element to check.
     * @return true if the given element is in the set, false otherwise.
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(@NotNull Object object) {
        return (tree.root != null) && (tree.root.find((E)object) != null);
    }

    /**
     * Returns an iterator over the elements in this set in descending order.
     * @return an iterator over the elements in the set in descending order.
     */
    @Override
    @NotNull public Iterator<E> descendingIterator() {
        return new TreeSetIterator(true);
    }

    /**
     * Returns a reverse order view of the elements contained in this TreeSet.
     * Changes to the set are reflected in the descending set, and vice-versa.
     * Does not copy the original set, time complexity is O(1).
     * @return a reverse order view of the original set.
     */
    @Override
    @NotNull public MyTreeSet<E> descendingSet() {
        TreeSet<E> descending = new TreeSet<>();
        descending.isReversed = !isReversed;
        descending.tree = tree;
        return descending;
    }

    /**
     * Returns the smallest element in the TreeSet.
     * @return the smallest element in the set.
     * @throws NoSuchElementException if the set is empty.
     */
    @Override
    @NotNull public E first() {
        if (tree.root == null) {
            throw new NoSuchElementException("No elements in TreeSet");
        }
        return tree.root.getFirst(isReversed).value;
    }

    /**
     * Returns the largest element in the TreeSet.
     * @return the largest element in the set.
     * @throws NoSuchElementException if the set is empty.
     */
    @Override
    @NotNull public E last() {
        if (tree.root == null) {
            throw new NoSuchElementException("No elements in TreeSet");
        }
        return tree.root.getFirst(!isReversed).value;
    }

    /**
     * Returns the greatest element in the TreeSet strictly less than the given value.
     * @param element the value to match.
     * @return the greatest element less than the given value, or null if there is no such element.
     */
    @Override
    @Nullable public E lower(@NotNull E element) {
        if (tree.root == null) {
            return null;
        }
        Node lowerNode = tree.root.findLower(element, isReversed);
        return lowerNode == null ? null : lowerNode.value;
    }

    /**
     * Returns the greatest element in the TreeSet not larger than than the given value.
     * @param element the value to match.
     * @return the greatest element less than or equal to the given value,
     * or null if there is no such element.
     */
    @Override
    @Nullable public E floor(@NotNull E element) {
        if (tree.root == null) {
            return null;
        }
        Node elementNode = tree.root.find(element);
        return elementNode != null ? elementNode.value : lower(element);
    }

    /**
     * Returns the smallest element in the TreeSet not smaller than than the given value.
     * @param element the value to match.
     * @return the smallest element greater than or equal to the given value,
     * or null if there is no such element.
     */
    @Override
    @Nullable public E ceiling(@NotNull E element) {
        if (tree.root == null) {
            return null;
        }
        Node elementNode = tree.root.find(element);
        return elementNode != null ? elementNode.value : higher(element);
    }

    /**
     * Returns the smallest element in the TreeSet strictly greater than the given value.
     * @param element the value to match.
     * @return the least element greater than the given value, or null if there is no such element.
     */
    @Override
    @Nullable public E higher(@NotNull E element) {
        if (tree.root == null) {
            return null;
        }
        Node higherNode = tree.root.findLower(element, !isReversed);
        return higherNode == null ? null : higherNode.value;
    }

    private class Node {
        @NotNull private E value;
        @Nullable private Node left;
        @Nullable private Node right;
        @Nullable private Node parent;

        private Node(@NotNull E value) {
            this.value = value;
        }

        private Node(@NotNull E value, @Nullable Node parent) {
            this.value = value;
            this.parent = parent;
        }

        @Nullable private Node getLeft(boolean reverse) {
            return reverse ? right : left;
        }

        @Nullable private Node getRight(boolean reverse) {
            return reverse ? left : right;
        }

        @NotNull private Node getFirst(boolean reverse) {
            Node next = getLeft(reverse);
            return next == null ? this : next.getFirst(reverse);
        }

        @Nullable private Node getLargerAncestor(boolean reverse) {
            if (parent == null) {
                return null;
            }
            return parent.getRight(reverse) == this ? parent.getLargerAncestor(reverse) : parent;
        }

        @Nullable private Node getNext(boolean reverse) {
            Node next = getRight(reverse);
            return next == null ? getLargerAncestor(reverse) : next.getFirst(reverse);
        }

        @Nullable private Node nextInPathTo(E element) {
            return tree.comparator.compare(value, element) > 0 ? left : right;
        }

        @Nullable private Node find(E element) {
            if (tree.comparator.compare(value, element) == 0) {
                return this;
            } else {
                Node next = nextInPathTo(element);
                return next == null ? null : next.find(element);
            }
        }

        @Nullable private Node findLower(@NotNull E element, boolean reverse) {
            int comparison = tree.comparator.compare(value, element);
            if (comparison == 0 || (comparison < 0) == reverse) {
                Node next = getLeft(reverse);
                return next == null ? null : next.findLower(element, reverse);
            } else {
                Node result = this;
                Node next = getRight(reverse);
                if (next != null && next.findLower(element, reverse) != null) {
                    result = next.findLower(element, reverse);
                }
                return result;
            }
        }

        @SuppressWarnings("ConstantConditions") // removeNode is called only from existing element.
        @Nullable private Node removeNode(@NotNull E element) {
            if (tree.comparator.compare(value, element) == 0) {
                if (left == null) {
                    if (right != null) {
                        right.parent = parent;
                    }
                    return right;
                } else if (right == null) {
                    left.parent = parent;
                    return left;
                } else {
                    value = getFirst(false).value;
                    left = left.removeNode(value);
                }
            } else {
                if (tree.comparator.compare(value, element) > 0) {
                    left = left.removeNode(element);
                } else {
                    right = right.removeNode(element);
                }
            }
            return this;
        }

    }

    private class Tree {
        @Nullable private Node root;
        @NotNull private Comparator<? super E> comparator;
        private int modCount;
        private int size;

        private Tree(@NotNull Comparator <? super E> comparator) {
            this.comparator = comparator;
        }
    }

    private class TreeSetIterator implements Iterator<E> {
        @Nullable private Node current;
        private int modCount;
        private boolean isDescending;

        private TreeSetIterator(boolean isDescending) {
            this.isDescending = isDescending ^ isReversed;
            modCount = tree.modCount;
        }

        /**
         * Returns true if the iteration has more elements.
         * @return true if the iteration has more elements or false otherwise.
         * @throws ConcurrentModificationException if iterator is invalid.
         */
        @Override
        public boolean hasNext() {
            return peekNext() != null;
        }

        /**
         * Returns the next element in the iteration.
         * @return the next element in the iteration.
         * @throws NoSuchElementException if next element does not exist.
         * @throws ConcurrentModificationException if iterator is invalid.
         */
        @Override
        @NotNull public E next() {
            current = peekNext();
            if (current == null) {
                throw new NoSuchElementException("Next element does not exist.");
            }
            return current.value;
        }

        @Nullable private Node peekNext() {
            if (modCount != tree.modCount) {
                throw new ConcurrentModificationException("Tree was modified.");
            }
            if (tree.root == null) {
                return null;
            } else if (current == null) {
                return tree.root.getFirst(isDescending);
            } else {
                return current.getNext(isDescending);
            }
        }
    }
}
