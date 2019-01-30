package ru.hse.inclass.list;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class MyListTest {

    @BeforeEach
    void initializeList() {
        list = new MyList();
    }

    @Test
    void iteratorHasNext() {
        Iterator it = list.iterator();
        assertFalse(it.hasNext());

        list.insertObject(1);
        it = list.iterator();
        assertTrue(it.hasNext());
        it.next();
        assertFalse(it.hasNext());
    }

    @Test
    void iteratorNext() {
        list.insertObject(1);
        list.insertObject(2);
        list.insertObject(3);

        Iterator it = list.iterator();
        assertEquals(3, it.next());
        assertEquals(2, it.next());
        assertEquals(1, it.next());
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void iteratorRemoveFirst() {
        list.insertObject(1);
        list.insertObject(2);
        Iterator it = list.iterator();
        it.next();
        it.remove();

        it = list.iterator();
        assertEquals(1, it.next());
        assertFalse(it.hasNext());
    }

    @Test
    void iteratorRemoveLast() {
        list.insertObject(1);
        list.insertObject(2);
        Iterator it = list.iterator();
        it.next();
        it.next();
        it.remove();

        it = list.iterator();
        assertEquals(2, it.next());
        assertFalse(it.hasNext());
    }

    @Test
    void iteratorRemoveInMiddle() {
        list.insertObject(1);
        list.insertObject(2);
        list.insertObject(3);
        Iterator it = list.iterator();
        it.next();
        it.next();
        it.remove();

        it = list.iterator();
        assertEquals(3, it.next());
        assertEquals(1, it.next());
        assertFalse(it.hasNext());
    }

    @Test
    void insertObject() {
        Iterator it = list.iterator();
        assertFalse(it.hasNext());

        list.insertObject(1);
        list.insertObject(2);
        list.insertObject(3);

        it = list.iterator();
        assertEquals(3, it.next());
        assertEquals(2, it.next());
        assertEquals(1, it.next());
        assertFalse(it.hasNext());
    }

    private MyList list;
}