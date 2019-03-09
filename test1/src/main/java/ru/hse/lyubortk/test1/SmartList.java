package ru.hse.lyubortk.test1;

import java.util.*;

public class SmartList<E> extends AbstractList<E> implements List<E> {
    private int size;
    private Object data;

    public SmartList() {
        size = 0;
        data = null;
    }

    public SmartList(Collection<? extends E> collection) {
        this();

        if (collection.size() == 1) {
            size = 1;
            data = collection.toArray()[0];
        } else if (collection.size() > 1 && collection.size() <= 5) {
            size = collection.size();
            data = new Object[5];
            collection.toArray((Object[])data);
        } else if (collection.size() > 5) {
            size = collection.size();
            data = new ArrayList<E>(collection);
        }
    }

    @Override
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        if (size == 1) {
            return (E)data;
        } else if (size <= 5) {
            return (E)((Object[])data)[index];
        } else {
            return ((ArrayList<E>)data).get(index);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public E set(int index, E element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        if (size == 1) {
            E previousValue = get(index);
            data = element;
            return previousValue;
        } else if (size <= 5){
            E previousValue = get(index);
            ((Object[])data)[index] = element;
            return previousValue;
        } else {
            return ((ArrayList<E>)data).set(index, element);
        }
    }

    @Override
    public void add(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }

        if (size == 0) {
            data = element;
        } else if (size == 1) {
            var tempArrayList = new ArrayList<E>();
            tempArrayList.add((E)data);
            tempArrayList.add(index, element);
            copyArrayListToArray(tempArrayList);
        } else if (size < 5) {
            var tempArrayList = new ArrayList<E>(Arrays.asList((E[])data));
            tempArrayList.add(index, element);
            copyArrayListToArray(tempArrayList);
        } else if (size == 5) {
            data = new ArrayList<E>(Arrays.asList((E[])data));
            ((ArrayList<E>)data).add(index, element);
        } else {
            ((ArrayList<E>)data).add(index, element);
        }
        size++;
    }

    @Override
    public E remove(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException();
        }

        E previousObject;
        if (size == 1) {
            var temp = data;
            data = null;
            previousObject = (E)temp;
        } else if (size == 2) {
            var temp = ((Object[])data)[index];
            data = ((Object[])data)[index^1];
            previousObject = (E)temp;
        } else if (size <= 5) {
            var tempArrayList = new ArrayList<E>(Arrays.asList((E[])data));
            previousObject = tempArrayList.remove(index);
            copyArrayListToArray(tempArrayList);
        } else if (size == 6) {
            previousObject = ((ArrayList<E>)data).remove(index);
            copyArrayListToArray((ArrayList<E>)data);
        } else {
            previousObject = ((ArrayList<E>)data).remove(index);
        }

        size--;
        return previousObject;
    }

    private void copyArrayListToArray(ArrayList<E> tempArrayList) {
        var tempArray = tempArrayList.toArray();
        data = new Object[5];
        for (int i = 0; i < 5 && i < tempArray.length; i++) {
            ((Object[])data)[i] = tempArray[i];
        }
    }
}