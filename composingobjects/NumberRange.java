/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.composingobjects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *Number Range class that doesn't sufficiently protect its invariants
 * 
 * @author vijay
 */
public class NumberRange {
    private final AtomicInteger lower = new AtomicInteger();
    private final AtomicInteger upper = new AtomicInteger();
    
    public void setLower(int i){
        if(i<upper.get()){
            lower.set(i);
        }else{
            throw new IllegalArgumentException();
        }
    }
    
    public void setUpper(int i){
        if(i>lower.get()){
            upper.set(i);
        }else{
            throw new IllegalArgumentException();
        }
    }
    
    public boolean isInRange(int i){
        return (i>=lower.get() && i<=upper.get());
    }
}
