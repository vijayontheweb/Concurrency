/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.composingobjects;

/**
 * Illustrates Java Monitor Pattern in which any lock object could be used to 
 * guard an object's state so long as it is used consistently
 * 
 * @author vijay
 */

class Widget{}

public class PrivateLock {
    private final Object myLock = new Object();
    private Widget myWidget;
    
    public void someMethod(Widget widget){
        synchronized(myLock){
            //doSomething..like;
            myWidget = widget;
            
        }
    }
    
}
