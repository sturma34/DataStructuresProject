package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl<T> implements Stack<T> {
    private class StackNode<T> {
        private T value;
        private StackNode lowerNode;

        StackNode(T value, StackNode lower) {
            if (value == null) {
                throw new IllegalArgumentException("You have not entered a key.");
            }
            this.value = value;
            this.lowerNode = lower;
        }
    }
    StackNode<T> top;
    int elementCount;
    public StackImpl(){
        elementCount = 0;
    }
    /**
     * @param element object to add to the Stack
     */
    public void push(T element){
        if (element == null){
            throw new IllegalArgumentException("You cannot push a null value.");
        }
        StackNode<T> newNode = new StackNode(element, this.top);
        this.top = newNode;
        elementCount++;
    }

    /**
     * removes and returns element at the top of the stack
     * @return element at the top of the stack, null if the stack is empty
     */
    public T pop(){
        if (this.top == null){
            return null;
        } else {
            T returnValue = this.top.value;
            this.top = this.top.lowerNode;
            elementCount--;
            return returnValue;
        }
    }

    /**
     *
     * @return the element at the top of the stack without removing it
     */
    public T peek(){
        if (this.elementCount == 0){
            return null;
        } else {
            return this.top.value;
        }
    }

    /**
     *
     * @return how many elements are currently in the stack
     */
    public int size(){
        return this.elementCount;
    }
}
