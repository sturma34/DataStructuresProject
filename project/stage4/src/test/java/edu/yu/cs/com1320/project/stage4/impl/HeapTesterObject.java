package edu.yu.cs.com1320.project.stage4.impl;

public class HeapTesterObject implements Comparable<HeapTesterObject>{

    private String string;

    public HeapTesterObject(String string){
        this.string = string;
    }

    public void changeString(String string){
        this.string = string;
    }

    public String getString(){
        return this.string;
    }

    @Override
    public int compareTo(HeapTesterObject o) {
        return Integer.compare(this.getString().length(), o.getString().length());
    }
}
