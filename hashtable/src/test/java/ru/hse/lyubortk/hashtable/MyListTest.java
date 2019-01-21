package ru.hse.lyubortk.hashtable;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class MyListTest {

    @Test
    void iteratorHasNext() {
        MyList list = new MyList();
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
        MyList list = new MyList();
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
        MyList list = new MyList();
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
        MyList list = new MyList();
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
        MyList list = new MyList();
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
        MyList list = new MyList();
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
}