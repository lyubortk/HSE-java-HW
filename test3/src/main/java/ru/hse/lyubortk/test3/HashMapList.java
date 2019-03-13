package ru.hse.lyubortk.test3;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A simple doubly linked list.
 * Allows to add objects by one and to iterate through content using {@link Iterator}.
 */
class HashMapList<E> implements Iterable<E> {
    private ListNode<E> head;

    /** {@inheritDoc} */
    @Override
    public @NotNull Iterator<E> iterator() {
        return new MyListIterator(head);
    }

    /**
     *  Inserts one element to the front of the list. I.e. iteration
     *  by {@link Iterator} will start with the newest added element.
     *  @param data element to add to the list
     */
    void insertToFront(@NotNull E data) {
        var node = new ListNode<>(data);
        if (head != null) {
            node.nextNode = head;
            head.prevNode = node;
        }
        head = node;
    }

    /** Inner class which represents nodes of list */
    private static class ListNode<E>{
        private E data;
        private ListNode<E> prevNode;
        private ListNode<E> nextNode;

        private ListNode(@NotNull E data) {
            this.data = data;
        }
    }

    private void removeNodeFromList(@NotNull ListNode<E> node) {
        if (node.prevNode != null) {
            node.prevNode.nextNode = node.nextNode;
        }
        if (node.nextNode != null) {
            node.nextNode.prevNode = node.prevNode;
        }
        if (head == node) {
            head = node.nextNode;
        }
    }

    /** {@inheritDoc}
     *  Implementation of {@link Iterator} for HashMapList.*/
    private class MyListIterator implements Iterator<E> {
        private ListNode<E> nextNode;
        private ListNode<E> prevNode;

        private MyListIterator(@Nullable ListNode<E> firstNode) {
            nextNode = firstNode;
        }

        @Override
        public boolean hasNext() {
            return nextNode != null;
        }

        @Override
        public E next() {
            if (nextNode != null) {
                prevNode = nextNode;
                nextNode = nextNode.nextNode;
                return prevNode.data;
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            if (prevNode != null) {
                removeNodeFromList(prevNode);
                prevNode = null;
            } else {
                throw new IllegalStateException();
            }
        }
    }
}
