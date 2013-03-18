/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.composingobjects;

import java.util.List;
import java.util.ListIterator;

/**
 * Implementing put if absent with composition
 * 
 * ImprovedList adds an additional level of locking using its own intrinsic lock. 
 * It does not care whether the underlying List  is  thread safe, because  it 
 * provides  its own consistent  locking that provides thread safety even  if 
 * the List  is not thread safe  or  changes  its  locking  implementation.While  
 * the  extra  layer  of  synchronization  may  add  some  small performance 
 * penalty,[7]  the  implementation  in  ImprovedList  is  less  fragile  than  
 * attempting  to  mimic  the  locking strategy of another object. In effect, 
 * we've used the Java monitor pattern to encapsulate an existing List, and this 
 * is guaranteed to provide thread safety so long as our class holds the only 
 * outstanding reference to the underlying List.
 * 
 * @author vijay
 */
public class ImprovedList<T> implements List<T>{
    
    private final List<T> list;
    
    public ImprovedList(List<T> list){
        this.list = list;
    }
    
    public synchronized void putIfAbsent(E e){
        if(!list.contains(e)){
            list.add(e);
        }
    }

    
}
