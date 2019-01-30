package ru.hse.inclass.list;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/** A simple doubly linked list.
 * Allows to add objects by one and to iterate through content using {@link Iterator} */
public class MyList<E>  extends AbstractList<E> implements Iterable<E> {

    /** Inner class which represents nodes of list*/
    private class ListNode{
        private ListNode(E obj) {
            this.obj = obj;
        }
        private E obj;
        private ListNode prev;
        private ListNode next;
    }

    /** {@inheritDoc}
     * Implementation of {@link Iterator} for MyList.*/
    private class MyListIterator implements ListIterator<E> {
        private MyListIterator(ListNode firstNodeInList) {
            rightNode = firstNodeInList;
            leftNode = null;
            nodeOnPreviousMove = null;
            index = 0;
        }

        /** {@inheritDoc} */
        @Override
        public boolean hasNext() {
            return rightNode != null;
        }

        /** {@inheritDoc} */
        @Override
        public boolean hasPrevious() {
            return leftNode != null;
        }

        @Override
        public int nextIndex() {
            return index;
        }

        @Override
        public int previousIndex() {
            return index-1;
        }

        /** {@inheritDoc} */
        @Override
        public E next() {
            if (rightNode != null) {
                leftNode = rightNode;
                rightNode = leftNode.next;
                nodeOnPreviousMove = leftNode;
                index++;
                return leftNode.obj;
            } else {
                throw new NoSuchElementException();
            }
        }

        /** {@inheritDoc} */
        @Override
        public E previous() {
            if (leftNode != null) {
                rightNode = leftNode;
                leftNode = rightNode.prev;
                nodeOnPreviousMove = rightNode;
                index--;
                return rightNode.obj;
            } else {
                throw new NoSuchElementException();
            }
        }


        @Override
        public void add(E e) {
            var tempNode = new ListNode(e);
            tempNode.next = rightNode;
            tempNode.prev = leftNode;
            if (leftNode != null) {
                leftNode.next = tempNode;
            }
            if (rightNode != null) {
                rightNode.prev = tempNode;
            }
            leftNode = tempNode;
            index++;
            nodeOnPreviousMove = null;
        }

        /** {@inheritDoc} */
        @Override
        public void remove() {
            if (nodeOnPreviousMove == null) {
                throw new IllegalStateException();
            } else {
                if (nodeOnPreviousMove.prev != null) {
                    nodeOnPreviousMove.prev.next = nodeOnPreviousMove.next;
                }
                if (nodeOnPreviousMove.next != null) {
                    nodeOnPreviousMove.next.prev = nodeOnPreviousMove.prev;
                }

                if (leftNode == nodeOnPreviousMove) {
                    leftNode = nodeOnPreviousMove.prev;
                    index--;
                } else {
                    rightNode = nodeOnPreviousMove.next;
                }

                nodeOnPreviousMove = null;
            }
        }

        @Override
        public void set(E e) {
            nodeOnPreviousMove.obj = e;
        }

        private ListNode rightNode;
        private ListNode leftNode;
        private ListNode nodeOnPreviousMove;
        private int index;
    }

    /** {@inheritDoc} */
    @Override
    public MyListIterator iterator() {
        return new MyListIterator(head);
    }

    /** A reference to the front node of the list */
    private ListNode head;
}