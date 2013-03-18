/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.sharingobject;

/**
 * NotThreadSafe mutable Integer holder
 * @author vijay
 */

public class MutableInteger {
   private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
   
}
