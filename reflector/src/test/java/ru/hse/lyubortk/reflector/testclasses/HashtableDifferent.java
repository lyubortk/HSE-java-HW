package ru.hse.lyubortk.reflector.testclasses;

import java.util.Iterator;

/** A simple Hash table class. Both key and stored value are {@link String}s.
 *  Hash table implements separate chaining method and uses {@link MyList}
 *  for buckets. */
public class HashtableDifferent {

    /** Number of pairs (key, mapped value) stored in the hash table */
    private int size;

    /** Number of allocated buckets */
    private int bucketsNumber;

    /** Array of buckets (has {@link HashtableDifferent#bucketsNumber} size) */
    private MyList[] bucketArray;

    public void dummyMethod(int a, int b) {

    }

    /** Default constructor which sets initial number of buckets to 16. */
    public HashtableDifferent() {
        this(16);
    }

    /** A constructor with initial number of buckets as argument.
     * @param buckets initial number of buckets */
    public HashtableDifferent(int buckets) {
        bucketsNumber = buckets;
        bucketArray = new MyList[buckets];
        for (int i = 0; i< buckets; i++) {
            bucketArray[i] = new MyList();
        }
    }

    /** Returns number of items (pairs of key and mapped value) stored in the hash table.
     * @return number of stored elements */
    public int size(){
        return size;
    }

    /** Checks whether some value is stored by particular key
     * @param key a non-null key to search for mapped value
     * @return whether some value is mapped to the key
     * @throws IllegalArgumentException if key is null*/

    public boolean contains(String key){
        int bucket = getBucketIndex(key);
        for (Object o : bucketArray[bucket]) {
            var curPair = (StringPair)o;
            if (curPair.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    /** Returns value mapped to particular key or null if the key is not in the hash table
     * @param key a non-null key to search for mapped value.
     * @return mapped value or null
     * @throws IllegalArgumentException if key is null*/
    public String get(String key){
        int bucket = getBucketIndex(key);
        for (Object o : bucketArray[bucket]) {
            var curPair = (StringPair)o;
            if (curPair.key.equals(key)) {
                return curPair.val;
            }
        }
        return null;
    }


    /** Removes pair of (key, mapped value) from hash table.
     * @param key a non-null key to search mapped value
     * @return a value that was mapped to this key or null if there was none
     * @throws IllegalArgumentException if key is null*/
    public String remove(String key){
        int bucket = getBucketIndex(key);
        String foundValue = null;
        for (Iterator it = bucketArray[bucket].iterator(); it.hasNext();) {
            var lastPair = (StringPair)it.next();
            if (lastPair.key.equals(key)){
                foundValue = lastPair.val;
                it.remove();
                size--;
                break;
            }
        }
        checkBucketsNumber();
        return foundValue;
    }

    /** Removes everything from hash table and sets number of buckets to a default value.
     *  In other words, method makes the hash table identical to one constructed
     *  by default constructor */
    public void clear(){
        copyFrom(new HashtableDifferent());
    }

    /** Checks whether the number of elements in hash table is greater than
     *  the half of the number of buckets and reallocates memory (doubling the number of buckets)
     *  if such situation occurs. */
    private void checkBucketsNumber() {
        if (size * 2 > bucketsNumber) {
            var newHashtable = new HashtableDifferent(bucketsNumber * 2);
            //copyContentTo(newHashtable);
            copyFrom(newHashtable);
        }
    }


    /** Makes hash table identical to one given as argument. Method simply assigns all
     * fields to ones of argument table.
     * @param table a hash table to copy fields from */
    private void copyFrom(HashtableDifferent table) {
        size = table.size;
        bucketsNumber = table.bucketsNumber;
        bucketArray = table.bucketArray;
    }


    /** Calculates index in bucket array.
     * @param key a non-null key {@link String} to calculate its {@link String#hashCode}
     * @return number of bucket in which keys with that {@link String#hashCode} are stored
     * @throws IllegalArgumentException if key is null*/
    private int getBucketIndex(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }

        int hashCode = key.hashCode() % bucketsNumber;
        if (hashCode < 0) {
            hashCode += bucketsNumber;
        }
        return hashCode;
    }

    /** Inner class which represents pair of (key, mapped value). */
    private static class StringPair {
        private StringPair(String keyStr, String valStr) {
            key = keyStr;
            val = valStr;
        }
        private String key;
        private String val;
    }
}
