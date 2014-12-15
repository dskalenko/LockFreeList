package com.skillsup.lockfree.list;

import java.util.AbstractList;

/**
 * http://pirkelbauer.com/slides/opodis06.pdf
 * @param <E>
 */
public class LockFreeList<E> extends AbstractList<E>{


    @Override
    public E get(int index) {
       /* Integer p = 9;
        int pos = p + 2;
        System.out.println("backetSize " + (Integer.highestOneBit(pos)));
        System.out.println("index in backet " + (Integer.highestOneBit(pos) ^ pos));
        System.out.println("backetSize " + (Integer.highestOneBit(pos)));
        System.out.println(" numberOfBacket " + Integer.toBinaryString(Integer.highestOneBit(pos)) + " = " +
                (Integer.numberOfTrailingZeros(Integer.highestOneBit(pos)) - 1) );*/
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
