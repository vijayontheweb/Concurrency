/*
 * If you are tempted to register an event listener or start a thread from a 
 * constructor, you can avoid the improper construction by using private 
 * constructor and a public factory method
 */
package concurrency.sharingobject;

import java.util.EventListener;

/**
 *
 * @author vijay
 */
public class SafeListener {
    private final EventListener listener;
    
    private SafeListener(){
        listener = new EventListener() {
            public void onEvent(){
                doSomething(e);
            }
        };
    }
    
    public static SafeListener newInstance(){
        return new SafeListener();
    }
}
