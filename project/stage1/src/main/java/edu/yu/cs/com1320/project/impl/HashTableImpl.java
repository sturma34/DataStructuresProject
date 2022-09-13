package edu.yu.cs.com1320.project.impl;


import edu.yu.cs.com1320.project.HashTable;

import java.util.List;

public class HashTableImpl <Key, Value> implements HashTable<Key, Value> {
    private class ListNode<Key, Value> {
        private Key key;
        private Value value;
        private ListNode<Key, Value> next;

        /**
         * create a node that will contain the key value pair and be stored in the linked list.
         *
         * @param k the key.
         * @param v the value.
         * @param n the next node in the LinkedList. Null if this is the only item in teh list.
         */
        ListNode(Key k, Value v, ListNode<Key, Value> n) {
            if (k == null) {
                throw new IllegalArgumentException("You have not entered a key.");
            }
            this.key = k;
            this.value = v;
            this.next = n;
        }

        private void assignValue(Value value) {
            this.value = value;
        }

        private void assignNext(ListNode<Key, Value> next) {
            this.next = next;
        }

    }

    private ListNode<Key, Value>[] table;

    public HashTableImpl() {
        this.table = new ListNode[5];
    }

//    protected int getArrayLength(){
//        return this.table.length;
//    }
    private int hashFunction(Key key) {
        return (key.hashCode() & 0x7fffffff) % this.table.length;
    }

    /**
     * @param k the key whose value should be returned
     * @return the value that is stored in the HashTable for k, or null if there is no such key in the table
     */
    public Value get(Key k) {
        int index = this.hashFunction(k);
        ListNode<Key, Value> current = this.table[index];
        if (current == null) {
            return null;
        } else {
            while (current != null && current.key != k) {
                current = current.next;
            }
            if (current == null){
                return null;
            } else {
                Value value = current.value;
                return value;
            }
        }
    }

    private ListNode<Key, Value> findNodeWithKey(Key k) {
        int index = this.hashFunction(k);
        ListNode<Key, Value> current = this.table[index];
        if (current == null){
            return null;
        }
        if (current.next == null && current.key != k) {
            return null;
        }
        while (current != null && current.key != k){
            current = current.next;
        }
        if (current != null && current.key == k) {
            return current;
        } else {
            return null;
        }
    }

    /**
     * @param k the key at which to store the value
     * @param v the value to store.
     *          To delete an entry, put a null value.
     * @return if the key was already present in the HashTable, return the previous value stored for the key. If the key was not already present, return null.
     */
    public Value put(Key k, Value v) {
        if (k == null){
            return null;
        }
        ListNode<Key,Value> node = this.findNodeWithKey(k);
        Value returnValue = null;
        if (node == null){
            int index = this.hashFunction(k);
            ListNode<Key, Value> old = this.table[index];
            ListNode<Key, Value> newHead = new ListNode<>(k, v, old);
            this.table[index] = newHead;
        } else {
            returnValue = node.value;
            node.assignValue(v);
        }
        return returnValue;
    }

    private void deleteNode(Key keyForDeletion) {
        int index = this.hashFunction(keyForDeletion);
        ListNode<Key, Value> current = this.table[index];
        if (current.key == keyForDeletion){
            ListNode<Key, Value> newHead = current.next;
            current.assignNext(null);
            this.table[index] = newHead;
        }
        while(current.next != null){
            if (current.next.key == keyForDeletion){
                current.assignNext(current.next.next);
            } else {
                current = current.next;
            }
        }
    }
}
