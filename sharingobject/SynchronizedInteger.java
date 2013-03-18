/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.sharingobject;

/**
 * ThreadSafe mutable Integer holder
 * @author vijay
 */

public class SynchronizedInteger {
   private int value;

    public synchronized int getValue() {
        return value;
    }

    public synchronized void setValue(int value) {
        this.value = value;
    }
   
}
