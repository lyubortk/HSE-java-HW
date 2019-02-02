package ru.hse.inclass.list;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class MyListTest {

    @BeforeEach
    void initializeList() {
        list = new MyList<>();
    }

   @Test
    void iteratorHasNext() {
        Iterator<Integer> it = list.iterator();
        assertFalse(it.hasNext());

        list.add(1);
        it = list.iterator();
        assertTrue(it.hasNext());
        it.next();
        assertFalse(it.hasNext());
    }

    @Test
    void iteratorNext() {
        list.add(3);
        list.add(2);
        list.add(1);

        Iterator it = list.iterator();
        assertEquals(3, it.next());
        assertEquals(2, it.next());
        assertEquals(1, it.next());
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void iteratorRemoveFirst() {
        list.add(2);
        list.add(1);

        Iterator it = list.iterator();
        it.next();
        it.remove();

        it = list.iterator();
        assertEquals(1, it.next());
        assertFalse(it.hasNext());
    }
    @Test
    void test() {
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);

        ListIterator<Integer> it = list.listIterator();







        assertEquals(Integer.valueOf(1), it.next());
        assertEquals(Integer.valueOf(2), it.next());
        assertEquals(Integer.valueOf(3), it.next());
        assertEquals(Integer.valueOf(3), it.previous());
        it.remove();
        assertEquals(Integer.valueOf(2), it.previous());
        assertEquals(Integer.valueOf(2), it.next());
        assertEquals(Integer.valueOf(4), it.next());
        it.add(3);
        assertEquals(Integer.valueOf(3), it.previous());
    }
/*
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
/*
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
*/
    private MyList<Integer> list;
}