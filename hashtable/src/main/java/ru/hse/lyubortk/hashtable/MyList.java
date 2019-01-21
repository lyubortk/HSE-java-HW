package ru.hse.lyubortk.hashtable;

import java.util.Iterator;
import java.util.NoSuchElementException;

/** A simple doubly linked list.
 * Allows to add objects by one and to iterate through content using {@link Iterator} */
public class MyList implements Iterable<Object>{

    /** Inner class which represents nodes of list*/
    private class ListNode{
        private ListNode(Object obj) {
            this.obj = obj;
        }
        private Object obj;
        private ListNode prev;
        private ListNode next;
    }

    /** {@inheritDoc}
     * Implementation of {@link Iterator} for MyList.*/
    private class MyListIterator implements Iterator<Object> {
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
                nextNode = nextNode.next;
                return prevNode.obj;
            } else {
                throw new NoSuchElementException();
            }
        }

        /** {@inheritDoc} */
        @Override
        public void remove() {
            if (prevNode != null) {
                if (prevNode.prev != null) {
                    prevNode.prev.next = nextNode;
                } else {
                    head = nextNode;
                }

                if (nextNode != null) {
                    nextNode.prev = prevNode.prev;
                }
                prevNode = null;
            } else {
                throw new IllegalStateException();
            }
        }

        private ListNode nextNode;
        private ListNode prevNode;
    }

    /** {@inheritDoc} */
    @Override
    public MyListIterator iterator() {
        return new MyListIterator(head);
    }

    /** Inserts one {@link Object} to the front of the list. I.e. iteration
     * by {@link Iterator} will start with the newest added element.
     * @param obj an {@link Object} to add to the list */
    public void insertObject(Object obj) {
        ListNode node = new ListNode(obj);
        if (head != null) {
            node.next = head;
            head.prev = node;
        }
        head = node;
    }

    /** A reference to the front node of the list */
    private ListNode head;
}
