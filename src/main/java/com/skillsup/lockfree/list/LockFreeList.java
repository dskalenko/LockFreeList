package com.skillsup.lockfree.list;

import java.util.AbstractList;


public class LockFreeList<E> extends AbstractList<E>{


    @Override
    public E get(int index) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    public boolean add(E element){
        return false;
    }

    public void add(int index, E element){

    }
}
