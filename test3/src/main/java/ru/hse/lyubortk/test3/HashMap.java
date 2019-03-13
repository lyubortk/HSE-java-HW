package ru.hse.lyubortk.test3;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A simple HashMap class. May contain null as key and stored value.
 * Hash table implements separate chaining method.
 */
public class HashMap<K, V> extends AbstractMap<K, V> {
    private int size;
    private int bucketsNumber;
    private HashMapEntry<K, V>[] bucketArray;
    private HashMapEntry<K, V> headEntry;
    private HashMapEntry<K, V> tailEntry;

    /** Default constructor which sets initial number of buckets to 16. */
    public HashMap() {
        this(16);
    }

    /**
     * A constructor with initial number of buckets as argument.
     * @param buckets initial number of buckets *
     */
    public HashMap(int buckets) {
        bucketsNumber = buckets;
        @SuppressWarnings("unchecked")
        HashMapEntry<K, V>[] tempArray = new HashMapEntry[buckets];
        bucketArray = tempArray;
    }

    /**
     * Checks whether some value is stored by particular key
     * @param key key to search for mapped value
     * @return whether some value is mapped to the key
     */
    @Override
    public boolean containsKey(@Nullable Object key) {
        int bucket = getBucketIndex(key);
        for (var entry = bucketArray[bucket]; entry != null; entry = entry.next) {
            if (Objects.equals(entry.getKey(), key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns value mapped to particular key or null if the key is not in the hash table
     * @param key a key to search for mapped value.
     * @return mapped value or null
     */
    @Override
    public V get(@Nullable Object key) {
        int bucket = getBucketIndex(key);
        for (var entry = bucketArray[bucket]; entry != null; entry = entry.next) {
            if (Objects.equals(entry.getKey(), key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Maps value to key.
     * @param key a non-null key to map value to
     * @param value a non-null value to be mapped to key
     * @return previous value stored by that key or null if there was none
     */
    @Override
    public V put(@Nullable K key, @Nullable V value) {
        V prevValue = remove(key);
        int bucket = getBucketIndex(key);
        var entry = new HashMapEntry<>(key, value);
        addEntryToBucket(bucket, entry);
        addEntryToEntryList(entry);
        size++;
        checkBucketsNumber();
        return prevValue;
    }

    /**
     * Removes pair of (key, mapped value) from hash table.
     * @param key a non-null key to search mapped value
     * @return a value that was mapped to this key or null if there was none
     */
    @Override
    public V remove(@Nullable Object key) {
        int bucket = getBucketIndex(key);
        V foundValue = null;
        for (var entry = bucketArray[bucket]; entry != null; entry = entry.next) {
            if (Objects.equals(entry.getKey(), key)){
                foundValue = entry.getValue();
                deleteEntryFromBucket(bucket, entry);
                deleteEntryFromEntryList(entry);
                size--;
                break;
            }
        }
        checkBucketsNumber();
        return foundValue;
    }

    /**
     * Removes everything from hash table and sets number of buckets to a default value.
     * In other words, method makes the hash table identical to one constructed
     * by default constructor
     */
    @Override
    public void clear() {
        makeIdenticalTo(new HashMap<>());
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new SetView();
    }

    private void addEntryToEntryList(HashMapEntry<K, V> entry) {
        if (tailEntry == null) {
            tailEntry = entry;
            headEntry = entry;
        } else {
            tailEntry.after = entry;
            entry.before = tailEntry;
            tailEntry = entry;
        }
    }

    private void deleteEntryFromEntryList(HashMapEntry<K, V> entry) {
        if (entry.before != null) {
            entry.before.after = entry.after;
        }
        if (entry.after != null) {
            entry.after.before = entry.before;
        }

        if (tailEntry == entry) {
            tailEntry = entry.before;
        }
        if (headEntry == entry) {
            headEntry = entry.after;
        }
    }

    private void addEntryToBucket(int bucketNumber, HashMapEntry<K, V> entry) {
        if (bucketArray[bucketNumber] != null) {
            entry.next = bucketArray[bucketNumber];
            bucketArray[bucketNumber].prev = entry;
        }
        bucketArray[bucketNumber] = entry;
    }

    private void deleteEntryFromBucket(int bucketNumber, HashMapEntry<K, V> entry) {
        if (entry.prev != null) {
            entry.prev.next = entry.next;
        }
        if (entry.next != null) {
            entry.next.prev = entry.prev;
        }
        if (bucketArray[bucketNumber] == entry) {
            bucketArray[bucketNumber] = entry.next;
        }
    }

    /**
     * Checks whether the number of elements in hash table is greater than
     * the half of the number of buckets and reallocates memory (doubling the number of buckets)
     * if such situation occurs.
     */
    private void checkBucketsNumber() {
        if (size * 2 > bucketsNumber) {
            var newHashtable = new HashMap<K, V>(bucketsNumber * 2);
            copyContentTo(newHashtable);
            makeIdenticalTo(newHashtable);
        }
    }

    /**
     * Makes hash table identical to one given as argument. Method simply assigns all
     * fields to ones of argument table.
     * @param table a hash table to copy fields from
     */
    private void makeIdenticalTo(HashMap<K, V> table) {
        size = table.size;
        bucketsNumber = table.bucketsNumber;
        bucketArray = table.bucketArray;
    }

    /**
     * Adds (by calling method {@link HashMap#put}) every pair of (key, value) stored in the
     * hash table to another table.
     * @param table another hash table to copy all stored data to
     */
    private void copyContentTo(HashMap<K, V> table) {
        for (var entry : entrySet()) {
            table.put(entry.getKey(), entry.getValue());
        }
    }

    /** Calculates index in bucket array. */
    private int getBucketIndex(@Nullable Object key) {
        int hashCode = Objects.hashCode(key) % bucketsNumber;
        if (hashCode < 0) {
            hashCode += bucketsNumber;
        }
        return hashCode;
    }

    private static class HashMapEntry<K, V> extends SimpleEntry<K, V> {
        HashMapEntry<K, V> next;
        HashMapEntry<K, V> prev;

        HashMapEntry<K, V> after;
        HashMapEntry<K, V> before;

        HashMapEntry(K key, V value) {
            super(key, value);
        }
    }

    private class SetView extends AbstractSet<Entry<K, V>> {
        @Override
        public int size() {
            return size;
        }

        @Override
        public @NotNull Iterator<Entry<K, V>> iterator() {
            return new SetViewIterator(headEntry);
        }

        private class SetViewIterator implements Iterator<Entry<K, V>> {
            private HashMapEntry<K, V> nextEntry;

            private SetViewIterator(HashMapEntry<K, V> nextEntry) {
                this.nextEntry = nextEntry;
            }

            @Override
            public boolean hasNext() {
                return nextEntry != null;
            }

            @Override
            public Entry<K, V> next() {
                if (nextEntry != null) {
                    Entry<K, V> entry = nextEntry;
                    nextEntry = nextEntry.after;
                    return entry;
                } else {
                    throw new NoSuchElementException();
                }
            }
        }
    }
}

