/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.buildingblocks;

/**
 *
 * @author vijay
 */
public class BoundedSemaphore {
    int signal;
    int bound;
    
    BoundedSemaphore(int bound){
        this.bound = bound;
    }
    
    synchronized void acquire(){
        while(signal==bound){
            try{
                this.wait();
            }
            catch(InterruptedException ie){
            }
            System.out.println("Producer Notified By Consumer. Yippee!!");
        }        
        this.signal++;
        this.notify();
    }
            
    synchronized void release(){
        while(signal==0){
            try{
                this.wait();
            }
            catch(InterruptedException ie){
            }
            System.out.println("Consumer Notified By Producer. Yippee!!");
            
        }
        this.signal--;
        this.notify();                
    }  
    
}
