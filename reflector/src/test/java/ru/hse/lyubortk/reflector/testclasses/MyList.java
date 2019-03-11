package ru.hse.lyubortk.reflector.testclasses;

import java.util.Iterator;
import java.util.NoSuchElementException;

/** A simple doubly linked list.
 * Allows to add objects by one and to iterate through content using {@link Iterator} */
public class MyList implements Iterable<Object>{

    /** A reference to the front node of the list */
    private ListNode head;

    /** {@inheritDoc} */
    @Override
    public MyListIterator iterator() {
        return new MyListIterator(head);
    }

    /** Inserts one {@link Object} to the front of the list. I.e. iteration
     * by {@link Iterator} will start with the newest added element.
     * @param obj an {@link Object} to add to the list */
    public void insertObject(Object obj) {
        var node = new ListNode(obj);
        if (head != null) {
            node.nextNode = head;
            head.prevNode = node;
        }
        head = node;
    }

    /** Inner class which represents nodes of list*/
    private static class ListNode{
        private Object data;
        private ListNode prevNode;
        private ListNode nextNode;

        private ListNode(Object obj) {
            this.data = obj;
        }
    }

    /** {@inheritDoc}
     * Implementation of {@link Iterator} for MyList.*/
    private class MyListIterator implements Iterator<Object> {

        private ListNode nextNode;
        private ListNode prevNode;

        private MyListIterator(ListNode firstNode) {
            nextNode = firstNode;
        }

        /** {@inheritDoc} */
        @Override
        public boolean hasNext() {
            return nextNode != null;
        }

        /** {@inheritDoc} */
        @Override
        public Object next() {
            if (nextNode != null) {
                prevNode = nextNode;
                nextNode = nextNode.nextNode;
                return prevNode.data;
            } else {
                throw new NoSuchElementException();
            }
        }

        /** {@inheritDoc} */
        @Override
        public void remove() {
            if (prevNode != null) {
                if (prevNode.prevNode != null) {
                    prevNode.prevNode.nextNode = nextNode;
                } else {
                    head = nextNode;
                }

                if (nextNode != null) {
                    nextNode.prevNode = prevNode.prevNode;
                }

                prevNode = null;
            } else {
                throw new IllegalStateException();
            }
        }
    }
}
