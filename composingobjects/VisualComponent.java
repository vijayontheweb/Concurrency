/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.composingobjects;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Delegating Thread Safety to Multiple Underlying State Variables. 
 * 
 * VisualComponent uses a CopyOnWriteArrayList to store each listener list; 
 * this is a thread safe List implementation particularly  suited  for managing  
 * listener  lists  (see  Section  5.2.3).  Each List  is  thread safe, and  
 * because  there  are no constraints  coupling  the  state  of  one  to  the  
 * state  of  the  other,  VisualComponent  can  delegate  its  thread  safety 
 * responsibilities to the underlying mouseListeners and keyListeners objects. 
 * 
 * @author vijay
 */
public class VisualComponent {
    
    public final List<MouseListener> mouseListeners = new CopyOnWriteArrayList<MouseListener>();
    public final List<KeyListener> keyListeners = new CopyOnWriteArrayList<KeyListener>();
    
    public void addMouseListener(MouseListener m) {
        mouseListeners.add(m);
    }
    
    public void removeMouseListener(MouseListener m) {
        mouseListeners.remove(m);
    }
    
    public void addKeyListener(KeyListener k) {
        keyListeners.add(k);
    }
    
    public void removeKeyListener(KeyListener k) {
        keyListeners.remove(k);
    }
}

class MouseListener {
}

class KeyListener {
}
