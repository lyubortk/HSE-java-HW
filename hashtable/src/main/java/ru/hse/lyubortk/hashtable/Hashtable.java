package ru.hse.lyubortk.hashtable;

import java.util.Iterator;

/** A simple Hash table class. Both key and stored value are {@link String}s.
 *  Hash table implements separate chaining method and uses {@link MyList}
 *  for buckets. */
public class Hashtable {
    /** Default constructor which sets initial number of buckets to 16. */
    public Hashtable() {
        this(16);
    }

    /** A constructor with initial number of buckets as argument.
     * @param buckets initial number of buckets */
    public Hashtable(int buckets) {
        bucketsNumber = buckets;
        arr = new MyList[buckets];
        for (int i = 0; i< buckets; i++) {
            arr[i] = new MyList();
        }
    }

    /** Returns number of items (pairs of key and mapped value) stored in the hash table.
     * @return number of stored elements */
    public int size(){
        return size;
    }

    /** Checks whether some value is stored by particular key
     * @param key a key to search mapped value
     * @return whether some value is mapped to the key*/
    public boolean contains(String key){
        int bucket = getBucketIndex(key);
        for (Object o : arr[bucket]) {
            var cur = (StringPair)o;
            if (cur.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    /** Returns value mapped to particular key or null if the key is not in the hash table
     * @param key a key to search mapped value.
     * @return mapped value or null */
    public String get(String key){
        int bucket = getBucketIndex(key);
        for (Object o : arr[bucket]) {
            var cur = (StringPair)o;
            if (cur.key.equals(key)) {
                return cur.val;
            }
        }
        return null;
    }

    /** Maps value to key. Both arguments have to be non-null.
     * @param key a key to map value to
     * @param value a value to be mapped to key
     * @return previous value stored by that key or null if there was none
     * @exception IllegalArgumentException if either any of arguments is null */
    public String put(String key, String value){
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }

        String prevValue = remove(key);
        int bucket = getBucketIndex(key);
        arr[bucket].insertObject(new StringPair(key, value));
        size++;
        checkBucketsNumber();
        return prevValue;
    }

    /** Removes pair of (key, mapped value) from hash table.
     * @param key a key to search mapped value
     * @return a value that was mapped to this key or null if there was none*/
    public String remove(String key){
        int bucket = getBucketIndex(key);
        String foundValue = null;
        Iterator it = arr[bucket].iterator();
        while (it.hasNext()) {
            var last = (StringPair)it.next();
            if (last.key.equals(key)){
                foundValue = last.val;
                it.remove();
                size--;
                break;
            }
        }
        checkBucketsNumber();
        return foundValue;
    }

    /** Removes everything from hash table and sets number of buckets to a default value.
     *  In other words, method makes the hash table identical to one constructed.
     *  by default constructor */
    public void clear(){
        copyFrom(new Hashtable());
    }

    /** Checks whether the number of elements in hash table is greater than
     *  the half of the number of buckets and reallocates memory (doubling the number of buckets)
     *  if such situation occurs. */
    private void checkBucketsNumber() {
        if (size * 2 > bucketsNumber) {
            var newHashtable = new Hashtable(bucketsNumber*2);
            copyContentTo(newHashtable);
            copyFrom(newHashtable);
        }
    }

    /** Makes hash table identical to one given as argument. Method simply assigns all
     * fields to ones of argument table.
     * @param table a hash table to copy fields from */
    private void copyFrom(Hashtable table) {
        size = table.size;
        bucketsNumber = table.bucketsNumber;
        arr = table.arr;
    }

    /** Adds (by calling method {@link Hashtable#put}) every pair of (key, value) stored in the
     * hash table to another table.
     * @param table another hash table to copy all stored data to */
    private void copyContentTo(Hashtable table) {
        for (int i = 0; i < bucketsNumber; i++) {
            for (Object o : arr[i]) {
                var pair = (StringPair) o;
                table.put(pair.key, pair.val);
            }
        }
    }

    /** Calculates index in bucket array.
     * @param key a key {@link String} to calculate its {@link String#hashCode}
     * @return number of bucket in which keys with that {@link String#hashCode} are stored */
    private int getBucketIndex(String key) {
        int temp = key.hashCode() % bucketsNumber;
        return (temp >= 0 ? temp : temp + bucketsNumber);
    }

    /** Inner class which represents pair of (key, mapped value). */
    private class StringPair {
        private StringPair(String keyStr, String valStr) {
            key = keyStr;
            val = valStr;
        }
        private String key;
        private String val;
    }

    /** Number of pairs (key, mapped value) stored in the hash table */
    private int size;

    /** Number of allocated buckets */
    private int bucketsNumber;

    /** Array of buckets (has {@link Hashtable#bucketsNumber} size) */
    private MyList[] arr;
}
