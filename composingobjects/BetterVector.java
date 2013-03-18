/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.composingobjects;

import java.util.Vector;

/**
 * Extending vector to have a put-if-absent method
 * 
 * Extension is more fragile than adding code directly to a class, because the 
 * implementation of the synchronization policy is  now  distributed  over  
 * multiple,  separately  maintained  source  files.  If  the  underlying  class  
 * were  to  change  its synchronization  policy by choosing a  different  lock 
 * to guard  its  state  variables, the  subclass would  subtly and  silently 
 * break,  because  it  no  longer  used  the  right  lock  to  control  
 * concurrent  access  to  the  base  class  state.  (The synchronization policy 
 * of Vector is fixed by its specification, so BetterVector would not suffer 
 * from this problem.)
 * 
 * @author vijay
 */
public class BetterVector<E> extends Vector<E>{
    public synchronized void putIfAbsent(E e){
        if(!contains(e)){
            add(e);
        }
    }
    
}
