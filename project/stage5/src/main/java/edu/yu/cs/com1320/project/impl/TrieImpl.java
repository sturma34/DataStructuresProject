package edu.yu.cs.com1320.project.impl;

import java.util.*;
import edu.yu.cs.com1320.project.Trie;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;

public class TrieImpl<Value> implements Trie<Value> {
    private static final int alphabetSize = 125; // extended ASCII
    private Node<Value> root; // root of trie
    private class Node<Value>
    {
        private Set<Value> valueSet = new HashSet<>();
        private Node[] links = new TrieImpl.Node[TrieImpl.alphabetSize];
    }

    public TrieImpl(){
        this.root = new Node<>();
    }


    /**
     * add the given value at the given key
     * @param key
     * @param val
     */
    public void put(String key, Value val) {
        if (key == null || val == null) {
            throw new IllegalArgumentException("Put a valid value into the trie.");
        }
        String lowerCaseKey = key.toLowerCase();
        //deleteAll the value from this key
        if (val == null) {
            this.deleteAll(lowerCaseKey);
        } else {
            this.root = this.put(this.root, lowerCaseKey, val, 0);
        }
    }

    private Node<Value> put(Node<Value> x, String key, Value val, int d) {
        //create a new node
        if (x == null) {
            x = new Node();
        }
        //we've reached the last node in the key,
        //set the value for the key and return the node
        if (d == key.length()){
            x.valueSet.add(val);
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        x.links[c] = this.put(x.links[c], key, val, d + 1);
        return x;
    }

    private Node<Value> get(String key){
        Node x = this.get(this.root, key, 0);
        return x;
    }

    private Node<Value> get(Node<Value> x, String key, int d) {
        //link was null - return null, indicating a miss
        if (x == null) {
            return null;
        }
        //we've reached the last node in the key,
        //return the node
        if (d == key.length()) {
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        return this.get(x.links[c], key, d + 1);
    }
    /**
     * get all exact matches for the given key, sorted in descending order.
     * Search is CASE INSENSITIVE.
     * @param key
     * @param comparator used to sort  values
     * @return a List of matching Values, in descending order
     */
    public List<Value> getAllSorted(String key, Comparator<Value> comparator){
        if(key == null){
            throw new IllegalArgumentException("Please enter a valid key.");
        }
        key = key.toLowerCase();
        Node<Value> x = this.get(this.root, key, 0);
        if (x == null) {
            return new ArrayList<>();
        }
        List<Value> values = new ArrayList<>(x.valueSet);
        values.sort(comparator);
        return values;
    }

    /**
     * get all matches which contain a String with the given prefix, sorted in descending order.
     * For example, if the key is "Too", you would return any value that contains "Tool", "Too", "Tooth", "Toodle", etc.
     * Search is CASE INSENSITIVE.
     * @param prefix
     * @param comparator used to sort values
     * @return a List of all matching Values containing the given prefix, in descending order
     */
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator){
        if(prefix == null){
            throw new IllegalArgumentException("Please enter a valid key.");
        }
        prefix = prefix.toLowerCase();
        Node<Value> x = this.get(this.root, prefix, 0);
        if (x == null) {
            return new ArrayList<>();
        }
        Set<Value> results = new HashSet<Value>();
        this.collect(x, new StringBuilder(prefix), results);
        List<Value> values = new ArrayList<>(results);
        values.sort(comparator);
        return values;
    }

    /**
     * Delete the subtree rooted at the last character of the prefix.
     * Search is CASE INSENSITIVE.
     * @param prefix
     * @return a Set of all Values that were deleted.
     */
    public Set<Value> deleteAllWithPrefix(String prefix){
        if(prefix == null){
            throw new IllegalArgumentException("Please enter a valid key.");
        }
        prefix = prefix.toLowerCase();
        Set<Value> results = new HashSet<Value>();
        //find node which represents the prefix
        Node<Value> x = this.get(this.root, prefix, 0);
        //collect keys under it
        if(x!= null) {
            this.collect(x, new StringBuilder(prefix), results);
        }
        x = null;
        return results;
    }

    //Test this method for including the prefix itself.
    private void collect(Node x,StringBuilder prefix,Set<Value> results) {
        //if this node has a value, add its key to the queue
        if (x != null && !x.valueSet.isEmpty()) {
            //add a string made up of the chars from //root to this node to the result set
            results.addAll(x.valueSet);
        }
        //visit each non-null child/link
        for (char c = 0; c < this.alphabetSize; c++) {
            if (x.links[c] != null) {
                //add child's char to the string
                prefix.append(c);
                this.collect(x.links[c], prefix, results);
                //remove the child's char to prepare for next loop iteration which will look at another character/link
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
    }

    /**
     * Delete all values from the node of the given key (do not remove the values from other nodes in the Trie)
     * @param key
     * @return a Set of all Values that were deleted.
     */
    public Set<Value> deleteAll(String key){
        if(key == null){
            throw new IllegalArgumentException("Please enter a valid key.");
        }
        key = key.toLowerCase();
        Node<Value> x = this.get(this.root, key, 0);
        if(x == null){
            return Collections.emptySet();
        }
        Set<Value> returnSet = new HashSet<>(x.valueSet);
        this.root = this.deleteAll(this.root, key, 0);
        return returnSet;
    }

    private Node<Value> deleteAll(Node x, String key, int d){
        if (x == null) {
            return null;
        }
        //we're at the node to delete - clear
        if (d == key.length()) {
            x.valueSet.clear();
        }
        //continue down the trie to the target node
        else {
            char c = key.charAt(d);
            x.links[c] = this.deleteAll(x.links[c], key, d + 1);
        }
        //this node has a val – do nothing, return the node
        if (!x.valueSet.isEmpty()) {
            return x;
        }
        //otherwise, check if subtrie rooted at x is completely empty
        for (int c = 0; c < this.alphabetSize; c++) {
            if (this.checkChildren(x.links[c]) || x == this.root) {
                return x; // not empty }
            }
            //empty - set this link to null in the parent
        }
        return null;
    }

    private boolean checkChildren(Node x){
        if (x == null){
            return false;
        } else {
            if (!x.valueSet.isEmpty()){
                return true;
            } else {
                boolean check;
                for (int c = 0; c < this.alphabetSize; c++) {
                    check = this.checkChildren(x.links[c]);
                    if(check){
                        return true;
                    }
                    //empty - set this link to null in the parent
                }
            }
        }
        return true;
    }

    /**
     * Remove the given value from the node of the given key (do not remove the value from other nodes in the Trie)
     * @param key
     * @param val
     * @return the value which was deleted. If the key did not contain the given value, return null.
     */
    public Value delete(String key, Value val){
        if(key == null){
            throw new IllegalArgumentException("Please enter a valid key.");
        }
        key = key.toLowerCase();
        Node<Value> x = this.get(key);
        if(x.valueSet.contains(val)){
            HashSet<Value> checkSet = new HashSet<>(x.valueSet);
            for (Value valCheck : checkSet){
                if(valCheck == val){
                    this.root = delete(this.root, key, val, 0);
                }
            }
            return val;
        } else {
            return null;
        }
    }

    private Node<Value> delete(Node x, String key, Value val, int d) {
        if (x == null) {
            return null;
        }
        //we're at the node to delete - remove the val from the valset
        if (d == key.length()) {
            x.valueSet.remove(val);
        }
        //continue down the trie to the target node
        else {
            char c = key.charAt(d);
            x.links[c] = this.delete(x.links[c], key, val, d + 1);
        }
        //this node has a val – do nothing, return the node
        if (!x.valueSet.isEmpty()) {
            return x;
        }
        //otherwise, check if subtrie rooted at x is completely empty
        for (int c = 0; c < this.alphabetSize; c++) {
            if (this.checkChildren(x.links[c]) || x == this.root) {
                return x; // not empty }
            }
            //empty - set this link to null in the parent
        }
        return null;
    }
}
