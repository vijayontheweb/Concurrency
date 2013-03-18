/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.composingobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementing put if absent with client side locking
 * 
 * To make this approach work, we have to use the same lock that the List uses 
 * by using client  side locking or external locking. Client side locking  
 * entails guarding  client code that uses some object X with the lock X uses to 
 * guard  its own state. In order to use client side locking, you must know what 
 * lock X uses.
 * 
 * @author vijay 
 * ThreadSafe
 */
public class ListHelper<E> {

    public List<E> list = Collections.synchronizedList(new ArrayList<E>());

    public void putIfAbsent(E e) {
        synchronized (list) {
            if (list.contains(e)) {
                list.add(e);
            }
        }
    }
}
