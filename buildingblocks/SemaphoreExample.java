/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.buildingblocks;

import java.util.concurrent.Semaphore;

/**
 *
 * @author vijay
 */
public class SemaphoreExample {
    Semaphore semaphore = new Semaphore(10);
    int i;

    synchronized void callCommonResource() throws InterruptedException{
        semaphore.acquire();
        i++;
        System.out.println(i);
        semaphore.release();
    }

    public static void main(String[] args) {
        final SemaphoreExample se = new SemaphoreExample();
        Runnable r = new Runnable() {

            public void run() {
                while (true) {
                    try {                        
                        se.callCommonResource();
                    } catch (InterruptedException ie) {
                    }
                    
                }
            }
        };
        Thread t1 = new Thread(r);
        Thread t2 = new Thread(r);
        
        t1.start();
        t2.start();
        
    }
}
