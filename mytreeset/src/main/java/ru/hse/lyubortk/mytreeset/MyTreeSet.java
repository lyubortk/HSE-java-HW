package ru.hse.lyubortk.mytreeset;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public interface MyTreeSet<E> extends Set<E> {

    /** {@link TreeSet#descendingIterator()} **/
    Iterator<E> descendingIterator();

    /** {@link TreeSet#descendingSet()} **/
    MyTreeSet<E> descendingSet();


    /** {@link TreeSet#first()} **/
    E first();

    /** {@link TreeSet#last()} **/
    E last();

    /** {@link TreeSet#lower} **/
    E lower(E e);

    /** {@link TreeSet#floor} **/
    E floor(E e);


    /** {@link TreeSet#ceiling} **/
    E ceiling(E e);

    /** {@link TreeSet#higher} **/
    E higher(E e);
}